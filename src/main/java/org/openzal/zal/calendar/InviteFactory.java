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

import com.zimbra.common.calendar.ICalTimeZone;
import com.zimbra.common.calendar.ParsedDateTime;
import com.zimbra.common.calendar.ParsedDuration;
import com.zimbra.common.calendar.TimeZoneMap;
import com.zimbra.common.calendar.ZCalendar;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.calendar.Alarm;
import com.zimbra.cs.mailbox.calendar.IcalXmlStrMap;
import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.mailbox.calendar.Recurrence;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mailbox.calendar.ZOrganizer;
import com.zimbra.cs.mailbox.calendar.ZRecur;
import com.zimbra.cs.mailbox.calendar.Recurrence.IRecurrence;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.internet.MimeMessage;
import org.openzal.zal.Item;
import org.openzal.zal.Mailbox;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.ActualClock;
import org.openzal.zal.lib.Clock;


public class InviteFactory
{

  private static final long MINUTES_30 = TimeUnit.MINUTES.toMillis(30);

  private       String             mMethod;
  private       MapTimeZone        mTimeZoneMap;
  private       String             mUid;
  private       GlobalInviteStatus mStatus;
  private       Priority           mPriority;
  private       int                mPercentage;
  private       long               mCompletedTime;
  private       FreeBusyStatus     mFreeBusyStatus;
  private       Sensitivity        mSensitivity;
  private       boolean            mAllDayEvent;
  private       long               mUtcDateStart;
  private       long               mUtcDateEnd;
  private       long               mExceptionStartTime;
  private       String             mOrganizerAddress;
  private       String             mOrganizerName;
  private       String             sentByAddress;
  private       List<Attendee>     mAttendeeList;
  private       String             mSubject;
  private       String             mLocation;
  private       String             mDescription;
  private       String             mDescriptionHtml;
  private       long               mLastModifyTimeUtc;
  private       int                mSequence;
  private       ICalendarTimezone mTimezone;
  private       boolean           mAlarmSet;
  private       int               mAlarmTime;
  private       long              mReminderTime;
  private       MimeMessage       mMimeMessage;
  private       boolean           mHasAttachment;
  @Nonnull
  private final Clock             mClock;
  private       RecurrenceRule    mRecurrenceRule;
  private       int               mMailItemId = 0;
  private       String            mPartStat;
  private       boolean           mResponseRequest;
  private       List<Attach>      mICalAttachmentList;
  private boolean isAtLeastEas16;

  public InviteFactory()
  {
    mAlarmSet = false;
    mSequence = 0;
    mClock = ActualClock.sInstance;

    this.sentByAddress = null;

    mICalAttachmentList = new ArrayList<>();
  }

