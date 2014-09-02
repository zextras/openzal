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


public class ItemChange extends PlacedItemInfo
{
  public boolean deleted;

  public ItemChange(
    boolean deleted,
    int folderId,
    int itemId,
    int sequence,
    long date
  )
  {
    super( folderId, itemId, sequence, date);
    this.deleted = deleted;
  }

  public ItemChange(
    boolean deleted,
    int folderId,
    ItemInfo itemInfo
  )
  {
    super(folderId, itemInfo.itemId, itemInfo.sequence, itemInfo.date);
    this.deleted = deleted;
  }

  @Override
  public String toString()
  {
    return
      "deleted: " + deleted + " " +super.toString();
  }
}
