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

package org.openzal.zal;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.annotation.Nullable;

import java.io.Serializable;

@JsonSerialize(using = ToStringSerializer.class)
public class ItemStatus implements Serializable
{
  private static final long serialVersionUID = 1522734297886684519L;

  public int sequence;
  public long date;

  public ItemStatus(int sequence, long date)
  {
    this.sequence = sequence;
    this.date = date;
  }

  @JsonCreator
  public static ItemStatus fromString(String value)
  {
    String[] values = value.split("@");
    return new ItemStatus(
      Integer.parseInt(values[0]),
      Long.parseLong(values[1])
    );
  }

  @Override
  public String toString()
  {
    return sequence+"@"+date;
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    ItemStatus that = (ItemStatus) o;

    if (date != that.date)
    {
      return false;
    }
    if (sequence != that.sequence)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = sequence;
    result = 31 * result + (int) (date ^ (date >>> 32));
    return result;
  }
}
