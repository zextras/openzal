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

import java.util.HashMap;
import java.util.Map;

public enum GlobalInviteStatus
{
  APPOINTMENT_TENTATIVE("TENT", AttendeeInviteStatus.TENTATIVE),
  APPOINTMENT_CANCELLED("CANC", AttendeeInviteStatus.DECLINED),
  APPOINTMENT_CONFIRMED("CONF", AttendeeInviteStatus.ACCEPTED),
  APPOINTMENT_NEED_ACTION("NEED", AttendeeInviteStatus.NEEDS_ACTION),
  TASK_COMPLETED("COMP", AttendeeInviteStatus.COMPLETED),
  TASK_IN_PROGRESS("INPR", AttendeeInviteStatus.IN_PROCESS);

  public final String mRawStatus;
  private final AttendeeInviteStatus mAttendeeInviteStatus;

  GlobalInviteStatus(String status, @Nonnull AttendeeInviteStatus attendeeInviteStatus)
  {
    mRawStatus = status;
    mAttendeeInviteStatus = attendeeInviteStatus;
  }


  public String getRawStatus()
  {
    return mRawStatus;
  }

  @Nonnull
  private final static Map<String, GlobalInviteStatus> sZimbra2Zal;
  @Nonnull
  private final static Map<String, GlobalInviteStatus> sICal2Zimbra;

  static
  {
    sZimbra2Zal = new HashMap<String, GlobalInviteStatus>(7);
    sZimbra2Zal.put("TENT", APPOINTMENT_TENTATIVE);
    sZimbra2Zal.put("CANC", APPOINTMENT_CANCELLED);
    sZimbra2Zal.put("CONF", APPOINTMENT_CONFIRMED);
    sZimbra2Zal.put("NEED", APPOINTMENT_NEED_ACTION);
    sZimbra2Zal.put("WAITING", APPOINTMENT_NEED_ACTION);
    sZimbra2Zal.put("DEFERRED", APPOINTMENT_NEED_ACTION);
    sZimbra2Zal.put("", APPOINTMENT_NEED_ACTION);
    sZimbra2Zal.put("COMP", TASK_COMPLETED);
    sZimbra2Zal.put("INPR", TASK_IN_PROGRESS);

    sICal2Zimbra = new HashMap<String, GlobalInviteStatus>(6);
    sICal2Zimbra.put("TENTATIVE", APPOINTMENT_TENTATIVE);
    sICal2Zimbra.put("CANCELLED", APPOINTMENT_CANCELLED);
    sICal2Zimbra.put("CONFIRMED", APPOINTMENT_CONFIRMED);
    sICal2Zimbra.put("NEEDS-ACTION", APPOINTMENT_NEED_ACTION);
    sICal2Zimbra.put("COMPLETED", TASK_COMPLETED);
    sICal2Zimbra.put("IN-PROGRESS", TASK_IN_PROGRESS);
  }

  @Nonnull
  public static GlobalInviteStatus fromZimbra(String status)
  {
    GlobalInviteStatus globalInviteStatus = sZimbra2Zal.get(status);
    if (globalInviteStatus == null)
    {
      throw new RuntimeException("Invalid invite status: " + status);
    }
    return globalInviteStatus;
  }

  @Nonnull
  public static GlobalInviteStatus fromICal(String status)
  {
    GlobalInviteStatus globalInviteStatus = sICal2Zimbra.get(status);
    if (globalInviteStatus == null)
    {
      throw new RuntimeException("Invalid invite status: " + status);
    }
    return globalInviteStatus;
  }

  @Nonnull
  public AttendeeInviteStatus toAttendeeStatus()
  {
    return mAttendeeInviteStatus;
  }
}
