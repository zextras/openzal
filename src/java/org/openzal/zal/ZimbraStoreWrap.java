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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.BlobWrap;
import com.zimbra.cs.store.file.InternalOverrideBlob;
import com.zimbra.cs.store.file.InternalOverrideVolumeBlob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.store.file.FileBlobStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class ZimbraStoreWrap implements org.openzal.zal.StoreManager
{
  protected final com.zimbra.cs.store.StoreManager sm;
  private final VolumeManager mVolumeManager;

  @Inject
  public ZimbraStoreWrap(
    @Assisted Object storeManager,
    VolumeManager volumeManager
  )
  {
    this.sm = (com.zimbra.cs.store.StoreManager) storeManager;
    mVolumeManager = volumeManager;
  }

  @NotNull
  public FileBlobStoreWrap getFileBlobStore()
  {
    return new FileBlobStoreWrapImpl((FileBlobStore) sm);
  }

  @Nullable
  public MailboxBlob getMailboxBlob(@NotNull Mailbox mbox, int msgId, int revision, String locator)
    throws ZimbraException
  {
    com.zimbra.cs.store.MailboxBlob mailboxBlob;
    try
    {
      mailboxBlob = sm.getMailboxBlob(mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
                          msgId,
                          revision,
                          locator);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    if (mailboxBlob == null)
    {
      return null;
    }

    return MailboxBlobWrap.wrap(mailboxBlob);
  }

  @NotNull
  public MailboxBlob copy(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ZimbraException
  {
    return copy(src, destMbox, destMsgId, destRevision, mVolumeManager.getCurrentMessageVolume().getId());
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, short volumeId)
    throws IOException, ZimbraException
  {
    try
    {
      return MailboxBlobWrap.wrap(
        getFileBlobStore().copy(
          com.zimbra.cs.store.Blob.class.cast(InternalOverrideBlob.wrap(src)),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          volumeId
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public MailboxBlob link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ZimbraException
  {
    return link(src, destMbox, destMsgId, destRevision, mVolumeManager.getCurrentMessageVolume().getId());
  }

  @NotNull
  public MailboxBlob link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision, short volumeId)
    throws IOException, ZimbraException
  {
    try
    {
      return MailboxBlobWrap.wrap(
        getFileBlobStore().link(
          com.zimbra.cs.store.Blob.class.cast(new InternalOverrideVolumeBlob(src, volumeId)),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          volumeId
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean delete(@NotNull Mailbox mailbox) throws IOException
  {
    try
    {
      /* $if ZimbraVersion >= 7.2.0 $ */
      return sm.deleteStore(mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class), null);
      /* $else $
      return sm.deleteStore(mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class));
      /* $endif $ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean delete(@NotNull Blob blob) throws IOException
  {
    return sm.delete(com.zimbra.cs.store.Blob.class.cast(InternalOverrideBlob.wrap(blob)));
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
    /* $if ZimbraVersion >= 7.2.0 $ */
    return sm.supports(feature.toZimbra(StoreManager.StoreFeature.class));
    /* $else $
    return false;
    /* $endif $ */
  }

  @Override
  public BlobBuilder getBlobBuilder(short volumeId) throws IOException, ZimbraException
  {
    try
    {
      return BlobBuilderWrap.wrap(sm.getBlobBuilder(), volumeId);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Blob storeIncoming(InputStream data, boolean storeAsIs) throws IOException, ZimbraException
  {
    try
    {
      com.zimbra.cs.store.Blob blob;
      /* $if ZimbraVersion >= 8.0.0 $ */
      blob = sm.storeIncoming(data, storeAsIs);
      /* $elseif ZimbraVersion >= 7.0.0 $
      blob = sm.storeIncoming(data, null, storeAsIs);
      /* $else $
      blob = sm.storeIncoming(data, 0L, null, storeAsIs);
      /* $endif $ */
      return BlobWrap.wrap(blob, mVolumeManager.getCurrentMessageVolume().getId());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public StagedBlobWrap stage(Blob blob, Mailbox mbox) throws IOException
  {
    try
    {
      return StagedBlobWrap.wrap(
        sm.stage(
          blob.toZimbra(com.zimbra.cs.store.Blob.class),
          mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class)
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public InputStream getContent(Blob blob) throws IOException
  {
    return sm.getContent(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }

  @Override
  public MailboxBlob renameTo(org.openzal.zal.StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    try
    {
      return MailboxBlobWrap.wrap(
        sm.renameTo(
          src.toZimbra(StagedBlob.class),
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

  private String getBlobDir(short volumeId, int mboxId, int itemId )
  {
    StoreVolume vol = mVolumeManager.getById(volumeId);
    return vol.getBlobDir(mboxId, itemId);
  }

  @NotNull
  public String getBlobPath(int mboxId, int itemId, int revision,
                                  short volumeId) throws org.openzal.zal.exceptions.ZimbraException
  {
    String path = getBlobDir(volumeId, mboxId, itemId);

    int buflen = path.length() + 15 + (revision < 0 ? 0 : 11);
    StringBuilder sb = new StringBuilder(buflen);

    sb.append(path).append(File.separator).append(itemId);
    if( revision >= 0 ) {
      sb.append('-').append(revision);
    }
    sb.append(".msg");

    return sb.toString();
  }

  public static void quietDelete(@NotNull MailboxBlob blob)
  {
    com.zimbra.cs.store.StoreManager.getInstance().quietDelete(
      blob.toZimbra(com.zimbra.cs.store.MailboxBlob.class)
    );
  }

  @Override
  public void registerStoreAccessor(StoreAccessorFactory storeAccessorFactory, short volumeId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void invalidateStoreAccessor(Collection<Short> volumes)
  {
    throw new UnsupportedOperationException();
  }

  @Nullable
  @Override
  public InputStream getContent(Blob blob, String volumeId) throws IOException
  {
    return sm.getContent(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }

  @Override
  public boolean delete(Blob blob, String volumeId) throws IOException
  {
    return sm.delete(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }
}
