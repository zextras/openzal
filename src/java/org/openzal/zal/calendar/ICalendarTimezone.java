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

import javax.annotation.Nullable;
import org.openzal.zal.Account;
import javax.annotation.Nonnull;

import java.util.Date;
import java.util.TimeZone;

import com.zimbra.common.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.Util;

public class ICalendarTimezone
{
  private ICalTimeZone mICalTimeZone;

  public ICalendarTimezone(@Nonnull Object timeZone)
  {
    if (timeZone == null)
    {
      throw new NullPointerException();
    }

    mICalTimeZone = (ICalTimeZone)timeZone;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mICalTimeZone);
  }

  @Deprecated //Use Account.getAccountTimeZone instead
  public static ICalendarTimezone getAccountTimeZone(@Nonnull Account account)
  {
    ICalTimeZone accountTimeZone = Util.getAccountTimeZone(
      account.toZimbra(com.zimbra.cs.account.Account.class)
    );
    return new ICalendarTimezone(accountTimeZone);
  }

  @Nonnull
  public static ICalendarTimezone lookup(String tzid,
                                      int stdOffset, String stdDtStart, String stdRRule, String stdTzname,
                                      int dayOffset, String dayDtStart, String dayRRule, String dayTzname)
  {
    return new ICalendarTimezone(
      ICalTimeZone.lookup(tzid, stdOffset, stdDtStart, stdRRule, stdTzname, dayOffset, dayDtStart, dayRRule, dayTzname)
    );
  }

  public static class SimpleOnset implements Comparable<SimpleOnset>
  {
    private final ICalTimeZone.SimpleOnset mSimpleOnset;

    public int getSecond()
    {
      return mSimpleOnset.getSecond();
    }
    public int getDayOfMonth()
    {
      return mSimpleOnset.getDayOfMonth();
    }

    public SimpleOnset(
      int week, int dayOfWeek, int month, int dayOfMonth,
      int hour, int minute, int second, boolean skipBYMONTHDAYFixup
    )
    {
      this(new ICalTimeZone.SimpleOnset(week, dayOfWeek, month, dayOfMonth, hour, minute, second, skipBYMONTHDAYFixup));
    }

    SimpleOnset(ICalTimeZone.SimpleOnset simpleOnset)
    {
      mSimpleOnset = simpleOnset;
    }

    public int getMinute()
    {
      return mSimpleOnset.getMinute();
    }
    public int getMonth()
    {
      return mSimpleOnset.getMonth();
    }
    public int getDayOfWeek()
    {
      return mSimpleOnset.getDayOfWeek();
    }
    public int getWeek()
    {
      return mSimpleOnset.getWeek();
    }
    public int getHour()
    {
      return mSimpleOnset.getHour();
    }

    @Override
    public String toString()
    {
      return mSimpleOnset.toString();
    }

    public <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mSimpleOnset);
    }

    @Override
    public int compareTo(SimpleOnset o)
    {
      return mSimpleOnset.compareTo(o.toZimbra(ICalTimeZone.SimpleOnset.class));
    }
  }

  public String getDaylightTzname()
  {
    return mICalTimeZone.getDaylightTzname();
  }

  public boolean equals(Object object1)
  {
    return mICalTimeZone.equals(object1);
  }

  public String getID()
  {
    return mICalTimeZone.getID();
  }

  public static ICalendarTimezone getUTC()
  {
    return new ICalendarTimezone(ICalTimeZone.getUTC());
  }

  public int hashCode()
  {
    return mICalTimeZone.hashCode();
  }

  public int getStandardOffset()
  {
    return mICalTimeZone.getStandardOffset();
  }

  @Nullable
  public SimpleOnset getDaylightOnset()
  {
    ICalTimeZone.SimpleOnset daylight = mICalTimeZone.getDaylightOnset();
    if (daylight == null)
    {
      return null;
    }
    else
    {
      return new SimpleOnset(daylight);
    }
  }

  public boolean useDaylightTime()
  {
    return mICalTimeZone.useDaylightTime();
  }

  public int getDaylightOffset()
  {
    return mICalTimeZone.getDaylightOffset();
  }

  @Nullable
  public SimpleOnset getStandardOnset()
  {
    ICalTimeZone.SimpleOnset daylight = mICalTimeZone.getStandardOnset();
    if (daylight == null)
    {
      return null;
    }
    else
    {
      return new SimpleOnset(daylight);
    }
  }

  @Override
  public String toString()
  {
    return mICalTimeZone.toString();
  }

  public String getStandardTzname()
  {
    return mICalTimeZone.getStandardTzname();
  }

  public TimeZone getTimeZone()
  {
    return mICalTimeZone;
  }

  public boolean inDaylightTime(Date date)
  {
    return mICalTimeZone.inDaylightTime(date);
  }
}
