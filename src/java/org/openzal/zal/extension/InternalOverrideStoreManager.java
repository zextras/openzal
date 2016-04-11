package org.openzal.zal.extension;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.store.file.BlobWrap;
import com.zimbra.cs.store.file.VolumeStagedBlob;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.*;
/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.store.StorageCallback;
/* $endif $ */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

class InternalOverrideStoreManager extends com.zimbra.cs.store.StoreManager
{
  private final VolumeManager                mVolumeManager;
  private       org.openzal.zal.StoreManager mZALStoreManager;

  public InternalOverrideStoreManager(
    VolumeManager volumeManager
  )
  {
    mVolumeManager = volumeManager;
  }

  public void startup() throws IOException, ServiceException
  {
    mZALStoreManager.startup();
  }

  public void shutdown()
  {
    mZALStoreManager.shutdown();
  }

  /* $if ZimbraVersion >= 7.2.0 $ */
  public boolean supports(StoreManager.StoreFeature feature)
  {
    return mZALStoreManager.supports(org.openzal.zal.StoreFeature.fromZimbra(feature));
  }
  /* $endif $ */

  public BlobBuilder getBlobBuilder() throws IOException, ServiceException
  {
    return mZALStoreManager.getBlobBuilder(mVolumeManager.getCurrentMessageVolume().getId()).toZimbra(BlobBuilder.class);
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
    return mZALStoreManager.storeIncoming(data, storeAsIs).toZimbra(Blob.class);
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public StagedBlob stage(InputStream data, long actualSize, Mailbox mbox)
  /* $else $
  public StagedBlob stage(InputStream data, long actualSize, StorageCallback callback, Mailbox mbox)
  /* $endif $   */
    throws IOException, ServiceException
  {
    return mZALStoreManager.stage(
      mZALStoreManager.storeIncoming(data, false),
      new org.openzal.zal.Mailbox(mbox)
    ).toZimbra(StagedBlob.class);
  }

  public StagedBlob stage(Blob blob, Mailbox mbox) throws IOException, ServiceException
  {
    return mZALStoreManager.stage(
      BlobWrap.wrap(
        blob,
        mVolumeManager.getCurrentMessageVolume().getId()
      ),
      new org.openzal.zal.Mailbox(mbox)
    ).toZimbra(StagedBlob.class);
  }

  public MailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    return mZALStoreManager.copy(
      BlobWrap.wrap(
        src.getLocalBlob(),
        mVolumeManager.getCurrentMessageVolume().getId()
      ),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId, destRevision
    ).toZimbra(MailboxBlob.class);
  }

  public MailboxBlob link(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    return mZALStoreManager.link(
      StagedBlobWrap.wrap(src).getBlob(),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId,
      destRevision
    ).toZimbra(MailboxBlob.class);
  }

  /* $if ZimbraVersion < 8.0.0 $
  public MailboxBlob link(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    return mZALStoreManager.link(
      BlobWrap.wrap(src.getLocalBlob(), Short.parseShort(src.getLocator())),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId,
      destRevision
    ).toZimbra(MailboxBlob.class);
  }
  /* $endif $ */

  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    return mZALStoreManager.renameTo(
      StagedBlobWrap.wrap(src),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId,
      destRevision
    ).toZimbra(MailboxBlob.class);
  }

  public boolean delete(Blob blob) throws IOException
  {
    return mZALStoreManager.delete(
      BlobWrap.wrap(blob, mVolumeManager.getCurrentMessageVolume().getId())
    );
  }

  private static final Method mVolumeStagedBlobWasStagedDirectlyMethod;
  //private static final Method mExternalStagedBlobIsInsertedMethod;

  static
  {
    try
    {
      mVolumeStagedBlobWasStagedDirectlyMethod = VolumeStagedBlob.class.getDeclaredMethod("wasStagedDirectly");
      //mExternalStagedBlobIsInsertedMethod = ExternalStagedBlob.class.getDeclaredMethod("isInserted");

      mVolumeStagedBlobWasStagedDirectlyMethod.setAccessible(true);
      //mExternalStagedBlobIsInsertedMethod.setAccessible(true);
    }
    catch (NoSuchMethodException e)
    {
      throw new RuntimeException(e);
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

      String locator;
      /* $if ZimbraVersion >= 7.0.0 $ */
      locator = staged.getLocator();
      /* $else $
      locator = staged.getStagedLocator();
      /* $endif $ */
      return mZALStoreManager.delete(
        BlobWrap.wrap(
          vsb.getLocalBlob(),
          mVolumeManager.getCurrentMessageVolume().getId()
        ),
        locator
      );
    }
    else
    {
      return false;
    }
  }

  public boolean delete(MailboxBlob mblob) throws IOException
  {
    return mZALStoreManager.delete(
      BlobWrap.wrap(
        mblob.getLocalBlob(),
        mVolumeManager.getCurrentMessageVolume().getId()
      )
    );
  }

  @Nullable
  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) throws ServiceException
  {
    org.openzal.zal.MailboxBlob blob = mZALStoreManager.getMailboxBlob(
      new org.openzal.zal.Mailbox(mbox),
      itemId,
      revision,
      locator
    );
    if (blob == null)
    {
      return null;
    }

    return blob.toZimbra(MailboxBlob.class);
  }

  @Nullable
  public InputStream getContent(MailboxBlob mboxBlob) throws IOException
  {
    if (mboxBlob == null)
    {
      return null;
    }

    return mZALStoreManager.getContent(
      BlobWrap.wrap(
        mboxBlob.getLocalBlob(),
        mVolumeManager.getCurrentMessageVolume().getId()
      ),
      mboxBlob.getLocator()
    );
  }

  public InputStream getContent(Blob blob) throws IOException
  {
    return mZALStoreManager.getContent(
      BlobWrap.wrap(
        blob,
        mVolumeManager.getCurrentMessageVolume().getId()
      )
    );
  }

  /* $if ZimbraVersion >= 7.2.1 $ */
  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException
  /* $elseif ZimbraVersion >= 7.2.0 $
  public boolean deleteStore(Mailbox mbox, Iterable blobs) throws IOException, ServiceException
  /* $else $
  public boolean deleteStore(Mailbox mbox) throws IOException, ServiceException
  /* $endif $ */
  {
    org.openzal.zal.Mailbox mailbox = new org.openzal.zal.Mailbox(mbox);
    return mZALStoreManager.delete(mailbox);
  }

  public void setZALStoreManager(org.openzal.zal.StoreManager zalStoreManager)
  {
    mZALStoreManager = zalStoreManager;
  }

  public org.openzal.zal.StoreManager toZal()
  {
    return mZALStoreManager;
  }
}
