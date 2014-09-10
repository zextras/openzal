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

/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.calendar.ParsedDateTime;
import com.zimbra.common.calendar.TimeZoneMap;
/* $else $
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
/* $endif $ */
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.ZRecur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecurrenceRule
{
  private final ZRecur mZRecur;

  public enum Frequency
  {
    YEARLY,
    MONTHLY,
    WEEKLY,
    DAILY
  }

  private static final HashMap<ZRecur.Frequency, Frequency> sZimbra2Zal;
  private static final HashMap<Frequency, ZRecur.Frequency> sZal2Zimbra;

  static {
    sZimbra2Zal = new HashMap<ZRecur.Frequency, Frequency>(4);
    sZal2Zimbra = new HashMap<Frequency, ZRecur.Frequency>(4);

    sZimbra2Zal.put(ZRecur.Frequency.YEARLY, Frequency.YEARLY );
    sZal2Zimbra.put(Frequency.YEARLY, ZRecur.Frequency.YEARLY );

    sZimbra2Zal.put(ZRecur.Frequency.MONTHLY, Frequency.MONTHLY );
    sZal2Zimbra.put(Frequency.MONTHLY, ZRecur.Frequency.MONTHLY );

    sZimbra2Zal.put(ZRecur.Frequency.WEEKLY, Frequency.WEEKLY );
    sZal2Zimbra.put(Frequency.WEEKLY, ZRecur.Frequency.WEEKLY );

    sZimbra2Zal.put(ZRecur.Frequency.DAILY, Frequency.DAILY );
    sZal2Zimbra.put(Frequency.DAILY, ZRecur.Frequency.DAILY );
  }

  public RecurrenceRule(MapTimeZone tzMap)
  {
    try
    {
      mZRecur = new ZRecur("", tzMap.toZimbra(TimeZoneMap.class) );
    }
    catch (ServiceException e)
    {
      throw new RuntimeException("Error initializing ZRecur");
    }
  }

  public RecurrenceRule(Object zRecur)
  {
    mZRecur = (ZRecur)zRecur;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZRecur);
  }

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
    List<ZRecur.ZWeekDayNum> byDayList = mZRecur.getByDayList();
    List<Integer> list = new ArrayList<Integer>(byDayList.size());

    for( ZRecur.ZWeekDayNum weekDayNum : byDayList )
    {
      list.add(weekDayNum.mDay.getCalendarDay());
    }

    return list;
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
    return mZRecur.getUntil().getUtcTime();
  }

  public int getCount()
  {
    return mZRecur.getCount();
  }

/**
  The BYSETPOS rule part specifies aa list of values which corresponds
  to the nth occurrence within the set of events specified by the rule
*/
  public List<Integer> getBySetPosList()
  {
    return mZRecur.getBySetPosList();
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
}
