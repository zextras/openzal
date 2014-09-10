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

public class Mountpoint extends Folder
{
  private final com.zimbra.cs.mailbox.Mountpoint mMountpoint;

  public Mountpoint(@NotNull Object item)
  {
    super(item);
    mMountpoint = (com.zimbra.cs.mailbox.Mountpoint) item;
  }

  public String getRemoteUuid()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mMountpoint.getRemoteUuid();
    /* $else $
    return null;
    /* $endif $ */
  }

  public int getRemoteId()
  {
    return mMountpoint.getRemoteId();
  }

  public String getOwnerId()
  {
    return mMountpoint.getOwnerId();
  }
}
