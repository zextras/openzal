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
import org.openzal.zal.ZEAccount;
import org.jetbrains.annotations.NotNull;
import java.util.TimeZone;
/* $if ZimbraVersion >= 8.0.0 $*/
import com.zimbra.common.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.Util;
/* $else$
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
 $endif$ */

public class ZEIcalTimezone
{
  private ICalTimeZone mICalTimeZone;

  public ZEIcalTimezone(@NotNull Object timeZone)
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

  public static ZEIcalTimezone getAccountTimeZone(@NotNull ZEAccount account)
  {
/* $if MajorZimbraVersion <= 7 $
    ICalTimeZone accountTimeZone = ICalTimeZone.getAccountTimeZone(account.toZimbra(Account.class));
  $else$ */
    ICalTimeZone accountTimeZone = Util.getAccountTimeZone(account.toZimbra(Account.class));
/* $endif$ */
    return new ZEIcalTimezone(accountTimeZone);
  }

  public static ZEIcalTimezone lookup(String tzid,
                                      int stdOffset, String stdDtStart, String stdRRule, String stdTzname,
                                      int dayOffset, String dayDtStart, String dayRRule, String dayTzname)
  {
    return new ZEIcalTimezone(
      ICalTimeZone.lookup(tzid, stdOffset, stdDtStart, stdRRule, stdTzname, dayOffset, dayDtStart, dayRRule, dayTzname)
    );
  }

  public static class ZESimpleOnset implements Comparable<ZESimpleOnset>
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

    public ZESimpleOnset(
      int week, int dayOfWeek, int month, int dayOfMonth,
      int hour, int minute, int second, boolean skipBYMONTHDAYFixup
    )
    {
      this(new ICalTimeZone.SimpleOnset(week, dayOfWeek, month, dayOfMonth, hour, minute, second, skipBYMONTHDAYFixup));
    }

    ZESimpleOnset(ICalTimeZone.SimpleOnset simpleOnset)
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
    public int compareTo(ZESimpleOnset o)
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

  public static ZEIcalTimezone getUTC()
  {
    return new ZEIcalTimezone(ICalTimeZone.getUTC());
  }

  public int hashCode()
  {
    return mICalTimeZone.hashCode();
  }

  public int getStandardOffset()
  {
    return mICalTimeZone.getStandardOffset();
  }

  public ZESimpleOnset getDaylightOnset()
  {
    ICalTimeZone.SimpleOnset daylight = mICalTimeZone.getDaylightOnset();
    if (daylight == null)
    {
      return null;
    }
    else
    {
      return new ZESimpleOnset(daylight);
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

  public ZESimpleOnset getStandardOnset()
  {
    ICalTimeZone.SimpleOnset daylight = mICalTimeZone.getStandardOnset();
    if (daylight == null)
    {
      return null;
    }
    else
    {
      return new ZESimpleOnset(daylight);
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
}
