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
/* $else $
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
/* $endif $ */
import com.zimbra.cs.mailbox.calendar.RecurId;


public class RecurrenceId
{
  private final long mExceptionStartTimeUtc;
  private ParsedDateTime mDt;

  public RecurrenceId(Long exceptionStartTimeUtc)
  {
    mExceptionStartTimeUtc = exceptionStartTimeUtc;
  }

  public long getExceptionStartTimeUtc()
  {
    return mExceptionStartTimeUtc;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(new RecurId(ParsedDateTime.fromUTCTime(mExceptionStartTimeUtc), RecurId.RANGE_NONE));
  }

  ParsedDateTime getDt()
  {
    return mDt;
  }
}
