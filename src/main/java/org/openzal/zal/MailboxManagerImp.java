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

package org.openzal.zal;


import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbPool;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.commons.dbutils.DbUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.NoSuchMailboxException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ZimbraDatabase;
import org.openzal.zal.log.ZimbraLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"StaticVariableOfConcreteClass", "StaticNonFinalField", "Singleton"})
public class MailboxManagerImp implements MailboxManager
{
  static final String TABLE_MAILBOX       = "mailbox";
  static final String TABLE_METADATA      = "mailbox_metadata";
  static final String TABLE_OUT_OF_OFFICE = "out_of_office";

  public static final String[] sTABLES = {
                                           "mail_item",
                                           "appointment",
                                           "data_source_item",
                                           "imap_folder",
                                           "imap_message",
                                           "open_conversation",
                                           "pop3_message",
                                           "revision",
                                           "tag",
                                           "tagged_item",
                                           "tombstone",
                                           "mail_item_dumpster",
                                           "appointment_dumpster",
                                           "revision_dumpster",
                                           "zimbra.mailbox",
                                           "zimbra.mailbox_metadata",
                                           "zimbra.out_of_office",
                                           "zimbra.pending_acl_push",
                                           "zimbra.scheduled_task"
  };

  private final          com.zimbra.cs.mailbox.MailboxManager                           mMailboxManager;
  @Nonnull private final HashMap<MailboxManagerListener, MailboxManagerListenerWrapper> mListenerMap;

  public MailboxManagerImp()
  {
    try
    {
      mMailboxManager = com.zimbra.cs.mailbox.MailboxManager.getInstance();
    }
    catch (com.zimbra.common.service.ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }

    mListenerMap = new HashMap<MailboxManagerListener, MailboxManagerListenerWrapper>();
  }

  private static final Field sMaintenanceLocks;
  private static final Field sMailboxIds;
  private static final Field sCache;