  public Invite createAppointment(Mailbox mbox)
  {
    return createInvite(mbox);
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

  public void setOrganizer( String organizerAddress, String organizerName ) {
    mOrganizerAddress = organizerAddress;
    mOrganizerName = organizerName;
  }

  public void setSentByAddress(String sentByAddress) {
    this.sentByAddress = sentByAddress;
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

  public void setDescription( String description )
  {
    mDescription = description;
  }

  public void setDescriptionHtml( String html )
  {
    mDescriptionHtml = html;
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

  public void setAttachment(@Nullable MimeMessage mimeMessage )
  {
    if(mimeMessage != null)
    {
      mHasAttachment = true;
    }
    mMimeMessage = mimeMessage;
  }

  public void setResponseRequest(Boolean rsvp)
  {
    mResponseRequest = rsvp;
  }

  public void addICalAttach(Attach attachment)
  {
    mICalAttachmentList.add(attachment);
    mHasAttachment = true;
  }

  public void addICalAttaches(Iterable<Attach> attachments)
  {
    for( Attach attachment : attachments)
    {
      addICalAttach(attachment);
    }
  }

  public void setIsAtLeastEas16(boolean isAtLeastEas16) {
    this.isAtLeastEas16 = isAtLeastEas16;
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

    mRecurrenceRule = invite.getRecurrenceRule();
    if (invite.hasOrganizer()) {
      Attendee organizer = invite.getOrganizer();
      mOrganizerAddress = organizer.getAddress().trim().isEmpty() ? null : organizer.getAddress();
      mOrganizerName = organizer.getName();
    } else {
      mOrganizerAddress = null;
      mOrganizerName = null;
    }
    sentByAddress = invite.getSentBy();
    mSequence = invite.getSequence();
    mPercentage = invite.getTaskPercentComplete();
    mSubject = invite.getSubject();
    mTimezone = invite.getTimezone();
    if( mAllDayEvent ) {
      mUtcDateStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(invite.getUtcStartTime()), ZoneId.of(mTimezone.getID())).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
      mUtcDateEnd = LocalDateTime.ofInstant(Instant.ofEpochMilli(invite.getUtcEndTime()), ZoneId.of(mTimezone.getID())).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    } else {
      mUtcDateStart = invite.getUtcStartTime();
      mUtcDateEnd = invite.getUtcEndTime();
    }
    mLastModifyTimeUtc = mClock.now();
    mTimeZoneMap = invite.getTimezoneMap();
    if( invite.hasRecurId() )
    {
      mExceptionStartTime = invite.getRecurId().getExceptionStartTimeUtc();
    }
    else
    {
      mExceptionStartTime = 0L;
    }
    mMailItemId = invite.getMailItemId();
    mPartStat = invite.getPartStat();
    if(invite.hasResponseRequest()) {
      mResponseRequest = invite.getResponseRequest();
    }
    mICalAttachmentList = invite.getICalAttachList();
  }


  private Invite createInvite(Mailbox mbox)
  {
    byte type = Item.TYPE_APPOINTMENT;

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

    if(Objects.isNull(mOrganizerAddress) || mOrganizerAddress.trim().isEmpty()) {
      mOrganizerAddress = mbox.getAccount().getName();
    }

    ZOrganizer organizer = new ZOrganizer(mOrganizerAddress, Optional.ofNullable(mOrganizerName).orElse(""));
    organizer.setSentBy(sentByAddress);

    // This flag says whether the mailbox owner is the organizer of this event
    boolean isOrganizer = mbox.getAccount().hasAddress(mOrganizerAddress);

    List<ZAttendee> zAttendeeList = new LinkedList<>();
    for (Attendee attendee : mAttendeeList)
    {
      zAttendeeList.add(attendee.toZAttendee());
    }

    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    // see https://docs.microsoft.com/en-us/openspecs/exchange_server_protocols/ms-ascal/d36fecc3-9224-4c65-b58d-b4ddb354e93e
    if( mUtcDateStart == 0L && mUtcDateEnd == 0L) {
      mUtcDateStart = cal.getTimeInMillis();
      mUtcDateEnd = mUtcDateStart + MINUTES_30;
    }
    else if(mUtcDateStart == 0L && mUtcDateEnd < cal.getTimeInMillis())
    {
      throw new ZimbraException("EndDate can not be in the past!");
    }
    else if(mUtcDateStart == 0L && mUtcDateEnd > cal.getTimeInMillis())
    {
      mUtcDateStart = cal.getTimeInMillis();
    }
    else if(mUtcDateStart < cal.getTimeInMillis() && mUtcDateEnd == 0L)
    {
      mUtcDateEnd = mUtcDateStart + MINUTES_30;
    }
    else if(mUtcDateStart > cal.getTimeInMillis() && mUtcDateEnd == 0L)
    {
      throw new ZimbraException("StartDate can not be in the future if end time is not specified");
    }

    ParsedDateTime dateStart;
    ParsedDateTime dateEnd;
    if (mAllDayEvent && isAtLeastEas16) {
      if(TimeUnit.MILLISECONDS.toHours(mUtcDateEnd - mUtcDateStart) < 24) {
        mUtcDateEnd = mUtcDateStart + TimeUnit.HOURS.toMillis(24);
      }
      dateStart = ParsedDateTime.fromUTCTime(mUtcDateStart, ICalTimeZone.getUTC());
      dateEnd = ParsedDateTime.fromUTCTime(mUtcDateEnd, ICalTimeZone.getUTC());
    }
    else {
      dateStart = ParsedDateTime.fromUTCTime(mUtcDateStart, mTimezone.toZimbra(ICalTimeZone.class));
      dateEnd = ParsedDateTime.fromUTCTime(mUtcDateEnd, mTimezone.toZimbra(ICalTimeZone.class));
    }

    if (mAllDayEvent)
    {
        dateStart.setHasTime(false);
        dateEnd.setHasTime(false);
    }

    Recurrence.RecurrenceRule mainRecurrenceRule = null;
    ParsedDuration eventDuration = null;
    if (mRecurrenceRule != null)
    {
      if (!mAllDayEvent) {
        eventDuration = dateEnd.difference(dateStart);
      }
      Recurrence.IRecurrence simpleRecurrenceRule = new Recurrence.SimpleRepeatingRule(dateStart,
                                                                                       eventDuration,
                                                                                       mRecurrenceRule.toZimbra(ZRecur.class),
                                                                                       null);
      mainRecurrenceRule = new Recurrence.RecurrenceRule(dateStart,
                                                         eventDuration,
                                                         null,
                                                         Collections.singletonList(simpleRecurrenceRule),
                                                         Collections.<IRecurrence>emptyList());
    }

    setTypeRequest();

    com.zimbra.cs.mailbox.calendar.Invite invite = com.zimbra.cs.mailbox.calendar.Invite.createInvite(
      mbox.getId(),
      Item.convertType(MailItem.Type.class, type),
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
      eventDuration,
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
      mLastModifyTimeUtc,
      mSequence,
      mSequence,
      isOrganizer ? AttendeeInviteStatus.ACCEPTED.getRawStatus() : mPartStat,
      mResponseRequest,
      true
    );

    try
    {
      if( mAlarmSet )
      {
        Alarm alarm;
        alarm = Alarm.fromSimpleReminder(mAlarmTime);

        invite.addAlarm(alarm);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if (mMailItemId > 0)
    {
      invite.setInviteId(mMailItemId);
    }

    if( mHasAttachment )
    {
      invite.setHasAttachment(true);

      Invite newInvite = new Invite(invite, mMimeMessage);

      newInvite.addICalAttaches(mICalAttachmentList);

      return newInvite;
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

  // PartStat => Participation Status
  public void setPartStat(String partStat)
  {
    mPartStat = partStat;
  }
}
