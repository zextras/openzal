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

import javax.annotation.Nonnull;

public class ItemInfo extends ItemStatus
{
  public final int itemId;

  public ItemInfo(int itemId, int sequence, long date)
  {
    super(sequence,date);
    this.itemId = itemId;
  }

  public ItemInfo(int itemId, @Nonnull ItemStatus itemStatus )
  {
    super(itemStatus.sequence,itemStatus.date);
    this.itemId = itemId;
  }

  @Override
  public int hashCode()
  {
    return itemId + sequence;
  }

  @Override
  public boolean equals(Object object)
  {
    if(!(object instanceof ItemInfo))
    {
      return false;
    }

    ItemInfo other = (ItemInfo) object;

    return other.sequence == sequence
           && other.date == date
           && other.itemId == itemId;

  }

  @Override
  public String toString()
  {
    return
      "itemId: " + itemId + " " +
      "sq: " + sequence + " " +
      "dt: "+date;
  }

  public int getItemId()
  {
    return itemId;
  }
}
