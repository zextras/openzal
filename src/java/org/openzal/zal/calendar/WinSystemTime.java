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

import com.zimbra.cs.mailbox.calendar.WindowsSystemTime;
import javax.annotation.Nonnull;

public class WinSystemTime
{
  @Nonnull
  private final WindowsSystemTime mWindowsSystemTime;

  public WinSystemTime(int year, int month, int dayOfWeek, int day, int hour, int minute, int second, int ms)
  {
    mWindowsSystemTime = new WindowsSystemTime(
      year,
      month,
      dayOfWeek,
      day,
      hour,
      minute,
      second,
      ms
    );
  }

  public WinSystemTime(@Nonnull Object windowsSystemTime)
  {
    if (windowsSystemTime == null)
    {
      throw new NullPointerException();
    }

    mWindowsSystemTime = (WindowsSystemTime) windowsSystemTime;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mWindowsSystemTime);
  }

  private void checkWindowsSystemTimeInitialized()
  {
    if (mWindowsSystemTime == null)
    {
      throw new RuntimeException("WindowsSystemTime is not initialized.");
    }
  }

  public int getYear()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getYear();
  }

  public int getMonth()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getMonth();
  }

  public int getDayOfWeek()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getDayOfWeek();
  }

  public int getDay()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getDay();
  }

  public int getHour()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getHour();
  }

  public int getMinute()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getMinute();
  }

  public int getSecond()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getSecond();
  }

  public int getMilliseconds()
  {
    checkWindowsSystemTimeInitialized();
    return mWindowsSystemTime.getMilliseconds();
  }
}
