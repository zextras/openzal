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


import javax.annotation.Nullable;

public class ZimbraItemId
{
  private final ZimbraId mAccountId;
  private final int mItemId;

  public ZimbraItemId(ZimbraId accountId, int itemId)
  {
      mAccountId = accountId;
      mItemId = itemId;
  }

  public ZimbraItemId(String accountId, int itemId)
  {
    this(new ZimbraId(accountId), itemId);
  }

  public ZimbraItemId(Mailbox mbox, int itemId)
  {
    this(new ZimbraId(mbox.getAccountId()), itemId);
  }

  public static ZimbraItemId fromString(String string)
  {
    String[] parts = string.split("/");
    if (parts.length != 2)
    {
      throw new RuntimeException("Invalid item id: " + string);
    }
    ZimbraId accountId = new ZimbraId(parts[0]);
    int itemId = Integer.parseInt(parts[1]);
    return new ZimbraItemId(accountId, itemId);
  }

  @Override
  public String toString()
  {
    return mAccountId.toString() + "/" + mItemId;
  }

  public int getItemId()
  {
      return mItemId;
  }

  public ZimbraId getAccountId()
  {
      return mAccountId;
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

    ZimbraItemId that = (ZimbraItemId) o;

    if (mItemId != that.mItemId)
    {
      return false;
    }
    if (mAccountId != null ? !mAccountId.equals(that.mAccountId) : that.mAccountId != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = mAccountId != null ? mAccountId.hashCode() : 0;
    result = 31 * result + mItemId;
    return result;
  }
}
