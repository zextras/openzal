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

import org.openzal.zal.calendar.ZEInvite;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Message;
import org.jetbrains.annotations.NotNull;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZEMessage extends ZEItem
{
  private final Message mMessage;

  public ZEMessage(@NotNull Object mailItem)
  {
    super(mailItem);
    mMessage = (Message) mailItem;
  }

  public class ZECalendarInfo
  {
    private final Message.CalendarItemInfo mCalendarItemInfo;

    public ZECalendarInfo(Message.CalendarItemInfo calendarItemInfo)
    {
      mCalendarItemInfo = calendarItemInfo;
    }

    public ZEInvite getInvite()
    {
      return new ZEInvite(mCalendarItemInfo.getInvite());
    }

    public boolean calItemCreated()
    {
      return mCalendarItemInfo.calItemCreated();
    }

    public int getCalendarItemId()
    {
/* $if ZimbraVersion >= 8.0.5$ */
      return mCalendarItemInfo.getCalendarItemId().getId();
/* $else$
      return mCalendarItemInfo.getCalendarItemId();
$endif$ */
    }
  }

  public MimeMessage getMimeMessage()
  {
    try
    {
      return ((Message) mMailItem).getMimeMessage();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getSortRecipients()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mMessage.getSortRecipients();
    /* $else $
    return mMessage.getRecipients();
    /* $endif $ */
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

  public Iterator<ZECalendarInfo> getCalendarItemInfoIterator()
  {
    final Iterator<Message.CalendarItemInfo> calendarItemInfoIterator = mMessage.getCalendarItemInfoIterator();

    return new Iterator<ZECalendarInfo>(
    )
    {
      @Override
      public boolean hasNext()
      {
        return calendarItemInfoIterator.hasNext();
      }

      @Override
      public ZECalendarInfo next()
      {
        return new ZECalendarInfo(
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
}
