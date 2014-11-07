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

package org.openzal.zal.calendar;

import com.zimbra.cs.mailbox.MailItem;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Item;
import org.openzal.zal.Mailbox;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.lib.ActualClock;
import org.openzal.zal.lib.Clock;
import com.zimbra.common.service.ServiceException;

import com.zimbra.cs.mailbox.calendar.*;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ZRecur.ZWeekDay;
   $else$ */
import com.zimbra.common.calendar.*;
/* $endif$ */

import javax.mail.internet.MimeMessage;
import java.util.*;


public class InviteFactory
{
  private                String             mMethod;
  private                MapTimeZone        mTimeZoneMap;
  private                String             mUid;
  private                GlobalInviteStatus mStatus;
  private                Priority           mPriority;
  private                int                mPercentage;
  private                long               mCompletedTime;
  private                FreeBusyStatus     mFreeBusyStatus;
  private                Sensitivity        mSensitivity;
  private                boolean            mAllDayEvent;
  private                long               mUtcDateStart;
  private                long               mUtcDateEnd;
  private                long               mExceptionStartTime;
  private                String             mOrganizerAddress;
  private                String             mOrganizerName;
  private                List<Attendee>     mAttendeeList;
  private                String             mSubject;
  private                String             mLocation;
  private                String             mDescription;
  private                String             mDescriptionHtml;
  private                long               mLastModifyTimeUtc;
  private                int                mSequence;
  private                ICalendarTimezone  mTimezone;
  private                boolean            mAlarmSet;
  private                int                mAlarmTime;
  private                long               mReminderTime;
  private                MimeMessage        mMimeMessage;
  private                boolean            mHasAttachment;
  @NotNull
  private final          Clock              mClock;
  private                RecurrenceRule     mRecurrenceRule;

  public InviteFactory()
  {
    mAlarmSet = false;
    mSequence = 0;
    mClock = ActualClock.sInstance;
  }

  public Invite createTask(Mailbox mbox)
  {
    return createInvite(mbox, true);
  }

  public Invite createAppointment(Mailbox mbox)
  {
    return createInvite(mbox, false);
  }

  public void setTypeRequest()
  {
    mMethod = ZCalendar.ICalTok.REQUEST.toString();
  }

  public void setTypeCancel()
  {
    mMethod = ZCalendar.ICalTok.CANCEL.toString();
  }

  public void setTimezoneMap(MapTimeZone timeZoneMap)
  {
    mTimeZoneMap = timeZoneMap;
  }

  public void setTimezone(ICalendarTimezone timezone)
  {
    mTimezone = timezone;
  }

  public void setUid(String uid)
  {
    mUid = uid;
  }

  public void setStatus(GlobalInviteStatus globalInviteStatus)
  {
    mStatus = globalInviteStatus;
  }

  public void setPriority(Priority priority)
  {
    mPriority = priority;
  }

  public void setTaskPercentageCompleted(int percentage)
  {
    mPercentage = percentage;
  }

  public void setUtcTaskCompletedTime(long completedTime)
  {
    mCompletedTime = completedTime;
  }

  public void setFreeBusyStatus(FreeBusyStatus freeBusyStatus)
  {
    mFreeBusyStatus = freeBusyStatus;
  }

  public void setSensitivity(Sensitivity sensitivity)
  {
    mSensitivity = sensitivity;
  }

  public void setAllDayEvent(boolean allDayEvent)
  {
    mAllDayEvent = allDayEvent;
  }

  public void setUtcDateStart( long utcDateStart )
  {
    mUtcDateStart = utcDateStart;
  }

  public void setUtcDateEnd( long utcDateEnd )
  {
    mUtcDateEnd = utcDateEnd;
  }

  public void setExceptionStartTime( long exceptionStartTime )
  {
    mExceptionStartTime = exceptionStartTime;
  }

  public void setOrganizer( String organizerAddress, String organizerName )
  {
    mOrganizerAddress = organizerAddress;
    mOrganizerName = organizerName;
  }

  public void setAttendeeList(List<Attendee> attendeeList)
  {
    mAttendeeList = attendeeList;
  }

  public void setSubject(String subject)
  {
    mSubject = subject;
  }

  public void setLocation( String location )
  {
    mLocation = location;
  }

  public void setDescription( String description, String descriptionHtml )
  {
    mDescription = description;
    mDescriptionHtml = descriptionHtml;
  }

  public void setUtcLastModifyTime(long lastModifyTimeUtc)
  {
    mLastModifyTimeUtc = lastModifyTimeUtc;
  }

  public void setSequence( int sequence )
  {
    mSequence = sequence;
  }

  public int getSequence()
  {
    return mSequence;
  }

  public void setAlarm( int minutesBeforeStart )
  {
    mAlarmTime = minutesBeforeStart;
    mAlarmSet = true;
  }

  public void setReminderTime(long time)
  {
    mAlarmSet = true;
    mReminderTime = time;
  }

  public void setAttachment( MimeMessage mimeMessage )
  {
    mHasAttachment = true;
    mMimeMessage = mimeMessage;
  }

