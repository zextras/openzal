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

package org.openzal.zal.calendar;

import javax.annotation.Nonnull;
import org.openzal.zal.ParsedMessage;
import com.zimbra.cs.mailbox.Mailbox;


public class CalendarItemData
{
  private final Invite        mInvite;
  private final ParsedMessage mMessage;

  public CalendarItemData(Invite invite, ParsedMessage message)
  {
    mInvite = invite;
    mMessage = message;
  }

  public ParsedMessage getMessage()
  {
    return mMessage;
  }

  public Invite getInvite()
  {
    return mInvite;
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    Mailbox.SetCalendarItemData calendarItemData = new Mailbox.SetCalendarItemData();
    calendarItemData.invite = mInvite.toZimbra(com.zimbra.cs.mailbox.calendar.Invite.class);
    if (mMessage != null)
    {
      calendarItemData.message = mMessage.toZimbra(com.zimbra.cs.mime.ParsedMessage.class);
    }

    return cls.cast(calendarItemData);
  }
}
