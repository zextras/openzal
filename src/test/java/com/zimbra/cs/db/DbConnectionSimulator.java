package com.zimbra.cs.db;

import com.zimbra.common.service.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnectionSimulator extends DbPool.DbConnection
{
  public DbConnectionSimulator(Connection conn)
  {
    super(conn);
  }

  DbConnectionSimulator(Connection conn, Integer mboxId)
  {
    super(conn, mboxId);
  }

  public void disableForeignKeyConstraints() throws ServiceException
  {
    String sql = "SET DATABASE REFERENTIAL INTEGRITY FALSE";
    PreparedStatement stmt = null;

    Connection connection = null;
    try {
      connection = this.getConnection();
      stmt = connection.prepareStatement(sql);
      stmt.execute();
    } catch (SQLException var6) {
      throw ServiceException.FAILURE("disabling foreign key constraints", var6);
    } finally {
      DbPool.closeStatement(stmt);
    }

  }

  public void enableForeignKeyConstraints() throws ServiceException {
    String sql = "SET DATABASE REFERENTIAL INTEGRITY TRUE";
    PreparedStatement stmt = null;

    Connection connection = null;
    try {
      connection = this.getConnection();
      stmt = connection.prepareStatement(sql);
      stmt.execute();
    } catch (SQLException var7) {
      throw ServiceException.FAILURE("enabling foreign key constraints", var7);
    } finally {
      DbPool.closeStatement(stmt);
    }

  }
}
