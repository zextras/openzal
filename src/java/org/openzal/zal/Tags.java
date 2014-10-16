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

package org.openzal.zal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Tags
{
/* $if ZimbraVersion < 8.0.0 $
  private final long mBitmask;
 $else$ */
  private final String[] mTags;
/* $endif$ */

  private Tags(
    long bitmask
  )
  {
    /* $if ZimbraVersion < 8.0.0 $
    mBitmask = bitmask;
    /* $else $ */
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
  public static Tags extractFromItem( @NotNull Tag item )
  {
    /* $if ZimbraVersion < 8.0.0 $
    return new Tags(item.getBitmask());
    $else$ */
    return new Tags(item.getTags());
    /* $endif$ */
  }

  public long getLongTags()
  {
    /* $if ZimbraVersion < 8.0.0 $
    return mBitmask;
    $else$ */
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public String[] getTags()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mTags;
    /* $else$
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public Tags(String[] tags)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    mTags = tags;
    /* $else$
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

  public Tags(List<String> tags)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    mTags = tags.toArray(new String[tags.size()]);
    /* $else$
    throw new UnsupportedOperationException();
    /* $endif$ */
  }

}
