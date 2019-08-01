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

package org.openzal.zal.lib;

import java.util.Calendar;

public interface Clock
{
  long ONE_MINUTE = 60L * 1000L;
  long ONE_HOUR   = ONE_MINUTE * 60L;
  long ONE_DAY    = 24L * ONE_HOUR;
  long HALF_DAY   = 12L * ONE_HOUR;

  public long now();
  public Calendar getCurrentTime();
  public Calendar getDaysFromNow(int numDays);
}
