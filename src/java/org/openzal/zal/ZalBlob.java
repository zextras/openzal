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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZalBlob implements Blob
{
  private final File   mFile;
  private final String mVolumeId;
  private       String mDigest;
  private       Long   mRawSize;

  public ZalBlob(File file, String volumeId)
  {
    this(file, volumeId, null, null);
  }

  public ZalBlob(File file, String volumeId, String digest, Long rawSize)
  {
    mFile = file;
    mVolumeId = volumeId;
    mDigest = digest;
    mRawSize = rawSize;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    boolean success = mFile.renameTo(new File(newPath));
    if (!success)
    {
      throw new IOException("Cannot rename " + mFile.getPath() + " to " + newPath);
    }
  }

  @Override
  public String getKey()
  {
    return mFile.getAbsolutePath();
  }

  @Override
  public File getFile()
  {
    return mFile;
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(new InternalOverrideBlob(this));
  }

  @Override
  public String getDigest() throws IOException
  {
    if (mDigest == null || mDigest.isEmpty())
    {
      FileInputStream inputStream = null;
      try
      {
        inputStream = new FileInputStream(mFile);
        mDigest = Utils.computeDigest(inputStream);
      }
      finally
      {
        IOUtils.closeQuietly(inputStream);
      }
    }
    return mDigest;
  }

  public long getSize() throws IOException
  {
    if (mRawSize == null || mRawSize == 0)
    {
      mRawSize = mFile.length();
    }
    return mRawSize;
  }

  @Override
  public String getVolumeId()
  {
    return mVolumeId;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return new FileInputStream(mFile);
  }

  @Override
  public boolean hasMailboxInfo()
  {
    return false;
  }

  @Override
  public MailboxBlob toMailboxBlob()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public ZalBlob setDigest(String digest)
  {
    mDigest = digest;
    return this;
  }

  @Override
  public ZalBlob setSize(long size)
  {
    mRawSize = size;
    return this;
  }

  @Override
  public long getStoredFileSize() throws IOException
  {
    return mFile.length();
  }

  @Override
  public boolean isCompressed()
    throws IOException
  {
    return false;
  }
}
