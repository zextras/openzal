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

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import org.jetbrains.annotations.NotNull;

public class ZEMailboxManagerListenerWrapper implements MailboxManager.Listener
{
  private final ZEMailboxManagerListener mListener;

  public ZEMailboxManagerListenerWrapper(@NotNull ZEMailboxManagerListener listener)
  {
    if ( listener == null )
    {
      throw new NullPointerException();
    }
    mListener = listener;
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

    ZEMailboxManagerListenerWrapper that = (ZEMailboxManagerListenerWrapper) o;

    if (mListener != null ? !mListener.equals(that.mListener) : that.mListener != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return mListener != null ? mListener.hashCode() : 0;
  }

  @Override
  public void mailboxAvailable(Mailbox mbox)
  {
    mListener.mailboxAvailable(new ZEMailbox(mbox));
  }

  @Override
  public void mailboxCreated(Mailbox mbox)
  {
    mListener.mailboxCreated(new ZEMailbox(mbox));
  }

  @Override
  public void mailboxDeleted(String accountId)
  {
    mListener.mailboxDeleted(accountId);
  }

  @Override
  public void mailboxLoaded(Mailbox mbox)
  {
    mListener.mailboxLoaded(new ZEMailbox(mbox));
  }
}
