/*
 * ZAL - The abstraction layer for Zimbra.
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

import org.jetbrains.annotations.NotNull;
/* $if ZimbraVersion >= 8.0.0 $*/
import com.zimbra.common.calendar.TimeZoneMap;
import com.zimbra.common.calendar.ICalTimeZone;
/* $else$
import com.zimbra.cs.mailbox.calendar.ZRecur.ZWeekDay;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
 $endif$ */

public class MapTimeZone
{
  @NotNull
  private final TimeZoneMap mTimeZoneMap;

  public MapTimeZone(@NotNull Object timeZoneMap)
  {
    if (timeZoneMap == null)
    {
      throw new NullPointerException();
    }

    mTimeZoneMap = (TimeZoneMap) timeZoneMap;
  }

  public MapTimeZone(ICalendarTimezone icaltimezone)
  {
    mTimeZoneMap = new TimeZoneMap(icaltimezone.toZimbra(ICalTimeZone.class));
  }

  @NotNull
  public ICalendarTimezone getTimeZone(String key)
  {
    return new ICalendarTimezone(mTimeZoneMap.getTimeZone(key));
  }

  @NotNull
  public ICalendarTimezone getLocalTimeZone()
  {
    return new ICalendarTimezone(mTimeZoneMap.getLocalTimeZone());
  }

  public void add(ICalendarTimezone timezone)
  {
    mTimeZoneMap.add(timezone.toZimbra(ICalTimeZone.class));
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mTimeZoneMap);
  }
}
