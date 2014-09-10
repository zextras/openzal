/*
 * ZAL - An abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import org.openzal.zal.lib.Clock;
import org.openzal.zal.lib.ActualClock;

import org.openzal.zal.log.ZimbraLog;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.Session;


public class MailboxSessionProxy
{
  private final SessionImpl mSession;
  private       Integer     mMboxId;
  private       String      mName;
  private       Listener    mListener;
  private       Clock       mClock;

  public MailboxSessionProxy(int mboxId, String name, String accountId, Listener listener)
  {
    mSession = new SessionImpl(accountId, Session.Type.SYNCLISTENER);
    mMboxId = mboxId;
    mName = name;
    mListener = listener;
    mClock = new ActualClock();
  }

  public MailboxSessionProxy(Object session)
  {
    mSession = (SessionImpl) session;
  }

  private boolean areChangesForMobile(Object what)
  {
    byte type = Item.TYPE_UNKNOWN;

/* $if MajorZimbraVersion >= 8 $ */
    if (what instanceof MailItem.Type)
    {
      type = ((MailItem.Type) what).toByte();
    }
/* $endif$ */

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
      case Item.TYPE_TASK:
        isRelevant = true;
    }

    return isRelevant;
  }

  public String getLoggerName()
  {
    return "MailboxSessionProxy";
  }

  public <T> T toZimbra(Class<T> cls)
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
    public void notifyPendingChanges(PendingModifications pns, int changeId, Session source)
    {
      if( !pns.hasNotifications() )
      {
        return;
      }

      // Ignore changes that comes from another mailbox.
      if( source != null && source.getMailbox().getId() != mMboxId )
      {
        ZimbraLog.mailbox.debug(
          getLoggerName() +
          " The changes come from another mailbox, ignoring. S: " +
            source.getMailbox().getId() + " - C: " + mMboxId
        );
        return;
      }

      if( pns.created != null )
      {
        for( PendingModifications.ModificationKey mod : pns.created.keySet() )
        {
          MailItem what = pns.created.get(mod);
          if( areChangesForMobile( what ))
          {
            mListener.notifyChanges(
              mMboxId,
              new ItemChange(
                false,
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
        for( PendingModifications.ModificationKey mod : pns.modified.keySet() )
        {
          PendingModifications.Change change = pns.modified.get(mod);

          if( areChangesForMobile( change.what ))
          {
            ItemChange itemChange;
            if( change.what instanceof MailItem )
            {
              MailItem item = (MailItem) change.what;
              itemChange = new ItemChange(
                false,
                item.getFolderId(),
                item.getId(),
                item.getModifiedSequence(),
                item.getDate()
              );
            }
            else
            {
              ZimbraLog.mobile.debug(getLoggerName() + " Unable to obtain modification informations");
              itemChange = new ItemChange(
                false,
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
        for( PendingModifications.ModificationKey mod : pns.deleted.keySet() )
        {
  /* $if MajorZimbraVersion >= 8 $ */
          PendingModifications.Change change = pns.deleted.get(mod);
          if( areChangesForMobile( change.what ))
  /* $else$
          Object what = pns.deleted.get(mod);
          if( areChangesForMobile( what ))
     $endif$ */
          {
            ItemChange itemChange = new ItemChange(
              true,
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
      mListener = null;
    }
  }
}
