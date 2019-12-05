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

import com.zimbra.common.calendar.ParsedDateTime;
import com.zimbra.common.calendar.TimeZoneMap;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.ZRecur;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class RecurrenceRule
{
  @Nonnull private final ZRecur mZRecur;

  public enum Frequency
  {
    YEARLY,
    MONTHLY,
    WEEKLY,
    DAILY
  }

  private static final HashMap<ZRecur.Frequency, Frequency> sZimbra2Zal;
  private static final HashMap<Frequency, ZRecur.Frequency> sZal2Zimbra;

  static
  {
    sZimbra2Zal = new HashMap<ZRecur.Frequency, Frequency>(4);
    sZal2Zimbra = new HashMap<Frequency, ZRecur.Frequency>(4);

    sZimbra2Zal.put(ZRecur.Frequency.YEARLY, Frequency.YEARLY);
    sZal2Zimbra.put(Frequency.YEARLY, ZRecur.Frequency.YEARLY);

    sZimbra2Zal.put(ZRecur.Frequency.MONTHLY, Frequency.MONTHLY);
    sZal2Zimbra.put(Frequency.MONTHLY, ZRecur.Frequency.MONTHLY);

    sZimbra2Zal.put(ZRecur.Frequency.WEEKLY, Frequency.WEEKLY);
    sZal2Zimbra.put(Frequency.WEEKLY, ZRecur.Frequency.WEEKLY);

    sZimbra2Zal.put(ZRecur.Frequency.DAILY, Frequency.DAILY);
    sZal2Zimbra.put(Frequency.DAILY, ZRecur.Frequency.DAILY);
  }

  public RecurrenceRule(MapTimeZone tzMap)
  {
    try
    {
      mZRecur = new ZRecur("", tzMap.toZimbra(TimeZoneMap.class));
    }
    catch (ServiceException e)
    {
      throw new RuntimeException("Error initializing ZRecur");
    }
  }

  public RecurrenceRule(Object zRecur)
  {
    mZRecur = (ZRecur) zRecur;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZRecur);
  }

  @Nonnull
  public Frequency getFrequency()
  {
    Frequency frequency = sZimbra2Zal.get(mZRecur.getFrequency());
    if( frequency == null )
    {
      throw new RuntimeException("Invalid Frequency: " +mZRecur.getFrequency().toString());
    }

    return frequency;
  }

  public List<Integer> getByMonthList()
  {
    return mZRecur.getByMonthList();
  }

  public int getInterval()
  {
    return mZRecur.getInterval();
  }

  public List<Integer> getByMonthDayList()
  {
    return mZRecur.getByMonthDayList();
  }

  public List<Integer> getByCalendarDayList()
  {
    return getByCalendarDayList(null, null);
  }

  @Nonnull
  public List<WeekDayNum> getByDayList()
  {
    List<ZRecur.ZWeekDayNum> byDayList = mZRecur.getByDayList();
    List<WeekDayNum> list = new ArrayList<WeekDayNum>(byDayList.size());

    for( ZRecur.ZWeekDayNum weekDayNum : byDayList )
    {
      list.add(
        new WeekDayNum(
          weekDayNum.mDay.getCalendarDay(),
          weekDayNum.mOrdinal
        )
      );
    }

    return list;
  }

  @Deprecated
  public List<Integer> getByCalendarDayList(Long startTime, TimeZone timezone)
  {
    List<ZRecur.ZWeekDayNum> byDayList = mZRecur.getByDayList();
    List<Integer> list = new ArrayList<Integer>(byDayList.size());

    for( ZRecur.ZWeekDayNum weekDayNum : byDayList )
    {
      list.add(weekDayNum.mDay.getCalendarDay());
    }

    if (list.isEmpty() && Frequency.WEEKLY.equals(getFrequency()) && startTime != null && timezone != null)
    {
      Calendar calendar = Calendar.getInstance(timezone);
      calendar.setTimeInMillis(startTime);
      list.add(calendar.get(Calendar.DAY_OF_WEEK));
    }

    return list;
  }

  public List<WeekOfMonth> getByWeekOfMonth()
  {
    List<Integer> weeklist = mZRecur.getByWeekNoList();
    List<WeekOfMonth> weekOfMonthList = new ArrayList<WeekOfMonth>(weeklist.size());

    for( Integer weekno : weeklist )
    {
      weekOfMonthList.add(WeekOfMonth.fromZimbra(weekno));
    }
    return weekOfMonthList;
  }

  public void setWeekOfMonth(List<Integer> weekOfMonth)
  {
    mZRecur.setByWeekNoList(weekOfMonth);
  }

  public List<Integer> getByOffsetDayList()
  {
    List<ZRecur.ZWeekDayNum> byDayList = mZRecur.getByDayList();
    List<Integer> list = new ArrayList<Integer>(byDayList.size());

    for( ZRecur.ZWeekDayNum weekDayNum : byDayList )
    {
      list.add(weekDayNum.mOrdinal);
    }

    return list;
  }

  public boolean hasUntil()
  {
    return mZRecur.getUntil() != null;
  }

  public long getUntilUtc()
  {
    if(mZRecur.getUntil() != null)
    {
      return mZRecur.getUntil().getUtcTime();
    }

    return 0;
  }

  public int getCount()
  {
    return mZRecur.getCount();
  }

  public List<Integer> getBySetPosList()
  {
    return mZRecur.getBySetPosList();
  }

  public void setBySetPosList(List<Integer> bySetPosList) {
    mZRecur.setBySetPosList(bySetPosList);
  }

  public void setFrequency(Frequency frequency)
  {
    mZRecur.setFrequency(sZal2Zimbra.get(frequency));
  }

  public void setUntil(long until)
  {
    mZRecur.setUntil(ParsedDateTime.fromUTCTime(until));
  }

  public void setCount(int occurrences)
  {
    mZRecur.setCount(occurrences);
  }

  public void setInterval(int interval)
  {
    mZRecur.setInterval(interval);
  }

  public void setByDayList(List<WeekDayNum> byDayList)
  {
    List<ZRecur.ZWeekDayNum> dayNumList = new ArrayList<ZRecur.ZWeekDayNum>(byDayList.size());
    for (WeekDayNum weekDayNum : byDayList)
    {
      dayNumList.add(weekDayNum.toZimbra(ZRecur.ZWeekDayNum.class));
    }
    mZRecur.setByDayList(dayNumList);
  }

  public void setByMonthDayList(List<Integer> byMonthDayList)
  {
    mZRecur.setByMonthDayList(byMonthDayList);
  }

  public void setByMonthList(List<Integer> byMonthList)
  {
    mZRecur.setByMonthList(byMonthList);
  }

  public List<Date> expandRecurrenceOverRange(long dateStart, long rangeStart, long rangeEnd)
  {
    ParsedDateTime dtStart = ParsedDateTime.fromUTCTime(dateStart);
    try
    {
      return mZRecur.expandRecurrenceOverRange(dtStart, rangeStart, rangeEnd);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public String toString()
  {
    return toZimbra(ZRecur.class).toString();
  }
}
