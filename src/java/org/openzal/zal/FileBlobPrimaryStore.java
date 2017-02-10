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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.store.StoreManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileBlobPrimaryStore implements PrimaryStore
{
  private final FileBlobStoreWrap sm;
  private final StoreVolume       mVolume;

  public FileBlobPrimaryStore(
    Object storeManager,
    StoreVolume volume
  )
  {
    this.sm = (FileBlobStoreWrap) storeManager;
    mVolume = volume;
    // TODO handle compression
    // mVolume.getCompressBlobs()
  }

  @Override
  public String getVolumeId()
  {
    return mVolume.getId();
  }

  @Override
  public String getVolumeName()
  {
    return mVolume.getName();
  }

  @Override
  public String getBlobPath(int mboxId, int itemId, int revision)
  {
    String path = mVolume.getBlobDir(mboxId, itemId);
    path = path.startsWith(File.separator)?path:File.separator+path;

    int buflen = path.length() + 15 + (revision < 0 ? 0 : 11);
    StringBuilder sb = new StringBuilder(buflen);

    sb.append(path).append("/").append(itemId);
    if( revision >= 0 ) {
      sb.append('-').append(revision);
    }
    sb.append(".msg");

    return sb.toString();
  }

  @Override
  public String getMailboxDirPath(int mboxId)
  {
    return mVolume.getMailboxDir(mboxId, (short) 1);
  }

  @Override
  public String getRootPath()
  {
    return mVolume.getRootPath();
  }

  @Override
  public boolean isCompressed()
  {
    return mVolume.getCompressBlobs();
  }

  @Override
  public long getCompressionThreshold()
  {
    return mVolume.getCompressionThreshold();
  }

  @Nullable
  @Override
  public MailboxBlob getMailboxBlob(@NotNull Mailbox mbox, int msgId, int revision)
    throws ZimbraException
  {
    com.zimbra.cs.store.MailboxBlob mailboxBlob;
    try
    {
      mailboxBlob = sm.getMailboxBlob(
        mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
        msgId,
        revision,
        mVolume.getId()
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if (mailboxBlob == null)
    {
      return null;
    }

    return MailboxBlobWrap.wrapZimbraObject(mailboxBlob);
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ZimbraException
  {
    try
    {
      return MailboxBlobWrap.wrapZimbraObject(
        sm.copy(
          com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(src)),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          mVolume.getId()
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public MailboxBlob link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ZimbraException
  {
    try
    {
      return MailboxBlobWrap.wrapZimbraObject(
        sm.link(
          com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(src)),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          mVolume.getId()
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ZimbraException
  {
    try
    {
      return MailboxBlobWrap.wrapZimbraObject(
        sm.renameTo(
          src.toZimbra(com.zimbra.cs.store.StagedBlob.class),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public boolean delete(Blob blob) throws IOException
  {
    return sm.delete(com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(blob)));
  }

  public boolean delete(@NotNull MailboxBlob blob)
    throws IOException
  {
    return sm.delete(com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(blob.getLocalBlob())));
  }

  @Override
  public void startup() throws IOException, ZimbraException
  {
    try
    {
      sm.startup();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void shutdown()
  {
    sm.shutdown();
  }

  @Override
  public boolean supports(StoreFeature feature)
  {
    return sm.supports(feature.toZimbra(StoreManager.StoreFeature.class));
  }

  @Override
  public InputStream getContent(MailboxBlob blob) throws IOException
  {
    return sm.getContent(blob.toZimbra(com.zimbra.cs.store.MailboxBlob.class));
  }

  @Override
  public Blob storeIncoming(InputStream data, boolean storeAsIs)
    throws IOException, ZimbraException
  {
    try
    {
      com.zimbra.cs.store.Blob blob = sm.storeIncoming(data, storeAsIs);
      return BlobWrap.wrapZimbraBlob(blob, mVolume.getId());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public StagedBlob stage(Blob blob, Mailbox mbox)
    throws IOException, ZimbraException
  {
    return StagedBlobWrap.wrapZimbraObject(
      sm.stage(
        blob.toZimbra(com.zimbra.cs.store.Blob.class),
        mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class)
      )
    );
  }

  @Override
  @Nullable
  public InputStream getContent(Blob blob) throws IOException
  {
    return sm.getContent(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }

  @Override
  public <T> T toZimbra(Class<T> claz)
  {
    return claz.cast(sm.getWrappedObject());
  }

  @Override
  public boolean delete(Mailbox mailbox, @Nullable Iterable blobs)
    throws IOException
  {
    try
    {
      return sm.deleteStore(mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class), blobs);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public PrimaryStore toPrimaryStore()
  {
    return this;
  }

  @Override
  public boolean canBePrimary()
  {
    return true;
  }
}
