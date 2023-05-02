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

package org.openzal.zal.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.MailboxBlob.MailboxBlobInfo;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.VolumeStagedBlob;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.openzal.zal.BlobWrap;
import org.openzal.zal.MailboxBlobWrap;
import org.openzal.zal.MailboxData;
import org.openzal.zal.Pair;
import org.openzal.zal.PrimaryStore;
import org.openzal.zal.StagedBlobWrap;
import org.openzal.zal.Store;
import org.openzal.zal.StoreFeature;
import org.openzal.zal.StoreVolume;
import org.openzal.zal.Utils;
import org.openzal.zal.VolumeManager;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.AnyThrow;
import org.openzal.zal.log.ZimbraLog;

import javax.annotation.Nullable;

import static com.zimbra.common.service.ServiceException.SENDERS_FAULT;
import static com.zimbra.cs.mailbox.MailServiceException.ITEM_ID;
import static com.zimbra.cs.mailbox.MailServiceException.NO_SUCH_BLOB;
import static com.zimbra.cs.mailbox.MailServiceException.REVISION;

class InternalOverrideStoreManager extends com.zimbra.cs.store.StoreManager {

  private final org.openzal.zal.StoreManager mStoreManager;
  private final VolumeManager mVolumeManager;

  public InternalOverrideStoreManager(
    org.openzal.zal.StoreManager storeManager,
    VolumeManager volumeManager
  )
  {
    mStoreManager = storeManager;
    mVolumeManager = volumeManager;
  }

  public void startup() throws IOException
  {
    mStoreManager.startup();
  }

  public void shutdown()
  {
    mStoreManager.shutdown();
  }

  public boolean supports(StoreManager.StoreFeature feature)
  {
     switch (feature)
    {
      case BULK_DELETE:
        return supportedByAll(feature);
      case CENTRALIZED:
        return supportedByAll(feature);
      case RESUMABLE_UPLOAD:
        return supportedByAll(feature);
      case SINGLE_INSTANCE_SERVER_CREATE:
        return supportedByAll(feature);
      case CUSTOM_STORE_API:
        return supportedAtLeasOnce(feature);
      default:
        return false;
    }
  }

  public boolean supports(StoreManager.StoreFeature storeFeature, String s)
  {
    return mStoreManager.getStore(s).supports(org.openzal.zal.StoreFeature.fromZimbra(storeFeature));
  }

  private boolean supportedAtLeasOnce(StoreFeature feature)
  {
    for (Store store : mStoreManager.getAllStores())
    {
      if (store.supports(org.openzal.zal.StoreFeature.fromZimbra(feature)))
      {
        return true;
      }
    }
    return false;
  }

  private boolean supportedByAll(StoreFeature feature)
  {
    for (Store store : mStoreManager.getAllStores())
    {
      if (!store.supports(org.openzal.zal.StoreFeature.fromZimbra(feature)))
      {
        return false;
      }
    }
    return true;
  }

