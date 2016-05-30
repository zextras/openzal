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
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.VolumeMailboxBlob;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
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

    return MailboxBlobWrap.wrapZimbraObject(mailboxBlob);
  }

  @NotNull
  public Future<MailboxBlob> copy(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision)
  {
    return copy(src, destMbox, destMsgId, destRevision, mVolumeManager.getCurrentMessageVolume().getId());
  }

  @Override
  public Future<MailboxBlob> copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, String volumeId)
  {
    Promise<MailboxBlob> future = new DefaultPromise<MailboxBlob>(ImmediateEventExecutor.INSTANCE);
    try
    {
      VolumeMailboxBlob volumeMailboxBlob = getFileBlobStore().copy(
        com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(src)),
        destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
        destMsgId,
        destRevision,
        volumeId
      );

      return future.setSuccess(MailboxBlobWrap.wrapZimbraObject(volumeMailboxBlob));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      return future.setFailure(ExceptionWrapper.wrap(e));
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
  }

  @NotNull
  public Future<MailboxBlob> link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision)
  {
    return link(src, destMbox, destMsgId, destRevision, mVolumeManager.getCurrentMessageVolume().getId());
  }

  @Override
  public Future<MailboxBlob> renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
  {
    return null;
  }

  @NotNull
  public Future<MailboxBlob> link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision, String volumeId)
  {
    Promise<MailboxBlob> future = new DefaultPromise<MailboxBlob>(ImmediateEventExecutor.INSTANCE);
    try
    {
      VolumeMailboxBlob volumeMailboxBlob = getFileBlobStore().link(
        com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(src)),
        destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
        destMsgId,
        destRevision,
        volumeId
      );

      return future.setSuccess(MailboxBlobWrap.wrapZimbraObject(volumeMailboxBlob));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      return future.setFailure(ExceptionWrapper.wrap(e));
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
  }

  public Future<Boolean> delete(@NotNull Blob blob)
  {
    Promise<Boolean> future = new DefaultPromise<Boolean>(ImmediateEventExecutor.INSTANCE);
    try
    {
      return future.setSuccess(
        sm.delete(com.zimbra.cs.store.Blob.class.cast(InternalOverrideFactory.wrapBlob(blob)))
      );
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
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
  public BlobBuilder getBlobBuilder() throws IOException, ZimbraException
  {
    try
    {
      return BlobBuilderWrap.wrap(sm.getBlobBuilder(), mVolumeManager.getCurrentMessageVolume().getId());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Future<Blob> storeIncoming(InputStream data, boolean storeAsIs)
  {
    Promise<Blob> future = new DefaultPromise<Blob>(ImmediateEventExecutor.INSTANCE);
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
      return future.setSuccess(
        BlobWrap.wrapZimbraObject(blob, mVolumeManager.getCurrentMessageVolume().getId())
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      return future.setFailure(ExceptionWrapper.wrap(e));
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
  }

  @Override
  public Future<StagedBlob> stage(Blob blob, Mailbox mbox)
  {
    Promise<StagedBlob> future = new DefaultPromise<StagedBlob>(ImmediateEventExecutor.INSTANCE);
    try
    {
      return future.setSuccess(
        StagedBlobWrap.wrapZimbraObject(
          sm.stage(
            blob.toZimbra(com.zimbra.cs.store.Blob.class),
            mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class)
          )
        )
      );
    }
    catch (ServiceException e)
    {
      return future.setFailure(ExceptionWrapper.wrap(e));
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
  }

  @Override
  @Nullable
  public InputStream getContent(Blob blob) throws IOException
  {
    return sm.getContent(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }

  @Override
  public Future<Boolean> delete(Mailbox mailbox, @Nullable Iterable blobs, String volumeId)
  {
    return null;
  }

  private String getBlobDir(String volumeId, int mboxId, int itemId )
  {
    StoreVolume vol = mVolumeManager.getById(volumeId);
    return vol.getBlobDir(mboxId, itemId);
  }

  @NotNull
  public String getBlobPath(int mboxId, int itemId, int revision,
                                  String volumeId) throws org.openzal.zal.exceptions.ZimbraException
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
  public void registerStoreAccessor(StoreAccessorFactory storeAccessorFactory, String volumeId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void invalidateStoreAccessor(Collection volumes)
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
  public Future<Boolean> delete(StagedBlob blob)
  {
    return null;
  }

  @Override
  public Future<Boolean> delete(MailboxBlob blob)
  {
    return null;
  }

  @Override
  public Future<Boolean> delete(Mailbox mailbox, @Nullable Iterable blobs)
  {
    Promise<Boolean> future = new DefaultPromise<Boolean>(ImmediateEventExecutor.INSTANCE);
    try
    {
      return future.setSuccess(
        /* $if ZimbraVersion >= 7.2.0 $ */
        sm.deleteStore(mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class), blobs)
        /* $else $
        sm.deleteStore(mailbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class));
        /* $endif $ */
      );
    }
    catch (ServiceException e)
    {
      return future.setFailure(ExceptionWrapper.wrap(e));
    }
    catch (IOException e)
    {
      return future.setFailure(e);
    }
  }
}
