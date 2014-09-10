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

import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.RecurrenceId;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.calendar.Recurrence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;


public class CalendarItem extends Item
{
  private final com.zimbra.cs.mailbox.CalendarItem mCalendarItem;

  public CalendarItem(@NotNull Object item)
  {
    super((MailItem) item);
    mCalendarItem = (com.zimbra.cs.mailbox.CalendarItem) item;
  }

  public String getUid()
  {
    return mCalendarItem.getUid();
  }

  public void copyReplyInfoTo(CalendarItem calendar)
  {
    try
    {
      calendar.mCalendarItem.setReplies(
        mCalendarItem.getAllReplies()
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public MimeMessage getMimeMessage()
  {
    try
    {
      return mCalendarItem.getMimeMessage();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isRecurring()
  {
    return mCalendarItem.isRecurring();
  }

  public long getStartTime()
  {
    return mCalendarItem.getStartTime();
  }

  public long getEndTime()
  {
    return mCalendarItem.getEndTime();
  }

  @Nullable
  public Invite getDefaultInviteOrNull()
  {
    return wrap(mCalendarItem.getDefaultInviteOrNull());
  }

  public List<Invite> getInvites()
  {
    com.zimbra.cs.mailbox.calendar.Invite[] inviteArray = mCalendarItem.getInvites();
    List<Invite> inviteList = new ArrayList<Invite>(inviteArray.length);
    for (com.zimbra.cs.mailbox.calendar.Invite invite : inviteArray)
    {
      inviteList.add(wrap(invite));
    }
    return inviteList;
  }

  @Nullable
  public Invite getInvite(RecurrenceId recurId)
  {
    if(recurId == null)
    {
      return wrap(mCalendarItem.getDefaultInviteOrNull());
    }
    return wrap(mCalendarItem.getInviteForRecurId(recurId.getExceptionStartTimeUtc()));
  }

  public Recurrence.IRecurrence getRecurrence()
  {
    return mCalendarItem.getRecurrence();
  }

  private Invite wrap(com.zimbra.cs.mailbox.calendar.Invite invite)
  {
    if (invite == null)
    {
      return null;
    }

    return new Invite(invite);
  }
}