  public BlobBuilder getBlobBuilder() throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().getBlobBuilder().toZimbra(BlobBuilder.class);
  }

  public Blob storeIncoming(InputStream data, boolean storeAsIs)
    throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().storeIncoming(data, storeAsIs).toZimbra(Blob.class);
  }

  public StagedBlob stage(InputStream data, long actualSize, Mailbox mbox)
    throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().stage(
      mStoreManager.getPrimaryStore().storeIncoming(data, false),
      new org.openzal.zal.Mailbox(mbox)
    ).toZimbra(StagedBlob.class);
  }

  public StagedBlob stage(Blob blob, Mailbox mbox) throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().stage(
      BlobWrap.wrapZimbraBlob(blob),
      new org.openzal.zal.Mailbox(mbox)
    ).toZimbra(StagedBlob.class);
  }

  public MailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      return mStoreManager.getPrimaryStore().copy(
        BlobWrap.wrapZimbraBlob(
          src.getLocalBlob(),
          src.getLocator()
        ),
        new org.openzal.zal.Mailbox(destMbox),
        destMsgId,
        destRevision
      ).toZimbra(MailboxBlob.class);
    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
  }

  public MailboxBlob link(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      return link(
        BlobWrap.wrapZimbraBlob(src, src.getLocator()),
        new org.openzal.zal.Mailbox(destMbox),
        destMsgId,
        destRevision
      );
    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
  }

  private MailboxBlob link(org.openzal.zal.Blob blob, org.openzal.zal.Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      return mStoreManager.getPrimaryStore().link(
        blob,
        destMbox,
        destMsgId,
        destRevision
      ).toZimbra(MailboxBlob.class);
    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
  }

  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      return mStoreManager.getPrimaryStore().renameTo(
        StagedBlobWrap.wrapZimbraObject(src),
        new org.openzal.zal.Mailbox(destMbox),
        destMsgId,
        destRevision
      ).toZimbra(MailboxBlob.class);

    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
  }

  public boolean delete(Blob blob) throws IOException
  {
    return mStoreManager.getPrimaryStore().delete(
      BlobWrap.wrapZimbraBlob(blob, null)
    );
  }

  private static final Method mVolumeStagedBlobWasStagedDirectlyMethod;
  private static final Constructor mMailServiceException;
  //private static final Method mExternalStagedBlobIsInsertedMethod;

  static
  {
    try
    {
      mVolumeStagedBlobWasStagedDirectlyMethod = VolumeStagedBlob.class.getDeclaredMethod("wasStagedDirectly");
      mMailServiceException = MailServiceException.class.getDeclaredConstructor(
        String.class, String.class, boolean.class, Throwable.class, MailServiceException.Argument[].class
      );
      //mExternalStagedBlobIsInsertedMethod = ExternalStagedBlob.class.getDeclaredMethod("isInserted");

      mVolumeStagedBlobWasStagedDirectlyMethod.setAccessible(true);
      mMailServiceException.setAccessible(true);
      //mExternalStagedBlobIsInsertedMethod.setAccessible(true);
    }
    catch (NoSuchMethodException e)
    {
      throw new RuntimeException("ZAL reflection error " + Utils.exceptionToString(e));
    }
  }

  public boolean delete(StagedBlob staged) throws IOException
  {
    if (staged == null)
    {
      return false;
    }

    return mStoreManager.getStore(staged.getLocator()).delete(StagedBlobWrap.wrapZimbraObject(staged));
  }

  public boolean delete(MailboxBlob blob) throws IOException
  {
    return mStoreManager.getStore(blob.getLocator()).delete(
      MailboxBlobWrap.wrapZimbraObject(
        blob
      )
    );
  }

  @Nullable
  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) throws ServiceException
  {
    try
    {
      org.openzal.zal.MailboxBlob blob = mStoreManager.getStore(locator).getMailboxBlob(
        new org.openzal.zal.Mailbox(mbox),
        itemId,
        revision
      );

      if (blob != null)
      {
        return blob.toZimbra(MailboxBlob.class);
      }
    }
    catch (Exception e)
    {
      ZimbraLog.mailbox.error(Utils.exceptionToString(e));
    }

    return null;
  }

  public MailboxBlob getMailboxBlob(Mailbox mailbox, int itemId, int revision, String locator, boolean validate)
    throws ServiceException
  {
    return getMailboxBlob(mailbox, itemId, revision, locator);
  }

  @Nullable
  public MailboxBlob getMailboxBlob(MailItem mailItem) throws ServiceException
  {
    MailboxBlob blob = getMailboxBlob(
      mailItem.getMailbox(), mailItem.getId(), mailItem.getSavedSequence(), mailItem.getLocator()
    );

    if( blob != null )
    {
      return blob.setSize(mailItem.getSize()).setDigest(mailItem.getDigest());
    }
    else
    {
      return null;
    }
  }

  @Nullable
  public InputStream getContent(MailboxBlob mboxBlob) throws IOException
  {
    org.openzal.zal.MailboxBlob zalMailboxBlob = MailboxBlobWrap.wrapZimbraObject(mboxBlob);
    try
    {
      Store store = mStoreManager.getStore(mboxBlob.getLocator());
      return store.getContent(zalMailboxBlob);
    }
    catch (Exception e)
    {
      try
      {
        ServiceException.Argument[] arguments = new ServiceException.Argument[2];
        arguments[0] = new ServiceException.Argument(ITEM_ID, zalMailboxBlob.getItemId(), ServiceException.Argument.Type.IID);
        arguments[1] = new ServiceException.Argument(REVISION, zalMailboxBlob.getRevision(), ServiceException.Argument.Type.NUM);
        AnyThrow.throwUnchecked(
          (Throwable) mMailServiceException.newInstance(
            "No such blob: mailbox=" + zalMailboxBlob.getMailbox().getId() + "," + " item=" + zalMailboxBlob.getItemId() + ", change=" + zalMailboxBlob.getRevision(),
            NO_SUCH_BLOB,
            SENDERS_FAULT,
            e,
            arguments
          )
        );
      }
      catch (Exception e1)
      {
        throw new RuntimeException(e1);
      }
      return null;
    }
  }

  public InputStream getContent(Blob blob) throws IOException
  {
    org.openzal.zal.Blob zalBlob = BlobWrap.wrapZimbraBlob(blob);
    if (zalBlob.hasMailboxInfo())
    {
      org.openzal.zal.MailboxBlob zalMailboxBlob = null;
      try
      {
        zalMailboxBlob = zalBlob.toMailboxBlob();
        Store store = mStoreManager.getStore(zalBlob.getVolumeId());
        return store.getContent(zalBlob.toMailboxBlob());
      }
      catch (Exception e)
      {
        if (zalMailboxBlob != null)
        {
          try
          {
            ServiceException.Argument[] arguments = new ServiceException.Argument[2];
            arguments[0] = new ServiceException.Argument(ITEM_ID, zalMailboxBlob.getItemId(), ServiceException.Argument.Type.IID);
            arguments[1] = new ServiceException.Argument(REVISION, zalMailboxBlob.getRevision(), ServiceException.Argument.Type.NUM);
            AnyThrow.throwUnchecked(
              (Throwable) mMailServiceException.newInstance(
                "No such blob: mailbox=" + zalMailboxBlob.getMailbox().getId() + "," + " item=" + zalMailboxBlob.getItemId() + ", change=" + zalMailboxBlob.getRevision(),
                NO_SUCH_BLOB,
                SENDERS_FAULT,
                e,
                arguments
              )
            );
          }
          catch (Exception e1)
          {
            throw new RuntimeException(e1);
          }
        }
        else
        {
          AnyThrow.throwUnchecked(e);
        }
        return null;
      }
    }
    else
    {
      try
      {
        PrimaryStore store = mStoreManager.getStore(zalBlob.getVolumeId()).toPrimaryStore();
        return store.getContent(zalBlob);
      }
      catch (Exception e)
      {
        try
        {
          ServiceException.Argument[] arguments = new ServiceException.Argument[2];
          arguments[0] = new ServiceException.Argument("volumeId", zalBlob.getVolumeId(), ServiceException.Argument.Type.STR);
          arguments[1] = new ServiceException.Argument("blobPath", zalBlob.getKey(), ServiceException.Argument.Type.STR);
          AnyThrow.throwUnchecked(
            (Throwable) mMailServiceException.newInstance(
              "No such blob: " + zalBlob.getKey() + ", volume=" + zalBlob.getVolumeId(),
              NO_SUCH_BLOB,
              false,
              e,
              arguments
            )
          );
        }
        catch (Exception e1)
        {
          throw new RuntimeException(e1);
        }
        return null;
      }
    }
  }

  public boolean deleteStore(final Mailbox mbox, final Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException
  {
    org.openzal.zal.Mailbox mailbox = new org.openzal.zal.Mailbox(mbox);
    for (StoreVolume volume : mVolumeManager.getAll())
    {
      Store store = mStoreManager.getStore(volume.getId());

      // blobs should be always != null if BULK_DELETE feature is not supported
      if (store.supports(org.openzal.zal.StoreFeature.BULK_DELETE) || blobs == null) {
        store.delete(mailbox, null);
      } else {
        store.delete(mailbox, new Iterable() {
          @Override
          public Iterator iterator() {
            final Iterator<MailboxBlobInfo> iterator = blobs.iterator();
            return new Iterator() {
              @Override
              public boolean hasNext() {
                return iterator.hasNext();
              }

              @Override
              public Object next() {
                MailboxBlobInfo next = iterator.next();

                return new Pair<MailboxData, ZalItemData>(
                    new MailboxData(mbox.getId(), next.accountId),
                    new ZalItemData(next.itemId, next.revision)
                );
              }
            };
          }
        });
      }
    }
    return true;
  }

  public boolean quietDelete(Blob blob)
  {
    if (blob == null)
    {
      return false;
    }
    org.openzal.zal.Blob zalBlob = BlobWrap.wrapZimbraBlob(blob);
    try
    {
      return toPrimaryStore(mStoreManager.getStore(zalBlob.getVolumeId())).delete(zalBlob);
    }
    catch (Throwable t)
    {
      return false;
    }
  }

  private PrimaryStore toPrimaryStore(Store store)
  {
    if (! store.canBePrimary())
    {
      throw new RuntimeException("Store " + store.getVolumeId() + " cannot be primary");
    }
    return store.toPrimaryStore();
  }

  public Object getWrapped()
  {
    return mStoreManager;
  }
}
