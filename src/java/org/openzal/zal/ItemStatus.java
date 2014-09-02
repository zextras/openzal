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

package org.openzal.zal;


import java.io.Serializable;

public class ItemStatus implements Serializable
{
  private static final long serialVersionUID = 1522734297886684519L;

  public final int sequence;
  public final long date;

  public ItemStatus(int sequence, long date)
  {
    this.sequence = sequence;
    this.date = date;
  }

  @Override
  public String toString()
  {
    return "sq: "+sequence+" dt: "+date;
  }

  @Override
  public boolean equals(Object o)
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
