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

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import java.util.HashMap;
import java.util.Map;

public enum FreeBusyStatus
{
  Busy("B"),
  Free("F"),
  BusyTentative("T"),
  BusyUnavailable("O"),
  @JsonEnumDefaultValue NoData("N");

  @Nonnull
  private static final Map<String, FreeBusyStatus> sZimbra2Zal;

  static
  {
    sZimbra2Zal = new HashMap<String, FreeBusyStatus>(6);
    sZimbra2Zal.put("B", Busy);
    sZimbra2Zal.put("F", Free);
    sZimbra2Zal.put("T", BusyTentative);
    sZimbra2Zal.put("O", BusyUnavailable);
    sZimbra2Zal.put("N", NoData);
    sZimbra2Zal.put("",  NoData);
  }

  public String getRawFreeBusyStatus()
  {
    return mRawFreeBusyStatus;
  }

  private final String mRawFreeBusyStatus;

  FreeBusyStatus(String rawFreeBusyStatus)
  {
    mRawFreeBusyStatus = rawFreeBusyStatus;
  }

  @Nonnull
  public static FreeBusyStatus fromZimbra(String freeBusy)
  {
    FreeBusyStatus status = sZimbra2Zal.get(freeBusy);

    if (status == null) {
      throw new RuntimeException("Invalid FreeBusyStatus: " + freeBusy);
    }

    return status;
  }

  public GlobalInviteStatus toGlobalInviteStatus()
  {
    switch(this)
    {
      case Busy:
        return GlobalInviteStatus.APPOINTMENT_CONFIRMED;
      case Free:
        return GlobalInviteStatus.APPOINTMENT_CANCELLED;
      case BusyTentative:
        return GlobalInviteStatus.APPOINTMENT_TENTATIVE;
      case BusyUnavailable:
        return GlobalInviteStatus.APPOINTMENT_CANCELLED;
      case NoData:
        return GlobalInviteStatus.APPOINTMENT_NEED_ACTION;
    }

    throw new RuntimeException();
  }
}
