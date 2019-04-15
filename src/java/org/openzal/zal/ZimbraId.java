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

public class ZimbraId
{
  private String mId;

  public ZimbraId(String id)
  {
    mId = id;
  }

  public String getId()
  {
    return mId;
  }

  @Override
  public String toString()
  {
    return mId;
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ZimbraId zimbraId = (ZimbraId) o;

    if (mId != null ? !mId.equals(zimbraId.mId) : zimbraId.mId != null)
      return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    return mId != null ? mId.hashCode() : 0;
  }
}
