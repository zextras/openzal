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

import com.google.common.annotations.VisibleForTesting;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Metadata;
import org.openzal.zal.Item;
import org.openzal.zal.Utils;
import org.openzal.zal.Account;
import org.openzal.zal.ZimbraListWrapper;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.common.service.ServiceException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import org.openzal.zal.log.ZimbraLog;

import com.zimbra.cs.mailbox.calendar.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.mailbox.calendar.*;
   $else$ */
import com.zimbra.common.calendar.*;
/* $endif$ */

public class Invite
{
  private static String TRIGGER_TYPE_FIELD    = "mTriggerType";
  private static String TRIGGER_RELATED_FIELD = "mTriggerRelated";

  public MimeMessage getAttachment()
  {
    return mMimeMessage;
  }

  private final MimeMessage                           mMimeMessage;
  private final com.zimbra.cs.mailbox.calendar.Invite mInvite;

  private static Field sTriggerTypeField    = null;
  private static Field sTriggerRelatedField = null;

  static
  {
    try
    {
      sTriggerTypeField = Alarm.class.getDeclaredField(TRIGGER_TYPE_FIELD);
      sTriggerRelatedField = Alarm.class.getDeclaredField(TRIGGER_RELATED_FIELD);

      sTriggerTypeField.setAccessible(true);
      sTriggerRelatedField.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public Invite(Object invite)
  {
    this(invite, null);
  }

  public Invite(@NotNull Object invite, MimeMessage mimeMessage)
  {
    if (invite == null)
    {
      throw new NullPointerException();
    }
    mInvite = (com.zimbra.cs.mailbox.calendar.Invite) invite;
    if (mimeMessage == null)
    {
      if (mInvite.hasAttachment())
      {
        try
        {
          mimeMessage = mInvite.getMimeMessage();
        }
        catch (ServiceException ignored) {}
      }
    }
    mMimeMessage = mimeMessage;
  }

  public boolean isRecurrent()
  {
    return mInvite.isRecurrence() && mInvite.getRecurrence() != null;
  }

  public boolean hasAttachment()
  {
    return mInvite.hasAttachment();
  }

  public int getSequence()
  {
    return mInvite.getSeqNo();
  }

  public boolean isRecurrence()
  {
    return mInvite.isRecurrence();
  }

  public String getLocation()
  {
    String location = mInvite.getLocation();
    if( location == null )
    {
      return "";
    }
    else
    {
      return location;
    }
  }

  public long getUtcDateCompleted()
  {
    return mInvite.getCompleted();
  }

  public GlobalInviteStatus getStatus()
  {
    return GlobalInviteStatus.fromZimbra(mInvite.getStatus());
  }

  public boolean hasFreeBusy()
  {
    return mInvite.hasFreeBusy();
  }

  public boolean hasAlarm()
  {
    if (mInvite.hasAlarm())
    {
      Alarm alarm = getDisplayAlarm();

      if (alarm != null)
      {
        Alarm.TriggerType triggerType = getTriggerType(alarm);
        if (Alarm.TriggerType.ABSOLUTE.equals(triggerType))
        {
          return true;
        }

        Alarm.TriggerRelated triggerRelated = getTriggerRelated(alarm);
        if (Alarm.TriggerType.RELATIVE.equals(triggerType))
        {
          if (Alarm.TriggerRelated.START.equals(triggerRelated))
          {
            return hasStartTime();
          }

          if (Alarm.TriggerRelated.END.equals(triggerRelated) || triggerRelated == null)
          {
            return hasEndDate();
          }
        }
      }
    }

    return false;
  }

  private Alarm.TriggerType getTriggerType(Alarm alarm)
  {
    try
    {
      return (Alarm.TriggerType) sTriggerTypeField.get(alarm);
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }

  private Alarm.TriggerRelated getTriggerRelated(Alarm alarm)
  {
    try
    {
      return (Alarm.TriggerRelated) sTriggerRelatedField.get(alarm);
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }

  private Alarm getDisplayAlarm()
  {
    for (Alarm alarm : mInvite.getAlarms())
    {
      if (alarm.getAction().equals(Alarm.Action.DISPLAY))
      {
        return alarm;
      }
    }

    return null;
  }

  public int getAlarmMinutesBeforeStart()
  {
    Alarm alarm = getDisplayAlarm();
    long alarmTime = alarm.getTriggerTime(getUtcStartTime(), getUtcEndTime());
    int alarmMins = (int) (((getUtcStartTime() - alarmTime) / 1000L) / 60L);
    return alarmMins;
  }

  public long getUTCAlarmAbsoluteTime()
  {
    Alarm alarm = getDisplayAlarm();
    return alarm.getTriggerTime(getUtcStartTime(), getUtcEndTime());
  }

  public FreeBusyStatus getFreeBusy()
  {
    return FreeBusyStatus.fromZimbra(mInvite.getFreeBusy());
  }

  public String getUid()
  {
    return mInvite.getUid();
  }

  public String getDescription()
  {
    try
    {
      String InviteDescription = mInvite.getDescription();
      if( InviteDescription != null )
      {
        return InviteDescription;
      }
      else
      {
        return "";
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public Attendee getOrganizer()
  {
    ZOrganizer organizer = mInvite.getOrganizer();
    if (organizer == null)
    {
      return null;
    }
    return new Attendee(organizer.getAddress(), organizer.getCn(), AttendeeInviteStatus.ACCEPTED, AttendeeType.Required);
  }

  @VisibleForTesting
  public void setOrganizer(String address, String cn)
  {
    try
    {
      if (address != null || cn != null)
      {
        mInvite.setOrganizer(new ZOrganizer(address, cn));
      }
      else
      {
        mInvite.setOrganizer(null);
      }
    }
    catch (Exception ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  @VisibleForTesting
  public void setIsOrganizer(boolean b)
  {
    mInvite.setIsOrganizer(b);
  }

  public long getUtcLastModify()
  {
    return mInvite.getDTStamp();
  }

  public boolean hasStartTime()
  {
    return mInvite.getStartTime() != null;
  }

  @Nullable
  public Date getStartTimeDate()
  {
    ParsedDateTime startTime = mInvite.getStartTime();
    if (startTime == null)
    {
      return null;
    }

    return startTime.getDate();
  }

  @Nullable
  public Date getEndTimeDate()
  {
    ParsedDateTime endTime = mInvite.getEffectiveEndTime();
    if (endTime == null)
    {
      return null;
    }

    return endTime.getDate();
  }

  public long getEffectiveDuration()
  {
    try
    {
      return Math.abs(mInvite.getEffectiveDuration().subtractFromTime(0));
    }
    catch (Exception ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  public long getUtcStartTime()
  {
    ParsedDateTime parsedDateTime = mInvite.getStartTime();
    if (parsedDateTime == null)
    {
      return 0L;
    }
    return parsedDateTime.getDate().getTime();
  }

  @Nullable
  public RecurrenceRule getRecurrenceRule()
  {
    Recurrence.IRecurrence recurrence = mInvite.getRecurrence();
    if (recurrence == null)
    {
      return null;
    }
    ZRecur zrec = ((Recurrence.SimpleRepeatingRule) recurrence.addRulesIterator().next()).getRule();
    return new RecurrenceRule(zrec);
  }

  /*
    warning: it only works AFTER you added the calendar to the mailbox (it uses calendar item)
  */
  public List<Invite> getExceptionInstances()
  {
    List<Invite> inviteList = new LinkedList<Invite>();

    CalendarItem calendarItem;
    try
    {
      calendarItem = mInvite.getCalendarItem();
    }
    catch( Exception ex )
    {
      throw ExceptionWrapper.wrap(ex);
    }

    if(calendarItem == null || (!calendarItem.isRecurring())) {
      return Collections.emptyList();
    }

    Recurrence.RecurrenceRule recurrence;
    recurrence = (Recurrence.RecurrenceRule) calendarItem.getRecurrence();

    Iterator<Recurrence.IException> it = recurrence.exceptionsIter();
    while (it.hasNext())
    {
      Recurrence.IException exception = it.next();
      if (exception.getType() != Recurrence.TYPE_EXCEPTION)
      {
        continue;
      }

      try
      {
        com.zimbra.cs.mailbox.calendar.Invite invite = calendarItem.getInvite(exception.getRecurId());
        inviteList.add(new Invite(invite));
      }
      catch (Exception ex)
      {
        throw ExceptionWrapper.wrap(ex);
      }
    }

    return inviteList;
  }

  public List<Invite> getCancellationInstances()
  {
    List<Invite> inviteList = new LinkedList<Invite>();

    CalendarItem calendarItem;
    try
    {
      calendarItem = mInvite.getCalendarItem();
    }
    catch( Exception ex )
    {
      throw ExceptionWrapper.wrap(ex);
    }

    if(calendarItem == null || (!calendarItem.isRecurring())) {
      return Collections.emptyList();
    }

    Recurrence.RecurrenceRule recurrence;
    recurrence = (Recurrence.RecurrenceRule) calendarItem.getRecurrence();

    Iterator<Recurrence.IException> it = recurrence.exceptionsIter();
    while (it.hasNext())
    {
      Recurrence.IException exception = it.next();
      if (exception.getType() != Recurrence.TYPE_CANCELLATION)
      {
        continue;
      }

      try
      {
        com.zimbra.cs.mailbox.calendar.Invite invite = calendarItem.getInvite(exception.getRecurId());
        inviteList.add(new Invite(invite));
      }
      catch (Exception ex)
      {
        throw ExceptionWrapper.wrap(ex);
      }
    }

    return inviteList;
  }

  public List<Long> getStartTimeUtcOfDeletedInstances()
  {
    List<Long> startTimeOfDeletedInstances = new LinkedList<Long>();

    CalendarItem calendarItem;
    try
    {
      calendarItem = mInvite.getCalendarItem();
    }
    catch( Exception ex )
    {
      throw ExceptionWrapper.wrap(ex);
    }

    if(calendarItem == null || (!calendarItem.isRecurring())) {
      return Collections.emptyList();
    }

    Recurrence.RecurrenceRule recurrence;
    recurrence = (Recurrence.RecurrenceRule) calendarItem.getRecurrence();
    
    Iterator<Recurrence.IException> it = recurrence.exceptionsIter();
    while (it.hasNext())
    {
      Recurrence.IException exception = it.next();
      if (exception.getType() != Recurrence.TYPE_CANCELLATION)
      {
        continue;
      }
      startTimeOfDeletedInstances.add(exception.getRecurId().getDt().getUtcTime());
    }

    Iterator<Recurrence.IRecurrence> subIt = recurrence.subRulesIterator();
    while (subIt != null && subIt.hasNext())
    {
      Recurrence.IRecurrence subrec = subIt.next();
      startTimeOfDeletedInstances.add(subrec.getStartTime().getUtcTime());
    }

    return startTimeOfDeletedInstances;
  }

  public long getUtcEndTime()
  {
    ParsedDateTime parsedDateTime = mInvite.getEndTime();
    if (parsedDateTime == null)
    {
      return getUtcStartTime() + 2L*60L*60L*1000L;
    }
    return parsedDateTime.getDate().getTime();
  }

  /*
    valid only if this invite has already been added to zimbra (related to its CalendarItem)
    otherwise it just return mailbox-independent partstat
  */
  public AttendeeInviteStatus getMyOwnInviteStatus()
  {
    try
    {
      return AttendeeInviteStatus.fromZimbra(mInvite.getEffectivePartStat());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getMethod()
  {
    return mInvite.getMethod();
  }

  @NotNull
  public RecurrenceId getRecurId()
  {
    if (mInvite.hasRecurId())
    {
      return new RecurrenceId(mInvite.getRecurId().getDt().getUtcTime());
    }
    throw new RuntimeException("Invalid RecursionId access");
  }

  public boolean hasRecurId()
  {
    return mInvite.hasRecurId();
  }

  public String getSubject()
  {
    if( mInvite.getName() != null )
    {
      return mInvite.getName();
    }
    else
    {
      return "";
    }
  }

  public String getDescriptionHtml()
  {
    try
    {
      if( mInvite.getDescriptionHtml() != null )
      {
        return mInvite.getDescriptionHtml();
      }
      else
      {
        return "";
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Priority getPriority()
  {
    return Priority.fromZimbra(mInvite.getPriority());
  }

  public long getEffectiveEndTime()
  {
    ParsedDateTime parsedDateTime = mInvite.getEffectiveEndTime();
    if (parsedDateTime == null)
    {
      return getUtcStartTime() + 2L*60L*60L*1000L;
    }
    return parsedDateTime.getDate().getTime();
  }

  public boolean isAllDayEvent()
  {
    return mInvite.isAllDayEvent();
  }

  public
  @Nullable
  Attendee getMatchingAttendee(Account account)
  {
    Attendee attendee = getMatchingAttendee(account.getName());
    if( attendee != null )
    {
      return attendee;
    }

    for( String alias : account.getMailAlias() )
    {
      attendee = getMatchingAttendee(alias);
      if( attendee != null )
      {
        return attendee;
      }
    }

    return null;
  }

  public
  @Nullable
  Attendee getMatchingAttendee(String address)
  {
    try
    {
      ZAttendee attendee = mInvite.getMatchingAttendee(address);
      if (attendee != null)
      {
        return convertAttendee(attendee);
      }
      else
      {
        return null;
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  private Attendee convertAttendee(ZAttendee attendee)
  {
    AttendeeType type = AttendeeType.fromString(attendee.getRole());

    return new Attendee(
      attendee.getAddress(),
      attendee.getCn(),
      AttendeeInviteStatus.fromZimbra(attendee.getPartStat()),
      type
    );
  }

  public boolean hasOrganizer()
  {
    return mInvite.hasOrganizer();
  }

  public boolean hasOtherAttendees()
  {
    return mInvite.hasOtherAttendees();
  }

  public List<Attendee> getAttendees()
  {
    List<ZAttendee> zAttendeeList = mInvite.getAttendees();
    List<Attendee> attendeeList = new ArrayList<Attendee>();
    if( zAttendeeList == null )
    {
      return attendeeList;
    }

    for (ZAttendee attendee : zAttendeeList)
    {
      attendeeList.add(convertAttendee(attendee));
    }
    return attendeeList;
  }

  public Sensitivity getSensitivity()
  {
    return Sensitivity.fromZimbra(mInvite.getClassProp());
  }

  public boolean hasSensitivity()
  {
    String classProp = mInvite.getClassProp();
    return classProp != null && !classProp.isEmpty();
  }

  public int getMailItemId()
  {
    return mInvite.getMailItemId();
  }

  public int getTaskPercentComplete()
  {
    String percent = mInvite.getPercentComplete();
    if( percent != null )
    {
      return Integer.valueOf(percent);
    }
    return 0;
  }

  public ICalendarTimezone getTimezone()
  {
    ParsedDateTime startTime = mInvite.getStartTime();
    if (startTime != null)
    {
      return new ICalendarTimezone(startTime.getTimeZone());
    }

    ParsedDateTime endTime = mInvite.getEndTime();
    if (endTime != null)
    {
      return new ICalendarTimezone(endTime.getTimeZone());
    }

    return new ICalendarTimezone(mInvite.getTimeZoneMap().getLocalTimeZone());
  }

  public MapTimeZone getTimezoneMap()
  {
    return new MapTimeZone(mInvite.getTimeZoneMap());
  }

  static List<Invite> createFromCalendar(Account account, ZCalendar.ZVCalendar cal, boolean sentByMe)
    throws ZimbraException
  {
    try
    {
      List<com.zimbra.cs.mailbox.calendar.Invite> inviteList = com.zimbra.cs.mailbox.calendar.Invite.createFromCalendar(
        account.toZimbra(com.zimbra.cs.account.Account.class),
        null,
        cal,
        sentByMe
      );

      return ZimbraListWrapper.wrapInvites(inviteList);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mInvite);
  }

  ZCalendar.ZVCalendar newToICalendar(boolean includePrivateData)
  {
    try
    {
      return mInvite.newToICalendar(includePrivateData);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean hasEndDate()
  {
    return mInvite.getEffectiveEndTime() != null;
  }

  public boolean hasEffectiveEndDate()
  {
    return mInvite.getEffectiveEndTime() != null;
  }

  public long getUtcEffectiveEndDate()
  {
    return mInvite.getEffectiveEndTime().getUtcTime();
  }

  public boolean isCompleted()
  {
    return getStatus().equals(GlobalInviteStatus.TASK_COMPLETED);
  }

  public boolean createdByOrganizer()
  {
    return mInvite.isOrganizer();
  }

  public void setSequence(int sequence)
  {
    mInvite.setSeqNo(sequence);
  }

  public Invite newCopy()
  {
    /* $if ZimbraVersion >= 6.0.13 && ZimbraVersion != 7.0.0 && ZimbraVersion < 8.0.0 $
    try
    {
      return new Invite(mInvite.newCopy());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $ */
    return new Invite(mInvite.newCopy());
    /* $endif $ */

  }

  public void setMailItemId(int id)
  {
    mInvite.setMailItemId(id);
  }

  public boolean methodIsReply()
  {
    ZCalendar.ICalTok method = ZCalendar.ICalTok.lookup( mInvite.getMethod() );
    return method == ZCalendar.ICalTok.REPLY;
  }

  public boolean methodIsCancel()
  {
    ZCalendar.ICalTok method = ZCalendar.ICalTok.lookup( mInvite.getMethod() );
   return method == ZCalendar.ICalTok.CANCEL;
  }

  @Nullable
  public String getBody()
  {
    try
    {
      byte[] content = mInvite.getCalendarItem().getContent();
      if (content == null)
      {
        return null;
      }
      return new String(mInvite.getCalendarItem().getContent(), Charset.defaultCharset());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<String> getTags()
  {
    try
    {
      return Arrays.asList(new Item(mInvite.getCalendarItem()).getTags());
    }
    catch (Throwable t)
    {
      return Collections.emptyList();
    }
  }

  public String getPartStat()
  {
    return mInvite.getPartStat();
  }

  public boolean isPublic()
  {
    return mInvite.isPublic();
  }

  public InputStream toIcal()
    throws ZimbraException, IOException, MessagingException
  {
    MimeBodyPart icalPart;
    try {
      ZCalendar.ZVCalendar cal = mInvite.newToICalendar(true);
  /* $if ZimbraVersion <= 7.1.0 $
      icalPart = CalendarMailSender.makeICalIntoMimePart(null, cal);
  /* $else$ */
      icalPart = CalendarMailSender.makeICalIntoMimePart(cal);
  /* $endif $ */
      return icalPart.getInputStream();
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  public void addAttendee(Map<String, Object> metadata)
  {
    try
    {
      mInvite.addAttendee(new ZAttendee(new Metadata(metadata)));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void addAlarm(Map<String, Object> metadata)
  {
    try
    {
      mInvite.addAlarm(com.zimbra.cs.mailbox.calendar.Alarm.decodeMetadata(new Metadata(metadata)));
    }
    catch (Exception e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}