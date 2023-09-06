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

import com.zimbra.cs.store.Blob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class InternalOverrideBlob extends com.zimbra.cs.store.Blob
{
  private final org.openzal.zal.Blob mBlob;
  private final boolean mHasMailboxInfo;

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
    return mBlob.getInputStream();
  }

  @Override
  public boolean isCompressed() throws IOException
  {
    return mBlob.isCompressed();
  }

  @Override
  public String getDigest() throws IOException
  {
    return mBlob.getDigest();
  }

  @Override
  public long getRawSize() throws IOException
  {
    return mBlob.getSize();
  }

  @Override
  public Blob setCompressed(boolean isCompressed)
  {
    throw new UnsupportedOperationException();
    //return this;
  }

  @Override
  public Blob setDigest(String digest)
  {
    mBlob.setDigest(digest);
    return this;
  }

  @Override
  public Blob setRawSize(long rawSize)
  {
    mBlob.setSize(rawSize);
    return this;
  }

  @Override
  public Blob copyCachedDataFrom(Blob other)
  {
    throw new UnsupportedOperationException();
    //return this;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }

  @Override
  public String toString()
  {
    return super.toString();
  }

  public InternalOverrideBlob(org.openzal.zal.Blob blob)
  {
    super(new File("/tmp/fake"));
    mBlob = blob;
    mHasMailboxInfo = false;
  }

  public InternalOverrideBlob(org.openzal.zal.MailboxBlob blob)
  {
    super(new File("/tmp/fake"));
    mBlob = blob;
    mHasMailboxInfo = true;
  }

  public org.openzal.zal.Blob getWrappedObject()
  {
    if (mHasMailboxInfo)
    {
      return (MailboxBlob) mBlob;
    }
    return mBlob;
  }

  public static Object wrap(org.openzal.zal.Blob src)
  {
    if (src instanceof BlobWrap)
      return src.toZimbra(Blob.class);

    if (src instanceof Blob)
      return src;

    return new InternalOverrideBlob(src);
  }
}
