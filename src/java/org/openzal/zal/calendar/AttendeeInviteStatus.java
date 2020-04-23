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
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.openzal.zal.log.ZimbraLog;

import java.util.HashMap;
import java.util.Map;

public enum AttendeeInviteStatus
{
  ORGANIZER("OR", "ORGANIZER"),
  TENTATIVE("TE", "TENTATIVE"),
  @JsonEnumDefaultValue NEEDS_ACTION("NE", "NEEDS-ACTION"),
  DELEGATED("DG", "DELEGATED"),
  DECLINED("DE", "DECLINED"),
  COMPLETED("CO", "COMPLETED"),
  ACCEPTED("AC", "ACCEPTED"),
  IN_PROCESS("IN", "IN_PROGRESS"),
  WAITING("WA", "WAITING"),
  DEFERRED("DF", "DEFERRED");


  private final String mRawStatus;
  private final String mIcalValue;

  @Nonnull
  private static final Map<String, AttendeeInviteStatus> sZimbra2Zal;
  @Nonnull
  private static final Map<String, AttendeeInviteStatus> sICal2Zimbra;

  static
  {
    sZimbra2Zal = new HashMap<String, AttendeeInviteStatus>(10);
    sZimbra2Zal.put("TE", TENTATIVE);
    sZimbra2Zal.put("NE", NEEDS_ACTION);
    sZimbra2Zal.put("",   NEEDS_ACTION);
    sZimbra2Zal.put("DG", DELEGATED);
    sZimbra2Zal.put("DE", DECLINED);
    sZimbra2Zal.put("CO", COMPLETED);
    sZimbra2Zal.put("AC", ACCEPTED);
    sZimbra2Zal.put("IN", IN_PROCESS);
    sZimbra2Zal.put("WA", WAITING);
    sZimbra2Zal.put("DF", DEFERRED);

    sICal2Zimbra = new HashMap<String, AttendeeInviteStatus>(9);
    sICal2Zimbra.put("TENTATIVE", TENTATIVE);
    sICal2Zimbra.put("NEEDS-ACTION", NEEDS_ACTION);
    sICal2Zimbra.put("DELEGATED", DELEGATED);
    sICal2Zimbra.put("DECLINED", DECLINED);
    sICal2Zimbra.put("COMPLETED", COMPLETED);
    sICal2Zimbra.put("ACCEPTED", ACCEPTED);
    sICal2Zimbra.put("IN_PROGRESS", IN_PROCESS);
    sICal2Zimbra.put("WAITING", WAITING);
    sICal2Zimbra.put("DEFERRED", DEFERRED);
  }


  AttendeeInviteStatus(String rawStatus, String icalValue)
  {
    mRawStatus = rawStatus;
    mIcalValue = icalValue;
  }

  public String getRawStatus()
  {
    return mRawStatus;
  }

  public String getIcalValue()
  {
    return mIcalValue;
  }

  @Nonnull
  public static AttendeeInviteStatus fromZimbra(String partStat)
  {
    AttendeeInviteStatus attendeeInviteStatus = sZimbra2Zal.get(partStat);
    if( attendeeInviteStatus == null ) {
      ZimbraLog.extensions.warn("Invalid invite status: "+partStat+", fall back to NE");
      attendeeInviteStatus = sZimbra2Zal.get("NE");
    }
    return attendeeInviteStatus;
  }

  @Nullable
  public static AttendeeInviteStatus fromZimbraOrNull(String partStat)
  {
    return sZimbra2Zal.get(partStat);
  }

  @Nonnull
  public static AttendeeInviteStatus fromICal(String partStat)
  {
    AttendeeInviteStatus attendeeInviteStatus = sICal2Zimbra.get(partStat);
    if (attendeeInviteStatus == null) {
      throw new RuntimeException("Invalid invite status: "+partStat);
    }
    return attendeeInviteStatus;
  }
}
