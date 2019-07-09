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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mailbox.calendar.ZOrganizer;

import javax.annotation.Nonnull;

@JsonIgnoreProperties( ignoreUnknown = true )
public class Attendee
{
  private final AttendeeType mType;
  private final String mAddress;
  private final String mName;
  private final AttendeeInviteStatus mStatus;
  private final Boolean mRsvp;

  public Attendee(
    String address
  )
  {
    this(address, address);
  }

  public Attendee(
    String address,
    String name
  )
  {
    this(address, name, AttendeeInviteStatus.NEEDS_ACTION);
  }

  public Attendee(
    String address,
    String name,
    AttendeeInviteStatus status
  )
  {
    this(address, name, status, AttendeeType.Required);
  }

  public Attendee(
    String address,
    String name,
    AttendeeInviteStatus status,
    AttendeeType type
  )
  {
    this(address, name, status, type, null);
  }

  @JsonCreator
  public Attendee(
    @JsonProperty( "address" ) String address,
    @JsonProperty( "name" ) String name,
    @JsonProperty( "status" ) AttendeeInviteStatus status,
    @JsonProperty( "type" ) AttendeeType type,
    @JsonProperty( "rsvp" ) Boolean rsvp
  )
  {
    mAddress = (address == null) ? "" : address;
    mName = (name == null) ? "" : name;
    mStatus = status;
    mType = type;
    mRsvp = rsvp;
  }

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

  public boolean hasRsvp()
  {
    return getRsvp() != null;
  }

  public Boolean getRsvp()
  {
    return mRsvp;
  }

  @Nonnull
  public ZOrganizer toZOrganizer()
  {
    return new ZOrganizer(mAddress, mName);
  }

  public ZAttendee toZAttendee()
  {
    return new ZAttendee(
      getAddress(),
      getName(),
      null,
      null,
      null,
      null,
      getType().toZimbra(),
      getStatus().getRawStatus(),
      getRsvp(),
      null,
      null,
      null,
      null
    );
  }
}
