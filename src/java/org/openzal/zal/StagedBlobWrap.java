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

import com.zimbra.cs.store.file.VolumeBlobProxy;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class StagedBlobWrap<S extends Blob> implements StagedBlob
{
  private final com.zimbra.cs.store.file.VolumeStagedBlob mStagedBlob;

  public StagedBlobWrap(@NotNull Object stagedBlob)
  {
    mStagedBlob = (com.zimbra.cs.store.file.VolumeStagedBlob) stagedBlob;
  }

  public Mailbox getMailbox()
  {
    return new Mailbox(mStagedBlob.getMailbox());
  }

  public long getSize()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getSize();
    /* $else $
    return mStagedBlob.getStagedSize();
    /* $endif $ */
  }

  public String getLocator()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getLocator();
    /* $else $
    return mStagedBlob.getStagedLocator();
    /* $endif $ */
  }

  public String getDigest()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getDigest();
    /* $else $
    return mStagedBlob.getStagedDigest();
    /* $endif $ */
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return new VolumeBlobProxy(mStagedBlob.getLocalBlob()).getInputStream();
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
  public StagedBlobWrap setDigest(String digest)
  {
    return this;
  }

  @Override
  public StagedBlobWrap setSize(long size)
  {
    return this;
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    wrapZimbraObject(mStagedBlob).renameTo(newPath);
  }

  @Override
  public String getKey()
  {
    return wrapZimbraObject(mStagedBlob).getKey();
  }

  @Override
  public File getFile()
  {
    return wrapZimbraObject(mStagedBlob).getFile();
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mStagedBlob);
  }

  @Override
  public String getVolumeId()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return mStagedBlob.getLocator();
    /* $else $
    return mStagedBlob.getStagedLocator();
    /* $endif $ */
  }

  @Override
  public Blob getLocalBlob()
  {
    /* $if ZimbraVersion >= 7.0.0 $ */
    return BlobWrap.wrapZimbraBlob(mStagedBlob.getLocalBlob(), mStagedBlob.getLocator());
    /* $else $
    return BlobWrap.wrapZimbraBlob(mStagedBlob.getLocalBlob(), mStagedBlob.getStagedLocator());
    /* $endif $ */
  }

  public static StagedBlob wrapZimbraObject(Object stagedBlob)
  {
    if (stagedBlob instanceof InternalOverrideStagedBlob)
    {
      return ((InternalOverrideStagedBlob) stagedBlob).getWrappedObject();
    }

    return new StagedBlobWrap(stagedBlob);
  }
}
