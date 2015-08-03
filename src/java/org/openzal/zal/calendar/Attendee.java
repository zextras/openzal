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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zimbra.cs.mailbox.calendar.ZOrganizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Attendee
{
  public String getAddress()
  {
    return mAddress;
  }

  public String getName()
  {
    return mName;
  }

  public AttendeeInviteStatus getStatus()
  {
    return mStatus;
  }

  public AttendeeType getType()
  {
    return mType;
  }

  private final AttendeeType         mType;
  private final String               mAddress;
  private final String               mName;
  private final AttendeeInviteStatus mStatus;

  public Attendee(
    String address,
    String name,
    AttendeeInviteStatus status
  )
  {
    this(address, name, status, AttendeeType.Required);
  }

  @JsonCreator
  public Attendee(
    @JsonProperty("address") String address,
    @JsonProperty("name") String name,
    @JsonProperty("status") AttendeeInviteStatus status,
    @JsonProperty("type") AttendeeType type
  )
  {
    mType = type;
    mAddress = (address == null) ? "" : address;
    mName = (name == null) ? "" : name;
    mStatus = status;
  }

  @NotNull
  ZOrganizer toZOrganizer()
  {
    return new ZOrganizer(mAddress, mName);
  }
}
