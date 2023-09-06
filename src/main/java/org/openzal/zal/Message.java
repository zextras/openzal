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

import org.openzal.zal.calendar.Invite;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Message extends Item
{
  @Nonnull private final com.zimbra.cs.mailbox.Message mMessage;

  public Message(@Nonnull Object mailItem)
  {
    super(mailItem);
    mMessage = (com.zimbra.cs.mailbox.Message) mailItem;
  }

  public class CalendarInfo
  {
    private final com.zimbra.cs.mailbox.Message.CalendarItemInfo mCalendarItemInfo;

    public CalendarInfo(com.zimbra.cs.mailbox.Message.CalendarItemInfo calendarItemInfo)
    {
      mCalendarItemInfo = calendarItemInfo;
    }

    public Invite getInvite()
    {
      return new Invite(mCalendarItemInfo.getInvite());
    }

    public boolean calItemCreated()
    {
      return mCalendarItemInfo.calItemCreated();
    }

    public int getCalendarItemId()
    {
      return mCalendarItemInfo.getCalendarItemId().getId();
    }
  }

  public MimeMessage getMimeMessage()
  {
    try
    {
      return ((com.zimbra.cs.mailbox.Message) mMailItem).getMimeMessage();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getSortRecipients()
  {
    return mMessage.getSortRecipients();
  }

  public MimeMessage getMimeMessage(boolean runConverters)
  {
    try
    {
      return mMessage.getMimeMessage(runConverters);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }


  public boolean hasCalendarItemInfos()
  {
    return mMessage.hasCalendarItemInfos();
  }

  public Iterator<CalendarInfo> getCalendarItemInfoIterator()
  {
    final Iterator<com.zimbra.cs.mailbox.Message.CalendarItemInfo> calendarItemInfoIterator = mMessage.getCalendarItemInfoIterator();

    return new Iterator<CalendarInfo>(
    )
    {
      @Override
      public boolean hasNext()
      {
        return calendarItemInfoIterator.hasNext();
      }

      @Override
      public CalendarInfo next()
      {
        return new CalendarInfo(
          calendarItemInfoIterator.next()
        );
      }

      @Override
      public void remove()
      {
        calendarItemInfoIterator.remove();
      }
    };
  }

  public String getSender()
  {
    return mMessage.getSender();
  }

  public List<String> getCustomDataSections()
  {
    return new ArrayList<String>(
      mMessage.getCustomDataSections()
    );
  }

  public boolean isInvite()
  {
    return mMessage.isInvite();
  }

  public String getFragment()
  {
    return mMessage.getFragment();
  }

  public String getRecipients()
  {
    return mMessage.getRecipients();
  }

  public int getConversationId()
  {
    return mMessage.getConversationId();
  }

  public boolean isFlagged()
  {
    return (getFlagBitmask() & Flag.BITMASK_FLAGGED) == Flag.BITMASK_FLAGGED;
  }
}
