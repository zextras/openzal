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
import org.apache.commons.dbutils.DbUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

  private final          com.zimbra.cs.mailbox.MailboxManager                           mMailboxManager;
  @NotNull private final HashMap<MailboxManagerListener, MailboxManagerListenerWrapper> mListenerMap;

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
  public void registerAdditionalQuotaProvider(final AdditionalQuotaProvider additionalQuotaProvider)
  {
    /* $if ZimbraVersion >= 8.8.10 $ */
    mMailboxManager.addAdditionalQuotaProvider(new ZALAdditionalQuotaProvider(additionalQuotaProvider));
    /* $elseif ZimbraX == 1 $
    return;
    /* $endif $ */
  }

  @Override
  public void removeAdditionalQuotaProvider(final AdditionalQuotaProvider additionalQuotaProvider)
  {
    /* $if ZimbraVersion >= 8.8.10 $ */
    mMailboxManager.removeAdditionalQuotaProvider(new ZALAdditionalQuotaProvider(additionalQuotaProvider));
    /* $elseif ZimbraX == 1 $
    return;
    /* $endif $ */
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
  public void forceDeleteMailbox(@NotNull MailboxData data)
  {
    Connection connection = null;
    PreparedStatement statement;
    try
    {
      connection = ZimbraDatabase.legacyGetConnection();

      Mailbox mailbox = Mailbox.createFakeMailbox(data.getId(), data.getAccountId(), data.getSchemaGroupId());
      DbMailbox.clearMailboxContent(
        connection.toZimbra(DbPool.DbConnection.class),
        mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class)
      );
      String tables[] = {TABLE_OUT_OF_OFFICE, TABLE_METADATA, TABLE_MAILBOX};
      String query = "DELETE FROM zimbra.%s WHERE id=?";
      for( String table : tables )
      {
        statement = null;
        try
        {
          statement = connection.prepareStatement(String.format(query, table));
          statement.setInt(1, data.getId());
          statement.executeUpdate();
        }
        catch (SQLException e)
        {
        }
        finally
        {
          DbUtils.closeQuietly(statement);
        }
      }
      connection.commit();
    }
    catch (ServiceException e )
    {
      connection.rollback();
      throw ExceptionWrapper.wrap(e);
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

  /* $if ZimbraVersion >= 8.8.10 $ */
  /* $if ZimbraVersion >= 8.8.10 && ZimbraX == 0 $ */
  class ZALAdditionalQuotaProvider implements com.zimbra.cs.mailbox.AdditionalQuotaProvider
  {
    private final AdditionalQuotaProvider mAdditionalQuotaProvider;

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
  /* $endif $ */
}
