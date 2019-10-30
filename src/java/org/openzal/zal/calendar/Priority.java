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

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Priority
{
  HIGH(1),
  @JsonEnumDefaultValue MEDIUM(5),
  LOW(9);

  private int mRawPriority;

  Priority(int i)
  {
    mRawPriority = i;
  }

  public String getRawPriority()
  {
    return String.valueOf(mRawPriority);
  }

  @Nonnull
  public static Priority fromZimbra(@Nullable String zimbraPriority)
  {
    if(zimbraPriority == null || zimbraPriority.isEmpty())
    {
      zimbraPriority = "9";
    }

    int priority = Integer.parseInt(zimbraPriority);

    if( priority < MEDIUM.mRawPriority )
    {
      return HIGH;
    }

    if( priority > MEDIUM.mRawPriority )
    {
      return LOW;
    }

    return MEDIUM;
  }
}
