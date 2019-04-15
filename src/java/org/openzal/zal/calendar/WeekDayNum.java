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

import com.zimbra.cs.mailbox.calendar.ZRecur;
import javax.annotation.Nonnull;

import com.zimbra.common.calendar.ZWeekDay;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class WeekDayNum
{
  @Nonnull
  private static final Map<Integer, ZWeekDay> mZal2Zimbra;
  private              int                    mOrdinal;
  private final        int                    mCalendarDay;


  static
  {
    mZal2Zimbra = new HashMap<Integer, ZWeekDay>(7);
    mZal2Zimbra.put(Calendar.SUNDAY, ZWeekDay.SU);
    mZal2Zimbra.put(Calendar.MONDAY, ZWeekDay.MO);
    mZal2Zimbra.put(Calendar.TUESDAY, ZWeekDay.TU);
    mZal2Zimbra.put(Calendar.WEDNESDAY, ZWeekDay.WE);
    mZal2Zimbra.put(Calendar.THURSDAY, ZWeekDay.TH);
    mZal2Zimbra.put(Calendar.FRIDAY, ZWeekDay.FR);
    mZal2Zimbra.put(Calendar.SATURDAY, ZWeekDay.SA);
  }

  public WeekDayNum(int day)
  {
    mOrdinal = 0;
    mCalendarDay = day;
  }

  public WeekDayNum(int day, int ordinal)
  {
    mOrdinal = ordinal;
    mCalendarDay = day;
  }

  public void setOrdinal(int ordinal)
  {
    mOrdinal = ordinal;
  }

  public int getOrdinal()
  {
    return mOrdinal;
  }

  public int getDay()
  {
    return mCalendarDay;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(new ZRecur.ZWeekDayNum(mOrdinal, mZal2Zimbra.get(getDay())));
  }

  public static class DayComparator implements Comparator<WeekDayNum>
  {
    @Override
    public int compare(WeekDayNum o1, WeekDayNum o2)
    {
      return o1.getDay() - o2.getDay();
    }
  }
}
