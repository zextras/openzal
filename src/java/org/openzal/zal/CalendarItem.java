/*
 * ZAL - The abstraction layer for Zimbra.
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

import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import org.openzal.zal.calendar.Attendee;
import org.openzal.zal.calendar.AttendeeInviteStatus;
import org.openzal.zal.calendar.CalendarItemData;
import org.openzal.zal.calendar.Invite;
import org.openzal.zal.calendar.InviteFactory;
import org.openzal.zal.calendar.RecurrenceId;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.calendar.Recurrence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;


public class CalendarItem extends Item
{
  @NotNull private final com.zimbra.cs.mailbox.CalendarItem mCalendarItem;

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
  public Invite getInvite(@Nullable RecurrenceId recurId)
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

  public void updatePartStat(Account invitedUser, String partStat, @Nullable RecurrenceId recurId, long time)
    throws IOException, MessagingException
  {
    Mailbox mailbox = getMailbox();
    OperationContext operationContext = mailbox.newOperationContext();
    int sequence;
    Invite requestInvite;

    Invite defaultInvite = getDefaultInviteOrNull();
    if (recurId == null)
    {
      requestInvite = defaultInvite;
    }
    else
    {
      requestInvite = getInvite(recurId);
    }

    if (defaultInvite == null || requestInvite == null)
    {
      return;
    }

    sequence = requestInvite.getSequence();

    ParsedMessage parsedMessage = null;
    MimeMessage mimeMessage = null;
    if (getDigest() != null)
    {
      mimeMessage = getMimeMessage();
    }

    if (mimeMessage != null)
    {
      parsedMessage = new ParsedMessage(mimeMessage, mailbox.attachmentsIndexingEnabled());
    }

    List<Invite> exceptions = defaultInvite.getExceptionInstances();

    List<CalendarItemData> newExceptions = new ArrayList<CalendarItemData>(exceptions.size());
    for (Invite exception : exceptions)
    {
      if (recurId != null && recurId.equals(exception.getRecurId()))
      {
        newExceptions.add(new CalendarItemData(updateInvitePartStat(mailbox, invitedUser, partStat, exception), parsedMessage));
      }
      else
      {
        newExceptions.add(new CalendarItemData(exception, parsedMessage));
      }
    }

    CalendarItemData defaultCalendarItemData;
    if (recurId == null)
    {
      defaultCalendarItemData = new CalendarItemData(updateInvitePartStat(mailbox, invitedUser, partStat, defaultInvite), parsedMessage);
    }
    else
    {
      defaultCalendarItemData = new CalendarItemData(defaultInvite, parsedMessage);
    }

    mailbox.setCalendarItem(
      operationContext,
      getFolderId(),
      getFlagBitmask(),
      getTags(),
      defaultCalendarItemData,
      newExceptions,
      updateAttendeePartStat(invitedUser, partStat, time, sequence, recurId),
      0L
    );
  }

  private Invite updateInvitePartStat(Mailbox mailbox, Account invitedUser, String partStat, Invite invite)
  {
    InviteFactory inviteFactory = new InviteFactory();
    inviteFactory.populateFactoryFromExistingInvite(invite);
    List<Attendee> attendees = invite.getAttendees();
    ListIterator<Attendee> attendeesIterator = attendees.listIterator();
    while (attendeesIterator.hasNext())
    {
      Attendee attendee = attendeesIterator.next();
      if (invitedUser.addressMatchesAccount(attendee.getAddress()))
      {
        attendeesIterator.set(new Attendee(
          attendee.getAddress(), attendee.getName(), AttendeeInviteStatus.fromZimbra(partStat), attendee.getType())
        );
      }
    }

    inviteFactory.setAttendeeList(attendees);
    return inviteFactory.createAppointment(mailbox);
  }

  private List<com.zimbra.cs.mailbox.CalendarItem.ReplyInfo> updateAttendeePartStat(
    Account invitedUser,
    String partStat,
    long time,
    int sequence,
    RecurrenceId recurId
  )
  {
    boolean updated = false;
    List<com.zimbra.cs.mailbox.CalendarItem.ReplyInfo> replies = mCalendarItem.getAllReplies();
    ListIterator<com.zimbra.cs.mailbox.CalendarItem.ReplyInfo> repliesIterator = replies.listIterator();
    while (repliesIterator.hasNext())
    {
      com.zimbra.cs.mailbox.CalendarItem.ReplyInfo reply = repliesIterator.next();
      ZAttendee attendee = reply.getAttendee();
      if (invitedUser.addressMatchesAccount(reply.getAttendee().getAddress()))
      {
        attendee.setPartStat(partStat);
        repliesIterator.set(new com.zimbra.cs.mailbox.CalendarItem.ReplyInfo(
          attendee,
          sequence,
          time,
          recurId == null ? null : recurId.toZimbra(RecurId.class)
        ));
        updated = true;
      }
    }

    if (!updated)
    {
      ZAttendee attendee = new ZAttendee(invitedUser.getMail());
      attendee.setPartStat(partStat);
      replies.add(new com.zimbra.cs.mailbox.CalendarItem.ReplyInfo(
        attendee,
        sequence,
        time,
        recurId == null ? null : recurId.toZimbra(RecurId.class)
      ));
    }

    return replies;
  }
}
