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

package com.zimbra.cs.store.file;

import java.io.File;

public class VolumeBlobProxy extends VolumeBlob
{
  public VolumeBlobProxy()
  {
    super(new File("/tmp/fakeblob"), (short) 0);
  }

  public VolumeBlobProxy(Object blob)
  {
    super(((VolumeBlob)blob).getFile(), ((VolumeBlob)blob).getVolumeId());
  }

  @Override
  public short getVolumeId()
  {
    return super.getVolumeId();
  }

  public static boolean isVolumeBlob(Object blob)
  {
    return blob != null && VolumeBlob.class.isAssignableFrom(blob.getClass());
  }
}
