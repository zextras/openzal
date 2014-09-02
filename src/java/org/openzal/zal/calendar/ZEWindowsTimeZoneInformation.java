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

import com.zimbra.cs.mailbox.calendar.WindowsSystemTime;
import com.zimbra.cs.mailbox.calendar.WindowsTimeZoneInformation;
import org.jetbrains.annotations.NotNull;

public class ZEWindowsTimeZoneInformation
{
  private final WindowsTimeZoneInformation mWindowsTimeZoneInformation;

  public ZEWindowsTimeZoneInformation(
    String id, int i, ZEWindowsSystemTime standardDate, int standardBiasMins,
    String standardTzname, ZEWindowsSystemTime daylightDate, int daylightBiasMins, String daylightTzname
  )
  {
    this(new WindowsTimeZoneInformation(
           id,
           i,
           (standardDate == null) ? null : standardDate.toZimbra(WindowsSystemTime.class),
           standardBiasMins,
           standardTzname,
           (daylightDate == null) ? null : daylightDate.toZimbra(WindowsSystemTime.class),
           daylightBiasMins,
           daylightTzname
         )
    );
  }

  protected ZEWindowsTimeZoneInformation(
    @NotNull Object windowsTimeZoneInformation
  )
  {
    if ( windowsTimeZoneInformation == null )
    {
      throw new NullPointerException();
    }

    mWindowsTimeZoneInformation = (WindowsTimeZoneInformation)windowsTimeZoneInformation;
  }

  public int getBiasMins()
  {
    return (int) mWindowsTimeZoneInformation.getBiasMins();
  }


  public ZEWindowsSystemTime getStandardDate()
  {
    return new ZEWindowsSystemTime(mWindowsTimeZoneInformation.getStandardDate());
  }

  public int getStandardBiasMins()
  {
    return mWindowsTimeZoneInformation.getStandardBiasMins();
  }

  public ZEWindowsSystemTime getDaylightDate()
  {
    return new ZEWindowsSystemTime(mWindowsTimeZoneInformation.getDaylightDate());
  }

  public int getDaylightBiasMins()
  {
    return mWindowsTimeZoneInformation.getDaylightBiasMins();
  }

  public ZEIcalTimezone toICal()
  {
    return new ZEIcalTimezone(mWindowsTimeZoneInformation.toICal());
  }
}
