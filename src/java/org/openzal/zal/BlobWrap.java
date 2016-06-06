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

import com.zimbra.cs.store.file.VolumeStagedBlob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BlobWrap implements Blob
{
  @NotNull private final com.zimbra.cs.store.Blob mBlob;
  private final String mVolumeId;

  @NotNull
  public Object getWrappedObject()
  {
    return mBlob;
  }

  public BlobWrap(
    @NotNull Object blob,
    String volumeId
  )
  {
    if (blob == null)
    {
      throw new NullPointerException();
    }
    if (volumeId == null && blob instanceof VolumeStagedBlob)
    {
      /* $if ZimbraVersion >= 7.0.0 $ */
      volumeId = String.valueOf(((VolumeStagedBlob) blob).getLocator());
      /* $else $
      volumeId = String.valueOf(((VolumeStagedBlob) blob).getStagedLocator());
      /* $endif $ */
    }
    mVolumeId = volumeId;
    mBlob = (com.zimbra.cs.store.Blob) blob;
  }

  public File getFile()
  {
    return mBlob.getFile();
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mBlob);
  }

  @Override
  public String getDigest()
  {
    try
    {
      return mBlob.getDigest();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long getRawSize()
  {
    return 0;
  }

  @Override
  public String getVolumeId()
  {
    return mVolumeId;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return mBlob.getInputStream();
  }

  public String getKey()
  {
    return mBlob.getPath();
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }

  public static Blob wrapZimbraBlob(Object blob)
  {
    return wrapZimbraBlob(blob, null);
  }

  public static Blob wrapZimbraBlob(Object blob, @Nullable String volumeId)
  {
    // TODO if volumeId == null check if blob instanceof VolumeBlob
    if (blob instanceof Blob)
      return (Blob) blob;

    if (blob instanceof InternalOverrideBlob)
      return ((InternalOverrideBlob) blob).getWrappedObject();

    if (blob instanceof InternalOverrideStagedBlob)
      return ((InternalOverrideStagedBlob) blob).getWrappedObject();

    if (blob instanceof InternalOverrideVolumeBlob)
      return ((InternalOverrideVolumeBlob) blob).getWrappedObject();

    if (blob instanceof VolumeStagedBlob)
      return new BlobWrap(((VolumeStagedBlob) blob).getLocalBlob(), volumeId);

    return new BlobWrap(blob, volumeId);
  }

  @Override
  public String toString()
  {
    return mBlob.toString();
  }
}
