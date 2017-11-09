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


import com.zimbra.cs.mailbox.*;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"StaticVariableOfConcreteClass", "StaticNonFinalField", "Singleton"})
public class MailboxManagerImp implements MailboxManager
{
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

  public MailboxManagerImp(Object mailboxManager)
  {
    mMailboxManager = (com.zimbra.cs.mailbox.MailboxManager) mailboxManager;
    mListenerMap = new HashMap<MailboxManagerListener, MailboxManagerListenerWrapper>();
  }

  @Override
  public int[] getMailboxIds()
  {
    try
    {
      return com.zimbra.cs.mailbox.MailboxManager.getInstance().getMailboxIds();
    }
    catch (com.zimbra.common.service.ServiceException ex)
    {
      throw new RuntimeException();
    }
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
        return new Mailbox(mMailboxManager.getMailboxByAccountId(
          accountId,
          com.zimbra.cs.mailbox.MailboxManager.FetchMode.DO_NOT_AUTOCREATE,
          true));
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

}
