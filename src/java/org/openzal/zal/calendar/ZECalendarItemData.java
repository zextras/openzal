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

package org.openzal.zal.calendar;

import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mime.ParsedMessage;
import org.openzal.zal.ZEParsedMessage;
import com.zimbra.cs.mailbox.Mailbox;


public class ZECalendarItemData
{
  private final ZEInvite mInvite;
  private final ZEParsedMessage mMessage;

  public ZECalendarItemData(ZEInvite invite, ZEParsedMessage message)
  {
    mInvite = invite;
    mMessage = message;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    Mailbox.SetCalendarItemData calendarItemData = new Mailbox.SetCalendarItemData();
    /* $if ZimbraVersion >= 8.0.0 $ */
    calendarItemData.invite = mInvite.toZimbra(Invite.class);
    if(mMessage != null)
    {
      calendarItemData.message = mMessage.toZimbra(ParsedMessage.class);
    }
    /* $else $
    calendarItemData.mInv = mInvite.toZimbra(Invite.class);
    if(mMessage != null)
    {
      calendarItemData.mPm = mMessage.toZimbra(ParsedMessage.class);
    }
    /* $endif $ */

    return cls.cast(calendarItemData);
  }
}
