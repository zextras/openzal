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

import com.zimbra.cs.account.Account;
import org.openzal.zal.Utils;
import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZimbraListWrapper;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.common.service.ServiceException;

import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.util.*;
import org.openzal.zal.log.ZimbraLog;

import com.zimbra.cs.mailbox.calendar.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ZRecur.ZWeekDay;
   $else$ */
import com.zimbra.common.calendar.*;
/* $endif$ */

public class ZEInvite
{
  private static String TRIGGER_TYPE_FIELD = "mTriggerType";
  private static String TRIGGER_RELATED_FIELD = "mTriggerRelated";

  public MimeMessage getAttachment()
  {
    return mMimeMessage;
  }

  private final MimeMessage mMimeMessage;
  private final Invite      mInvite;

  private static Field sTriggerTypeField = null;
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
    catch( Throwable ex ){
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
    }
  }

  public ZEInvite(Object invite)
  {
    this(invite, null);
  }

  public ZEInvite(@NotNull Object invite, MimeMessage mimeMessage)
  {
    if ( invite == null )
    {
      throw new NullPointerException();
    }
    mInvite = (Invite)invite;
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
    if(mInvite != null)
    {
      return mInvite.getSeqNo();
    }

    return 0;
  }

  public boolean isRecurrence()
  {
    return mInvite.isRecurrence();
  }

  public String getLocation()
  {
    return mInvite.getLocation();
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

          if (Alarm.TriggerRelated.END.equals(triggerRelated))
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
      if(mInvite == null)
      {
        return "";
      }

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

  public Attendee getOrganizer()
  {
    ZOrganizer organizer = mInvite.getOrganizer();
    return new Attendee(organizer.getAddress(), organizer.getCn(), AttendeeInviteStatus.ACCEPTED);
  }

  public long getUtcLastModify()
  {
    return mInvite.getDTStamp();
  }

  public boolean hasStartTime()
  {
    return mInvite.getStartTime() != null;
  }

  public Date getStartTimeDate()
  {
    return mInvite.getStartTime().getDate();
  }

  public Date getEndTimeDate()
  {
    return mInvite.getEffectiveEndTime().getDate();
  }

  public long getUtcStartTime()
  {
    //TODO is mInvite null?
    ParsedDateTime parsedDateTime = mInvite.getStartTime();
    if (parsedDateTime == null)
    {
      return 0L;
    }
    return parsedDateTime.getDate().getTime();
  }

  public ZERecurrenceRule getRecurrenceRule()
  {
    Recurrence.IRecurrence recurrence = mInvite.getRecurrence();
    ZRecur zrec = ((Recurrence.SimpleRepeatingRule) recurrence.addRulesIterator().next()).getRule();
    return new ZERecurrenceRule(zrec);
  }

  /*
    warning: it only works AFTER you added the calendar to the mailbox (it uses calendar item)
  */
  public List<ZEInvite> getExceptionInstances()
  {
    List<ZEInvite> inviteList = new LinkedList<ZEInvite>();

    Recurrence.RecurrenceRule recurrence = (Recurrence.RecurrenceRule) mInvite.getRecurrence();
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
        Invite invite = mInvite.getCalendarItem().getInvite(exception.getRecurId());
        inviteList.add(new ZEInvite(invite));
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

    Recurrence.RecurrenceRule recurrence = (Recurrence.RecurrenceRule) mInvite.getRecurrence();
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

  @Nullable
  public ZERecurId getRecurId()
  {
    if( mInvite != null )
    {
      if (mInvite.hasRecurId())
      {
        return new ZERecurId(mInvite.getRecurId().getDt().getUtcTime());
      }
      return new ZERecurId( mInvite.getStartTime().getUtcTime() );
    }
    return null;
  }

  public boolean hasRecurId()
  {
    if( mInvite != null)
    {
      return mInvite.hasRecurId();
    }
    return false;
  }

  public String getSubject()
  {
    if( mInvite != null )
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
      if( mInvite != null )
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
    if( mInvite != null )
    {
      return Priority.fromZimbra(mInvite.getPriority());
    }
    return null;
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
    return new Attendee(
      attendee.getAddress(),
      attendee.getCn(),
      AttendeeInviteStatus.fromZimbra(attendee.getPartStat())
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
    if( mInvite != null )
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
    return new ArrayList<Attendee>();
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
    if( mInvite != null )
    {
      String percent = mInvite.getPercentComplete();
      if( percent != null )
      {
        return Integer.valueOf(percent);
      }
    }
    return 0;
  }

  public ZEIcalTimezone getTimezone()
  {
    ParsedDateTime startTime = mInvite.getStartTime();
    if (startTime != null)
    {
      return new ZEIcalTimezone(startTime.getTimeZone());
    }

    ParsedDateTime endTime = mInvite.getEndTime();
    if (endTime != null)
    {
      return new ZEIcalTimezone(endTime.getTimeZone());
    }

    return new ZEIcalTimezone(mInvite.getTimeZoneMap().getLocalTimeZone());
  }

  public ZETimeZoneMap getTimezoneMap()
  {
    return new ZETimeZoneMap(mInvite.getTimeZoneMap());
  }

  static List<ZEInvite> createFromCalendar(ZEAccount account, ZCalendar.ZVCalendar cal, boolean sentByMe)
    throws ZimbraException
  {
    try
    {
      List<Invite> inviteList = Invite.createFromCalendar(
        account.toZimbra(Account.class),
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
    return mInvite.getEndTime() != null;
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

  public ZEInvite newCopy()
  {
    /* $if ZimbraVersion >= 6.0.13 && ZimbraVersion != 7.0.0 && ZimbraVersion < 8.0.0 $
    try
    {
      return new ZEInvite(mInvite.newCopy());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $ */
    return new ZEInvite(mInvite.newCopy());
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
}