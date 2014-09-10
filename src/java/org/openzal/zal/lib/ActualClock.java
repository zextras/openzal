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

package org.openzal.zal.lib;


import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActualClock implements Clock
{
  @Override
  public long now() {
    return System.currentTimeMillis();
  }

  @Override
  public Calendar getCurrentTime()
  {
    return new GregorianCalendar();
  }

  public Calendar getDaysFromNow(int numDays)
  {
    Calendar now = getCurrentTime();
    now.add(Calendar.DAY_OF_YEAR, numDays);
    return now;
  }
}
