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

import com.zimbra.cs.mailbox.MailboxManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class MailboxManagerListenerWrapper implements MailboxManager.Listener
{
  @Nonnull
  private final MailboxManagerListener mListener;
  private final Set<String>            mAlreadyNotifiedMailboxes;
  private final ReentrantLock          mLock;
  private       boolean                mTrack;

  public MailboxManagerListenerWrapper(@Nonnull MailboxManagerListener listener)
  {
    if (listener == null)
    {
      throw new NullPointerException();
    }
    mListener = listener;
    mAlreadyNotifiedMailboxes = new HashSet<String>();
    mLock = new ReentrantLock();
    mTrack = true;
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    MailboxManagerListenerWrapper that = (MailboxManagerListenerWrapper) o;

    if (!mListener.equals(that.mListener))
    {
      return false;
    }

    return true;
  }

  private void notifiedMailbox(String accountId)
  {
    if (mTrack)
    {
      mLock.lock();
      try
      {
        mAlreadyNotifiedMailboxes.add(accountId);
      }
      finally
      {
        mLock.unlock();
      }
    }
  }

  public void notifyExistingMailboxesAndStopTracking(Set<Mailbox> alreadyLoadedMailboxes)
  {
    mLock.lock();
    try
    {
      for( Mailbox mailbox : alreadyLoadedMailboxes )
      {
        if( mAlreadyNotifiedMailboxes.contains(mailbox.getAccountId()) )
        {
          continue;
        }

        mListener.mailboxLoaded(mailbox);
      }

      mTrack = false;
    }
    finally
    {
      mLock.unlock();
    }
  }

  @Override
  public int hashCode()
  {
    return mListener.hashCode();
  }

  @Override
  public void mailboxAvailable(com.zimbra.cs.mailbox.Mailbox mbox)
  {
    notifiedMailbox(mbox.getAccountId());
    mListener.mailboxAvailable(new Mailbox(mbox));
  }

  @Override
  public void mailboxCreated(com.zimbra.cs.mailbox.Mailbox mbox)
  {
    notifiedMailbox(mbox.getAccountId());
    mListener.mailboxCreated(new Mailbox(mbox));
  }

  @Override
  public void mailboxDeleted(String accountId)
  {
    notifiedMailbox(accountId);
    mListener.mailboxDeleted(accountId);
  }

  @Override
  public void mailboxLoaded(com.zimbra.cs.mailbox.Mailbox mbox)
  {
    notifiedMailbox(mbox.getAccountId());
    mListener.mailboxLoaded(new Mailbox(mbox));
  }
}
