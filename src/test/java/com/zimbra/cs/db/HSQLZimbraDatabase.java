package com.zimbra.cs.db;

/**
 * Zimbra Collaboration Suite Server
 */

import com.zimbra.common.service.ServiceException;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.hsqldb.cmdline.SqlFile;

public final class HSQLZimbraDatabase extends Db
{
  //
  // Populates ZIMBRA and MBOXGROUP1 schema.
  //
  public static void createDatabase() throws Exception {
    createDatabase(false);
  }

  //
  // Populates ZIMBRA and MBOXGROUP1 schema.
  // @param zimbraServerDir the directory that contains the ZimbraServer project
  // @throws Exception
  //
  public static void createDatabase(boolean isOctopus) throws Exception {
    PreparedStatement stmt = null;
    ResultSet rs = null;
    com.zimbra.cs.db.DbPool.DbConnection  conn = DbPool.getConnection();

    try {
      stmt = conn.prepareStatement("SET DATABASE SQL SYNTAX MYS TRUE");
      stmt.execute();
      stmt = conn.prepareStatement("SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?");
      stmt.setString(1, "ZIMBRA");
      rs = stmt.executeQuery();
      if (rs.next() && rs.getInt(1) > 0) {
        return;  // already exists
      }
      execute(conn, getBaseSqlPath()+"/db.sql");
      executeForAllGroups(conn, getBaseSqlPath()+"/create_database.sql");
    } finally {
      DbPool.closeResults(rs);
      DbPool.quietCloseStatement(stmt);
      DbPool.quietClose(conn);
    }
  }


  public static String getBaseSqlPath()
  {
    return "it/data/carbonio/sql/";
  }

  //
  // Deletes all records from all tables.
  // @param zimbraServerDir the directory that contains the ZimbraServer project
  // @throws Exception
  //
  public static void clearDatabase() throws Exception
  {
    com.zimbra.cs.db.DbPool.DbConnection conn = DbPool.getConnection();
    try {
      executeForAllGroups(
        conn,
        getBaseSqlPath() + "/clear.sql"
      );
    } finally {
      DbPool.quietClose(conn);
    }
  }

  public static void executeForAllGroups(com.zimbra.cs.db.DbPool.DbConnection conn, String file) throws Exception
  {
    for( int i=1; i <= 100; ++i ) execute(conn, file, i);
  }

  public static void execute(DbPool.DbConnection conn, String file, int mboxId) throws Exception
  {
    Map<String, String> vars = Collections.singletonMap("DATABASE_NAME", DbMailbox.getDatabaseName(mboxId));
    SqlFile sql = new SqlFile(new File(file));
    sql.addUserVars(vars);
    sql.setConnection(conn.getConnection());
    sql.execute();
    conn.commit();
  }

  public static void execute(DbPool.DbConnection conn, String file) throws Exception
  {
    execute(conn,file,1);
  }

  DbPool.PoolConfig getPoolConfig() {
    return new Config();
  }

  boolean supportsCapability(Capability capability) {
    switch (capability) {
      case MULTITABLE_UPDATE:
      case BITWISE_OPERATIONS:
      case REPLACE_INTO:
      case DISABLE_CONSTRAINT_CHECK:
        return false;
      default:
        return true;
    }
  }

  boolean compareError(SQLException e, Error error) {
    switch (error) {
      case DUPLICATE_ROW:
        return e.getErrorCode() == -104;
      default:
        return false;
    }
  }

  public boolean databaseExists(DbPool.DbConnection connection, String dbname)
  {
    return true;
  }


  String forceIndexClause(String index) {
    return "";
  }

  String getIFNULLClause(String expr1, String expr2) {
    return "COALESCE(" + expr1 + ", " + expr2 + ")";
  }

  public String bitAND(String expr1, String expr2) {
    return "BITAND(" + expr1 + ", " + expr2 + ")";
  }

  public String bitANDNOT(String expr1, String expr2)
  {
    return expr1 + " & ~" + expr2;
  }

  public void flushToDisk() {
  }


// mRootUrl = null;
// mConnectionUrl = "jdbc:hsqldb:file:/tmp/zimbra-it/zimbra";

  public static class Config extends DbPool.PoolConfig
  {
    Config() {
      mDriverClassName = "org.hsqldb.jdbcDriver";
      mPoolSize = 10;
      mRootUrl = null;
      mConnectionUrl = "jdbc:hsqldb:mem:zimbra" + UUID.randomUUID().toString();
      mSupportsStatsCallback = false;
      mDatabaseProperties = new Properties();
    }
  }

  public String concat(String... fieldsToConcat) {
    String joined = "";

    for( String field : fieldsToConcat )
    {
      joined += field + ", ";
    }

    if( joined.length() > 0 ) {
      joined = joined.substring(0, joined.length()-2);
    }
    return "CONCAT(" + joined + ")";
  }

  public String sign(String field) {
    return "SIGN(" + field + ")";
  }

  public String lpad(String field, int padSize, String padString) {
    return "LPAD(" + field + ", " + padSize + ", '" + padString + "')";
  }

  public String limit(int offset, int limit) {
    return "LIMIT " + limit + " OFFSET " + offset;
  }

  public static void useMVCC() throws ServiceException, SQLException {
    //tell HSQLDB to use multiversion so our asserts can read while write is open
    PreparedStatement stmt = null;
    ResultSet rs = null;
    com.zimbra.cs.db.DbPool.DbConnection
      conn = DbPool.getConnection();
    try {
      stmt = conn.prepareStatement("SET DATABASE TRANSACTION CONTROL MVCC");
      stmt.executeUpdate();
    } finally {
      DbPool.closeResults(rs);
      DbPool.quietCloseStatement(stmt);
      DbPool.quietClose(conn);
    }
  }
}