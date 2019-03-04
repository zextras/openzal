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

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class InternalOverrideBlobWithMailboxInfo extends com.zimbra.cs.store.file.VolumeBlobProxy
{
  private final Blob   mBlob;
  private final String mVolumeId;

  public InternalOverrideBlobWithMailboxInfo(Blob blob)
  {
    super();
    mBlob = blob;
    mVolumeId = blob.getVolumeId();
  }

  public Blob getWrappedObject()
  {
    return mBlob;
  }

  public short getVolumeId()
  {
    return Short.parseShort(mVolumeId);
  }

  @Override
  public File getFile()
  {
    return mBlob.getFile();
  }

  @Override
  public String getPath()
  {
    return mBlob.getKey();
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return new FileInputStream(mBlob.getFile());
  }

  @Override
  public boolean isCompressed() throws IOException
  {
    InputStream inputStream = new BufferedInputStream(new FileInputStream(mBlob.getFile()));
    try
    {
      return Utils.isGzipped(inputStream);
    }
    finally
    {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public String getDigest() throws IOException
  {
    InputStream inputStream = new FileInputStream(mBlob.getFile());
    try
    {
      return Utils.computeDigest(inputStream);
    }
    finally
    {
      IOUtils.closeQuietly(inputStream);
    }
  }

  @Override
  public long getRawSize() throws IOException
  {
    return mBlob.getSize();
  }

  @Override
  public com.zimbra.cs.store.Blob setCompressed(boolean isCompressed)
  {
    //mBlob.setCompressed(isCompressed);
    return this;
  }

  @Override
  public com.zimbra.cs.store.Blob setDigest(String digest)
  {
    //mBlob.setDigest(digest);
    return this;
  }

  @Override
  public com.zimbra.cs.store.Blob setRawSize(long rawSize)
  {
    //mBlob.setRawSize(rawSize);
    return this;
  }

  @Override
  public com.zimbra.cs.store.Blob copyCachedDataFrom(com.zimbra.cs.store.Blob other)
  {
    //mBlob.copyCachedDataFrom(new BlobWrap(other));
    return this;
  }

  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }

  @Override
  public String toString()
  {
    return mBlob.toString();
  }
}
