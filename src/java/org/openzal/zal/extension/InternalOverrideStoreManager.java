package org.openzal.zal.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.VolumeStagedBlob;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.AnyThrow;
import org.openzal.zal.log.ZimbraLog;
/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.store.StorageCallback;
/* $endif $ */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class InternalOverrideStoreManager
  extends com.zimbra.cs.store.StoreManager
{
  private final org.openzal.zal.StoreManager mStoreManager;
  private final VolumeManager mVolumeManager;
  // TODO maybe PrimaryStoreAccessr?

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

  /* $if ZimbraVersion >= 7.2.0 $ */
  public boolean supports(StoreManager.StoreFeature feature)
  {
    return mStoreManager.getPrimaryStore().supports(org.openzal.zal.StoreFeature.fromZimbra(feature));
  }
  /* $endif $ */

  public BlobBuilder getBlobBuilder() throws IOException, ServiceException
  {
    throw new UnsupportedOperationException();
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public Blob storeIncoming(InputStream data, boolean storeAsIs)
  /* $elseif ZimbraVersion >= 7.0.0 $
  public Blob storeIncoming(InputStream data, StorageCallback callback, boolean storeAsIs)
  /* $else $
  public Blob storeIncoming(InputStream data, long actualSize, StorageCallback callback, boolean storeAsIs)
  //TODO check callback not null -> RuntimeException
  /* $endif $ */
    throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().storeIncoming(data, storeAsIs).toZimbra(Blob.class);
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public StagedBlob stage(InputStream data, long actualSize, Mailbox mbox)
  /* $else $
  public StagedBlob stage(InputStream data, long actualSize, StorageCallback callback, Mailbox mbox)
  /* $endif $   */
    throws IOException, ServiceException
  {
    return mStoreManager.getPrimaryStore().storeIncoming(data, false).toZimbra(StagedBlob.class);
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

  /* $if ZimbraVersion < 8.0.0 $
  public MailboxBlob link(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      org.openzal.zal.Blob blob = BlobWrap.wrapZimbraBlob(src, src.getLocator());
      return link(
        blob, new org.openzal.zal.Mailbox(destMbox), destMsgId, destRevision
      );
    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
  }
  /* $endif $ */

  public MailboxBlob link(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    try
    {
      /* $if ZimbraVersion >= 7.0.0 $ */
      org.openzal.zal.Blob blob = BlobWrap.wrapZimbraBlob(src, src.getLocator());
      /* $else $
      org.openzal.zal.Blob blob = BlobWrap.wrapZimbraBlob(src, src.getStagedLocator());
      /* $endif $ */
      return link(
        blob,
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

    if (VolumeStagedBlob.class.isAssignableFrom(staged.getClass()))
    {
      VolumeStagedBlob vsb = (VolumeStagedBlob) staged;
      try
      {
        if (!(Boolean) mVolumeStagedBlobWasStagedDirectlyMethod.invoke(vsb))
        {
          return false;
        }
      }
      catch (Exception e)
      {
        throw new RuntimeException(e);
      }

      return delete(vsb);
    }
    else
    {
      return false;
    }
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
    return getMailboxBlob(
      mailItem.getMailbox(), mailItem.getId(), mailItem.getSavedSequence(), mailItem.getLocator()
    );
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
      AnyThrow.throwUnchecked(
        MailServiceException.NO_SUCH_BLOB(zalMailboxBlob.getMailbox().getId(), zalMailboxBlob.getItemId(), zalMailboxBlob.getRevision())
      );
      return null;
    }
  }

  public InputStream getContent(Blob blob) throws IOException
  {
    org.openzal.zal.Blob zalBlob = BlobWrap.wrapZimbraBlob(blob);
    if (zalBlob.hasMailboxInfo())
    {
      try
      {
        Store store = mStoreManager.getStore(zalBlob.getVolumeId());
        return store.getContent(zalBlob.toMailboxBlob());
      }
      catch (Exception e)
      {
        AnyThrow.throwUnchecked(
          MailServiceException.NO_SUCH_BLOB(
            zalBlob.toMailboxBlob().getMailbox().getId(),
            zalBlob.toMailboxBlob().getItemId(),
            zalBlob.toMailboxBlob().getRevision()
          )
        );
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
          AnyThrow.throwUnchecked(
            (Throwable) mMailServiceException.newInstance(
              "No such blob: " + zalBlob.getKey() + ", volume=" + zalBlob.getVolumeId(),
              MailServiceException.NO_SUCH_BLOB,
              false,
              new MailServiceException.Argument[0]
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

  /* $if ZimbraVersion >= 7.2.1 $ */
  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException
  {
    Iterable blobsCollection = blobs;
  /* $elseif ZimbraVersion >= 7.2.0 $
  public boolean deleteStore(Mailbox mbox, Iterable blobs) throws IOException, ServiceException
  {
    Iterable blobsCollection = blobs;
  /* $else $
  public boolean deleteStore(Mailbox mbox) throws IOException, ServiceException
  {
    Iterable blobsCollection = null;
  /* $endif $ */
    org.openzal.zal.Mailbox mailbox = new org.openzal.zal.Mailbox(mbox);
    for (StoreVolume volume : mVolumeManager.getAll())
    {
      mStoreManager.getStore(volume.getId()).delete(mailbox, blobsCollection);
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