  public void populateFactoryFromExistingInvite( Invite invite )
  {
    mUid = invite.getUid();
    mStatus = invite.getStatus();
    mHasAttachment = invite.hasAttachment();
    if( mHasAttachment )
    {
      mMimeMessage = invite.getAttachment();
    }

    mAttendeeList = invite.getAttendees();
    mAllDayEvent = invite.isAllDayEvent();
    mCompletedTime = invite.getUtcDateCompleted();
    mDescription = invite.getDescription();
    mDescriptionHtml = invite.getDescriptionHtml();
    mFreeBusyStatus = invite.getFreeBusy();
    mLocation = invite.getLocation();
    mPriority = invite.getPriority();
    mSensitivity = invite.getSensitivity();
    mMethod = invite.getMethod();

    mAlarmSet = invite.hasAlarm();
    if( mAlarmSet )
    {
      mAlarmTime = invite.getAlarmMinutesBeforeStart();
    }

    mOrganizerAddress = invite.getOrganizer().getAddress();
    mOrganizerName = invite.getOrganizer().getName();
    mSequence = invite.getSequence() + 1;
    mPercentage = invite.getTaskPercentComplete();
    mSubject = invite.getSubject();
    mUtcDateStart = invite.getUtcStartTime();
    mUtcDateEnd = invite.getUtcEndTime();
    mLastModifyTimeUtc = mClock.now();
    mTimezone = invite.getTimezone();
    mTimeZoneMap = invite.getTimezoneMap();
    if( invite.hasRecurId() )
    {
      mExceptionStartTime = invite.getRecurId().getExceptionStartTimeUtc();
    }
    else
    {
      mExceptionStartTime = 0L;
    }
  }


  private Invite createInvite(Mailbox mbox, boolean task)
  {
    byte type = (task ? Item.TYPE_TASK : Item.TYPE_APPOINTMENT);

    RecurId recurId;
    if(mExceptionStartTime != 0L)
    {
      recurId = new RecurId(
        ParsedDateTime.fromUTCTime(mExceptionStartTime, mTimezone.toZimbra(ICalTimeZone.class)),
        RecurId.RANGE_NONE
      );
    }
    else
    {
      recurId = null;
    }

    boolean isOrganizer = mbox.getAccount().hasAddress(mOrganizerAddress);
    ZOrganizer organizer = new ZOrganizer(mOrganizerAddress, mOrganizerName);

    List<ZAttendee> zAttendeeList = new LinkedList<ZAttendee>();
    for (Attendee attendee : mAttendeeList)
    {
      ZAttendee zAttendee = new ZAttendee(
        attendee.getAddress(), attendee.getName(),
        null, null, null, null, "REQ", attendee.getStatus().getRawStatus(),
        true, null, null, null, null
      );
      zAttendeeList.add(zAttendee);
    }

    ParsedDateTime dateStart = null;
    if( mUtcDateStart != 0 ) {
      dateStart = ParsedDateTime.fromUTCTime(mUtcDateStart, mTimezone.toZimbra(ICalTimeZone.class));
    }

    ParsedDateTime dateEnd = null;
    if( mUtcDateEnd != 0 ) {
      dateEnd = ParsedDateTime.fromUTCTime(mUtcDateEnd, mTimezone.toZimbra(ICalTimeZone.class));
    }

    if (mAllDayEvent)
    {
      dateStart.setHasTime(false);
      dateEnd.setHasTime(false);
    }

    Recurrence.RecurrenceRule mainRecurrenceRule = null;
    if (mRecurrenceRule != null)
    {
      ParsedDuration eventDuration = dateEnd.difference(dateStart);
      Recurrence.IRecurrence simpleRecurrenceRule = new Recurrence.SimpleRepeatingRule(dateStart,
                                                                                       eventDuration,
                                                                                       mRecurrenceRule.toZimbra(ZRecur.class),
                                                                                       null);
      mainRecurrenceRule = new Recurrence.RecurrenceRule(dateStart,
                                                         eventDuration,
                                                         null,
                                                         Arrays.asList(simpleRecurrenceRule),
                                                         new ArrayList<Recurrence.IRecurrence>());
    }

    setTypeRequest();

    com.zimbra.cs.mailbox.calendar.Invite invite = com.zimbra.cs.mailbox.calendar.Invite.createInvite(
      mbox.getId(),
  /* $if MajorZimbraVersion >= 8 $ */
      Item.convertType(MailItem.Type.class, type),
  /* $else$
      Item.convertType(Byte.class, type),
  /* $endif$ */
      mMethod,
      mTimeZoneMap.toZimbra(TimeZoneMap.class),
      mUid,
      mStatus.getRawStatus(),
      mPriority.getRawPriority(),
      String.valueOf(mPercentage),
      mCompletedTime,
      mFreeBusyStatus.getRawFreeBusyStatus(),
      IcalXmlStrMap.TRANSP_OPAQUE,
      mSensitivity.getRawSensitivity(),
      mAllDayEvent,
      dateStart,
      dateEnd,
      null,
      recurId,
      mainRecurrenceRule,
      isOrganizer,
      organizer,
      zAttendeeList,
      mSubject,
      mLocation,
      mDescription,
      mDescriptionHtml,
      null,
      null,
      null,
      null,
      null,
      mLastModifyTimeUtc,
/* $if ZimbraVersion >= 7.0.1 $ */
      mLastModifyTimeUtc,
/* $endif$ */
      mSequence,
/* $if ZimbraVersion > 8.0.1 |! ZimbraVersion > 7.2.3  && ZimbraVersion != 8.0.0 && ZimbraVersion != 8.0.1 $ */
      mSequence,
/* $endif$ */
      AttendeeInviteStatus.TENTATIVE.getRawStatus(),
      true,
      true
    );

    try
    {
      if( mAlarmSet )
      {
        Alarm alarm;
        if (task)
        {
          alarm = Alarm.fromSimpleTime(ParsedDateTime.fromUTCTime(mReminderTime));
        }
        else
        {
          alarm = Alarm.fromSimpleReminder(mAlarmTime);
        }

        invite.addAlarm(alarm);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if( mHasAttachment )
    {
      invite.setHasAttachment(true);
      return new Invite(invite, mMimeMessage);
    }
    else
    {
      return new Invite(invite);
    }
  }

  public void setRecurrenceRule(RecurrenceRule recurrenceRule)
  {
    mRecurrenceRule = recurrenceRule;
  }
}
