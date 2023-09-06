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

import com.zimbra.common.mailbox.BaseItemInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.ItemChange.ChangeType;
import org.openzal.zal.lib.Clock;
import org.openzal.zal.lib.ActualClock;

import org.openzal.zal.log.ZimbraLog;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.Session;

import java.util.Collection;
import java.util.Map;

public class MailboxSessionProxy
{
  @Nonnull private final SessionImpl mSession;
  private                Integer     mMboxId;
  private                String      mName;
  @Nonnull private       Listener    mListener;
  @Nonnull private       Clock       mClock;

  public MailboxSessionProxy(int mboxId, String name, String accountId, @Nonnull Listener listener)
  {
    mSession = new SessionImpl(accountId, Session.Type.SYNCLISTENER);
    mMboxId = mboxId;
    mName = name;
    mListener = listener;
    mClock = ActualClock.sInstance;
  }

  public MailboxSessionProxy(Object session)
  {
    mSession = (SessionImpl) session;
    mListener = new Listener() {
      @Override
      public void setStoreContext(MailboxSessionProxy sessionProxy)
      {

      }

      @Override
      public MailboxSessionProxy getStoreContext()
      {
        return MailboxSessionProxy.this;
      }

      @Override
      public void notifyChanges(long mboxId, ItemChange itemInfo)
      {

      }

      @Override
      public void sessionClosed()
      {

      }
    };
    mClock = new ActualClock();
  }

  private boolean areChangesForMobile(Object what)
  {
    byte type = Item.TYPE_UNKNOWN;

    if (what instanceof MailItem.Type)
    {
      type = ((MailItem.Type) what).toByte();
    }

    if (what instanceof MailItem)
    {
      Item item = new Item((MailItem) what);
      type = item.getType();
    }

    boolean isRelevant = false;
    switch (type)
    {
      case Item.TYPE_MESSAGE:
      case Item.TYPE_CONTACT:
      case Item.TYPE_APPOINTMENT:
        isRelevant = true;
    }

    return isRelevant;
  }

  @Nonnull
  public String getLoggerName()
  {
    return "MailboxSessionProxy";
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mSession);
  }

  private class SessionImpl extends Session
  {
    public SessionImpl(String accountId, Type type) {super(accountId, type);}

    @Override
    public String getSessionId()
    {
      return mName;
    }

    @Override
    protected boolean isMailboxListener()
    {
      return true;
    }

    @Override
    protected boolean isRegisteredInCache()
    {
      return false;
    }

    @Override
    protected long getSessionIdleLifetime()
    {
      return 0;
    }

    @Override
    public void notifyPendingChanges(@Nonnull PendingModifications pns, int changeId, @Nullable Session source)
    {
      if( !pns.hasNotifications() )
      {
        return;
      }

      // Ignore changes that comes from another mailbox.
      if( source != null && (source.getMailbox() instanceof com.zimbra.cs.mailbox.Mailbox) && ((com.zimbra.cs.mailbox.Mailbox) source.getMailbox()).getId() != mMboxId )
      {
        ZimbraLog.mailbox.debug(
          getLoggerName() +
            " The changes come from another mailbox, ignoring. S: " +
            ((com.zimbra.cs.mailbox.Mailbox) source.getMailbox()).getId() + " - C: " + mMboxId
        );
        return;
      }

      if( source != null && !(source.getMailbox() instanceof com.zimbra.cs.mailbox.Mailbox) )
      {
        ZimbraLog.mailbox.debug(
          getLoggerName() +
            " The changes come from an external mailbox, ignoring."
        );
        return;
      }

      if( pns.created != null )
      {
        for( PendingModifications.ModificationKey mod : ((Map<PendingModifications.ModificationKey, BaseItemInfo>) pns.created).keySet() )
        {
          Object whatObj = pns.created.get(mod);
          if (! (whatObj instanceof MailItem))
          {
            continue;
          }
          MailItem what = (MailItem) whatObj;
          if (areChangesForMobile(what))
          {
            mListener.notifyChanges(
              mMboxId,
              new ItemChange(
                ChangeType.CREATED,
                what.getFolderId(),
                what.getId(),
                what.getModifiedSequence(),
                what.getDate()
              )
            );
          }
        }
      }

      if( pns.modified != null )
      {
        for( PendingModifications.ModificationKey mod : ((Map<PendingModifications.ModificationKey, PendingModifications.Change>) pns.modified).keySet() )
        {
          PendingModifications.Change change = (PendingModifications.Change) pns.modified.get(mod);

          if( areChangesForMobile( change.what ))
          {
            ItemChange itemChange;
            if( change.what instanceof MailItem )
            {
              MailItem item = (MailItem) change.what;
              itemChange = new ItemChange(
                ChangeType.MODIFIED,
                item.getFolderId(),
                item.getId(),
                item.getModifiedSequence(),
                item.getDate()
              );
            }
            else
            {
              ZimbraLog.mailbox.debug(getLoggerName() + " Unable to obtain modification informations");
              itemChange = new ItemChange(
                ChangeType.MODIFIED,
                0,
                mod.getItemId(),
                0,
                mClock.getCurrentTime().getTimeInMillis()
              );
            }

            mListener.notifyChanges(mMboxId, itemChange);
          }
        }
      }

      if( pns.deleted != null )
      {
        for( PendingModifications.ModificationKey mod : ((Map<PendingModifications.ModificationKey, PendingModifications.Change>) pns.deleted).keySet() )
        {
          PendingModifications.Change change = (PendingModifications.Change) pns.deleted.get(mod);
          if( areChangesForMobile( change.what ))
          {
            ItemChange itemChange = new ItemChange(
              ChangeType.DELETED,
              0,
              mod.getItemId(),
              0,
              mClock.getCurrentTime().getTimeInMillis()
            );

            mListener.notifyChanges(mMboxId, itemChange);
          }
        }
      }
    }

    @Override
    protected void cleanup()
    {
      mListener.sessionClosed();
    }
  }
}