  static
  {
    try
    {
      sMaintenanceLocks = com.zimbra.cs.mailbox.MailboxManager.class.getDeclaredField("maintenanceLocks");
      sMailboxIds = com.zimbra.cs.mailbox.MailboxManager.class.getDeclaredField("mailboxIds");
      sCache = com.zimbra.cs.mailbox.MailboxManager.class.getDeclaredField("cache");
      sMaintenanceLocks.setAccessible(true);
      sMailboxIds.setAccessible(true);
      sCache.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public MailboxManagerImp(Object mailboxManager)
  {
    mMailboxManager = (com.zimbra.cs.mailbox.MailboxManager) mailboxManager;
    mListenerMap = new HashMap<MailboxManagerListener, MailboxManagerListenerWrapper>();
  }

  @Override
  public int[] getMailboxIds()
  {
    return mMailboxManager.getMailboxIds();
  }

  @Override
  public Set<Integer> getMailboxIdsSet()
  {
    int[] ids = getMailboxIds();
    Set<Integer> set = new HashSet<Integer>(ids.length);

    for( int n=0; n < ids.length; ++n ) {
      set.add(ids[n]);
    }

    return set;
  }


  @Override
  public Set<Integer> getMailboxGroupSet()
  {
    int[] ids = getMailboxIds();
    Set<Integer> set = new HashSet<Integer>(100);

    for( int n=0; n < ids.length; ++n )
    {
      set.add( (ids[n]-1) % 100 + 1 );
    }

    return set;
  }

  @Override
  public Mailbox getMailboxById(long mailboxId) throws ZimbraException
  {
    try
    {
      return new Mailbox(mMailboxManager.getMailboxById((int)mailboxId));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Mailbox getMailboxById(long mailboxId,boolean skipMailHostCheck) throws ZimbraException
  {
    try
    {
      return new Mailbox(mMailboxManager.getMailboxById((int)mailboxId,skipMailHostCheck));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Mailbox getMailboxByAccount(Account account) throws ZimbraException
  {
    try
    {
      return new Mailbox(mMailboxManager.getMailboxByAccount(account.toZimbra(com.zimbra.cs.account.Account.class)));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Mailbox getMailboxByAccountId(String accountId) throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return new Mailbox(mMailboxManager.getMailboxByAccountId(accountId));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public int getMailboxCount()
  {
    return mMailboxManager.getMailboxCount();
  }

  @Override
  public void addListener(MailboxManagerListener listener)
  {
    final MailboxManagerListenerWrapper wrapper = new MailboxManagerListenerWrapper(listener);
    List<com.zimbra.cs.mailbox.Mailbox> mailboxList = mMailboxManager.getAllLoadedMailboxes();

    mMailboxManager.addListener(wrapper);

    final Set<Mailbox> set = new HashSet<Mailbox>();
    for(com.zimbra.cs.mailbox.Mailbox mailbox : mailboxList )
    {
      set.add(new Mailbox(mailbox));
    }

/*
    MailboxManager Listener should be added in the boot phase,
    in the meanwhile some mailboxes could be already loaded,
    here we call mailboxLoaded for each mailbox already loaded
*/
    new Thread(
      new Runnable()
      {
        @Override
        public void run()
        {
          wrapper.notifyExistingMailboxesAndStopTracking(set);
        }
      }
    ).start();
  }

  @Override
  public void removeListener(MailboxManagerListener listener)
  {
    mMailboxManager.removeListener(new MailboxManagerListenerWrapper(listener));
  }

  @Nullable
  @Override
  public Mailbox getMailboxByAccountId(String accountId,boolean autoCreate) throws ZimbraException
  {
    try
    {
      if (autoCreate)
      {
        return new Mailbox(mMailboxManager.getMailboxByAccountId(
          accountId,
          com.zimbra.cs.mailbox.MailboxManager.FetchMode.AUTOCREATE,
          true));
      }
      else
      {
        com.zimbra.cs.mailbox.Mailbox mailbox = mMailboxManager.getMailboxByAccountId(
          accountId,
          com.zimbra.cs.mailbox.MailboxManager.FetchMode.DO_NOT_AUTOCREATE,
          true
        );
        if (mailbox == null)
        {
          return null;
        }
        return new Mailbox(mailbox);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public MailboxMaintenance beginMaintenance(String accountId, int mailboxId) throws ZimbraException
  {
    try
    {
      MailboxMaintenance maintenance = new MailboxMaintenance(mMailboxManager.beginMaintenance(
              accountId,
              mailboxId));
      return maintenance;
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void endMaintenance(MailboxMaintenance maintenance, boolean success, boolean removeFromCache) throws ZimbraException
  {
    try
    {
      mMailboxManager.endMaintenance(maintenance.toZimbra(com.zimbra.cs.mailbox.MailboxMaintenance.class),
              success,
              removeFromCache);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void cleanCache(Mailbox mailbox)
  {
    MailboxMaintenance maintenance = beginMaintenance(mailbox.getAccountId(), mailbox.getId());
    endMaintenance(maintenance, false, true);
  }

  @Override
  public Mailbox cleanCacheAndGetUpdatedMailbox(Mailbox mailbox)
  {
    cleanCache(mailbox);
    return getMailboxByAccountId(mailbox.getAccountId());
  }

  @Override
  public Mailbox cleanCacheAndGetUpdatedMailboxById(Mailbox mailbox, boolean skipMailhostCheck)
  {
    cleanCache(mailbox);
    return getMailboxById(mailbox.getId(), skipMailhostCheck);
  }

  @Override
  public void registerAdditionalQuotaProvider(final AdditionalQuotaProvider additionalQuotaProvider)
  {
    mMailboxManager.addAdditionalQuotaProvider(new ZALAdditionalQuotaProvider(additionalQuotaProvider));
  }

  @Override
  public void removeAdditionalQuotaProvider(final AdditionalQuotaProvider additionalQuotaProvider)
  {
    mMailboxManager.removeAdditionalQuotaProvider(new ZALAdditionalQuotaProvider(additionalQuotaProvider));
  }

  private interface ZalProxyObject
  {
    Object getProxiedObject();
  }

  @Override
  public MailboxData getMailboxData(long mailboxId)
  {
    DbPool.DbConnection conn = null;
    try
    {
      conn = DbPool.getConnection();
      com.zimbra.cs.mailbox.Mailbox.MailboxData data;
      try
      {
        data = DbMailbox.getMailboxStats(conn, (int) mailboxId);
        if( data == null )
        {
          throw new NoSuchMailboxException(new RuntimeException());
        }
        return new MailboxData(data.id,data.schemaGroupId,data.accountId,data.indexVolumeId);
      }
      finally
      {
        conn.closeQuietly();
      }
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void forceDeleteMailbox(@Nonnull MailboxData data)
  {
    Connection connection = null;
    PreparedStatement statement = null;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();

      Mailbox mailbox = Mailbox.createFakeMailbox(data.getId(), data.getAccountId(), data.getSchemaGroupId());
      DbMailbox.clearMailboxContent(
        connection.toZimbra(DbPool.DbConnection.class),
        mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class)
      );
      String query = "DELETE FROM %s WHERE %s=?";
      for( String table : sTABLES )
      {
        boolean zimbraNameSpace = table.startsWith("zimbra.");
        String tableName = table;
        String columnName = "id";

        if (!table.equals("zimbra.mailbox"))
        {
          columnName = "mailbox_id";
        }

        if (!zimbraNameSpace)
        {
          tableName = "mboxgroup" + mailbox.getSchemaGroupId() + "." + table;
        }
        try
        {
          statement = connection.prepareStatement(String.format(query, tableName, columnName));
          statement.setInt(1, data.getId());
          statement.executeUpdate();
        }
        finally
        {
          DbUtils.closeQuietly(statement);
          statement = null;
        }
      }
      connection.commit();
    }
    catch (SQLException e)
    {
      connection.rollback();
    }
    catch ( ServiceException e )
    {
      connection.rollback();
      throw ExceptionWrapper.wrap(e);
    }
    catch (ZimbraException e )
    {
      if( connection != null)
      {
        connection.rollback();
      }
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }

/*
    Remove mailbox entry from mailbox manager caches, it never existed....muhahaha
*/
    try
    {
      synchronized (mMailboxManager)
      {
        ((ConcurrentHashMap) (sMaintenanceLocks.get(mMailboxManager))).remove(data.getAccountId());
        ((Map) (sMailboxIds.get(mMailboxManager))).remove(data.getAccountId());
        ((Map) (sCache.get(mMailboxManager))).remove(data.getId());
      }
    }
    catch (Throwable ex)
    {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void createMailboxWithSpecificId(Connection connection, Account account, long mailboxId)
  {
    try
    {
      DbMailbox.createMailbox(
        connection.toZimbra(DbPool.DbConnection.class),
        (int)mailboxId,
        account.getId(),
        account.getName(),
        -1
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    try
    {
      synchronized (mMailboxManager)
      {
        ((ConcurrentHashMap) (sMaintenanceLocks.get(mMailboxManager))).remove(account.getId());
        ((Map) (sMailboxIds.get(mMailboxManager))).put(account.getId().toLowerCase(), (int)mailboxId);
        ((Map) (sCache.get(mMailboxManager))).remove((int)mailboxId);
      }
    }
    catch (Throwable ex)
    {
      throw new RuntimeException(ex);
    }
  }

  class ZALAdditionalQuotaProvider
    implements com.zimbra.cs.mailbox.AdditionalQuotaProvider
  {
    private AdditionalQuotaProvider mAdditionalQuotaProvider;

    ZALAdditionalQuotaProvider(AdditionalQuotaProvider mAdditionalQuotaProvider)
    {
      this.mAdditionalQuotaProvider = mAdditionalQuotaProvider;
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }
      ZALAdditionalQuotaProvider that = (ZALAdditionalQuotaProvider) o;
      return Objects.equals(mAdditionalQuotaProvider, that.mAdditionalQuotaProvider);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(mAdditionalQuotaProvider);
    }

    @Override
    public long getAdditionalQuota(com.zimbra.cs.mailbox.Mailbox mailbox)
    {
      return mAdditionalQuotaProvider.getAdditionalQuota(new org.openzal.zal.Mailbox(mailbox));
    }
  }
}
