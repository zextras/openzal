/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.Connection;
import org.openzal.zal.Item;
import org.openzal.zal.LocalConfig;
import org.openzal.zal.Mailbox;
import org.openzal.zal.StoreVolume;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.exceptions.UnableToObtainDBConnectionException;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mailbox.MailItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.zimbra.cs.db.*;

public class ZimbraDatabase
{
  @Nullable
  public static List<Item.UnderlyingData> getByType(Mailbox mbox, byte type, SortBy sort) throws ZimbraException
  {
    List<MailItem.UnderlyingData> list;
    try
    {
      list = DbMailItem.getByType(
        mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
        Item.convertType(MailItem.Type.class, type),
        sort
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if( list != null )
    {
      List<Item.UnderlyingData> newList = new ArrayList<Item.UnderlyingData>(list.size());

      for( MailItem.UnderlyingData item : list ){
        newList.add( new Item.UnderlyingData(item) );
      }
      return newList;
    }
    else
    {
      return null;
    }
  }

  @Deprecated
  public static Connection getConnectionLegacy()
    throws ZimbraException
  {
    try
    {
      return new ZimbraConnectionWrapper(DbPool.getConnection());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Deprecated
  public static Connection legacyGetConnection()
    throws ZimbraException
  {
    try
    {
      return new ZimbraConnectionWrapper(DbPool.getConnection());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static int setMailboxId(PreparedStatement stmt, Mailbox mbox, int pos) throws SQLException
  {
    return DbMailItem.setMailboxId(stmt,
                                   mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
                                   pos);
  }

  public static int setMailboxId(PreparedStatement stmt, int mailboxId, int pos) throws SQLException
  {
    int next = pos;
    stmt.setInt(next++, mailboxId);
    return next;
  }

  @Nonnull
  public static Set<String> listAccountIds(Connection conn) throws ZimbraException
  {
    try
    {
      return DbMailbox.listAccountIds(conn.toZimbra(DbPool.DbConnection.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static final String IN_THIS_MAILBOX_AND = DbMailItem.IN_THIS_MAILBOX_AND;

  public static void closeStatement(@Nullable Statement st)
    throws ZimbraException
  {
    try
    {
      DbPool.closeStatement(st);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static void quietCloseStatement(@Nullable Statement st )
  {
    DbPool.quietCloseStatement(st);
  }

  public static void closeResults(@Nullable ResultSet res )
    throws ZimbraException
  {
    try
    {
      DbPool.closeResults(res);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static String suitableNumberOfVariables(byte[] array)    { return DbUtil.suitableNumberOfVariables(array.length); }
  public static String suitableNumberOfVariables(short[] array)   { return DbUtil.suitableNumberOfVariables(array.length); }
  public static String suitableNumberOfVariables(int[] array)     { return DbUtil.suitableNumberOfVariables(array.length); }
  public static String suitableNumberOfVariables(Object[] array)  { return DbUtil.suitableNumberOfVariables(array.length); }
  public static String suitableNumberOfVariables(Collection<?> c) { return DbUtil.suitableNumberOfVariables(c.size()); }

  @Nonnull
  public static CurrentVolumes getCurrentVolumes(@Nonnull Connection conn) throws ZimbraException
  {
    DbVolume.CurrentVolumes cv;
    try
    {
      cv = DbVolume.getCurrentVolumes(conn.toZimbra(DbPool.DbConnection.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    CurrentVolumes myCv = new CurrentVolumes();
    myCv.msgVolId = cv.msgVolId;
    myCv.secondaryMsgVolId = cv.secondaryMsgVolId;
    myCv.indexVolId = cv.indexVolId;

    return myCv;
  }

  public interface ConnectionProvider
  {
    Connection getConnection() throws UnableToObtainDBConnectionException;
  }

  public static class CurrentVolumes {
    public short msgVolId          = StoreVolume.ID_NONE;
    public short secondaryMsgVolId = StoreVolume.ID_NONE;
    public short indexVolId        = StoreVolume.ID_NONE;
  }

  public static String getItemTableName(Mailbox mbox)
  {
    return getItemTableName(mbox,false);
  }

  public static String getItemTableName(int mailboxGroupId)
  {
    return getItemTableName(mailboxGroupId,false);
  }

  public static String getItemTableName(Mailbox mbox, boolean dumpster)
  {
    return getItemTableName(mbox.getSchemaGroupId(), dumpster);
  }

  public static String getItemTableName(int mailboxGroupId, boolean dumpster)
  {
    String s = "mboxgroup" + mailboxGroupId + ".mail_item";
    if (dumpster)
      s += "_dumpster";
    return s;
  }

  public static String getCalendarTableName(Mailbox mbox)
  {
    return getCalendarTableName(mbox.getSchemaGroupId(), false);
  }

  public static String getCalendarTableName(int mailboxGroupId)
  {
    return getCalendarTableName(mailboxGroupId, false);
  }

  public static String getCalendarTableName(Mailbox mbox, boolean dumpster)
  {
    return getCalendarTableName(mbox.getSchemaGroupId(), dumpster);
  }

  public static String getCalendarTableName(int mailboxGroupId, boolean dumpster)
  {
    String s = "mboxgroup" + mailboxGroupId + ".appointment";
    if (dumpster)
      s += "_dumpster";
    return s;
  }

  public static String getTombstoneTable(Mailbox mbox)
  {
    return "mboxgroup" + mbox.getSchemaGroupId() + ".tombstone";
  }

  public static String getTombstoneTable(int mailboxGroupId)
  {
    return "mboxgroup" + mailboxGroupId + ".tombstone";
  }

  public static String getRevisionTableName(Mailbox mbox)
  {
    return getRevisionTableName(mbox.getSchemaGroupId(),false);
  }

  public static String getRevisionTableName(int mailboxGroupId)
  {
    return getRevisionTableName(mailboxGroupId,false);
  }

  public static String getRevisionTableName(Mailbox mbox,boolean dumpster)
  {
    return getRevisionTableName(mbox.getSchemaGroupId(), dumpster);
  }

  public static String getRevisionTableName(int mailboxGroupId, boolean dumpster)
  {
    String s = "mboxgroup" + mailboxGroupId + ".revision";
    if (dumpster)
      s += "_dumpster";
    return s;
  }

  public static int getMailboxGroupFromMailboxId(int mailboxId)
  {
    return (((mailboxId - 1) % LocalConfig.getInt(LocalConfig.zimbra_mailbox_groups)) + 1);
  }
}
