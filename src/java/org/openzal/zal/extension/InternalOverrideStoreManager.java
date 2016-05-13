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
import io.netty.util.concurrent.Future;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.*;
import org.openzal.zal.exceptions.ZimbraException;
/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.store.StorageCallback;
/* $endif $ */

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

class InternalOverrideStoreManager
  extends com.zimbra.cs.store.StoreManager
{
  private org.openzal.zal.StoreManager mZALStoreManager;

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
    return (BlobBuilder) mZALStoreManager.getBlobBuilder().toZimbra(BlobBuilder.class);
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
    try
    {
      Future<org.openzal.zal.Blob> future = mZALStoreManager.storeIncoming(data, storeAsIs).sync();

      if (!future.isSuccess())
      {
        throw future.cause();
      }

      return future.getNow().toZimbra(Blob.class);
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public StagedBlob stage(InputStream data, long actualSize, Mailbox mbox)
  /* $else $
  public StagedBlob stage(InputStream data, long actualSize, StorageCallback callback, Mailbox mbox)
  /* $endif $   */
    throws IOException, ServiceException
  {
    Future<org.openzal.zal.Blob> futureStoreIncoming;
    try
    {
      futureStoreIncoming = mZALStoreManager.storeIncoming(data, false).sync();

      if (!futureStoreIncoming.isSuccess())
      {
        throw futureStoreIncoming.cause();
      }
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }

    Future<org.openzal.zal.StagedBlob> futureStage;
    try
    {
      futureStage = mZALStoreManager.stage(
        futureStoreIncoming.getNow(),
        new org.openzal.zal.Mailbox(mbox)
      ).sync();

      if (!futureStage.isSuccess())
      {
        throw futureStage.cause();
      }
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return futureStage.getNow().toZimbra(StagedBlob.class);
  }

  public StagedBlob stage(Blob blob, Mailbox mbox) throws IOException, ServiceException
  {
    Future<org.openzal.zal.StagedBlob> futureStage;
    try
    {
      futureStage = mZALStoreManager.stage(
        BlobWrap.wrapZimbraObject(
          blob
        ),
        new org.openzal.zal.Mailbox(mbox)
      ).sync();

      if (!futureStage.isSuccess())
      {
        throw futureStage.cause();
      }
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return futureStage.getNow().toZimbra(StagedBlob.class);
  }

  public MailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    Future<org.openzal.zal.MailboxBlob> future = mZALStoreManager.copy(
      BlobWrap.wrapZimbraObject(
        src.getLocalBlob()
      ),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId, destRevision
    );
    try
    {
      future.sync();
      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return future.getNow().toZimbra(MailboxBlob.class);
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  public MailboxBlob link(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    Future<org.openzal.zal.MailboxBlob> future = mZALStoreManager.link(
      BlobWrap.wrapZimbraObject(src),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId,
      destRevision
    );
  /* $else $
  public MailboxBlob link(MailboxBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    Future<org.openzal.zal.MailboxBlob> future = mZALStoreManager.link(
      BlobWrap.wrapZimbraObject(src),
      new org.openzal.zal.Mailbox(destMbox),
      destMsgId,
      destRevision
    );
  /* $endif $ */

    try
    {
      future.sync();

      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (ServiceException e)
    {
      throw e;
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return future.getNow().toZimbra(MailboxBlob.class);
  }

  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destMsgId, int destRevision)
    throws IOException, ServiceException
  {
    Future<org.openzal.zal.MailboxBlob> future;
    try
    {
      future = mZALStoreManager.renameTo(
        StagedBlobWrap.wrapZimbraObject(src),
        new org.openzal.zal.Mailbox(destMbox),
        destMsgId,
        destRevision
      ).sync();

      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (ZimbraException e)
    {
      throw ServiceException.FAILURE("zal", e);
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return future.getNow().toZimbra(MailboxBlob.class);
  }

  public boolean delete(Blob blob) throws IOException
  {
    Future<Boolean> future;
    try
    {
      future = mZALStoreManager.delete(
        BlobWrap.wrapZimbraObject(blob)
      ).sync();

      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }

    return future.getNow();
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

      return delete(vsb);
    }
    else
    {
      return false;
    }
  }

  public boolean delete(MailboxBlob mblob) throws IOException
  {
    Future<Boolean> future;
    try
    {
      future = mZALStoreManager.delete(
        BlobWrap.wrapZimbraObject(
          mblob
        )
      );

      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (Throwable t)
    {
      throw new IOException(t);
    }

    return future.getNow();
  }

  @Nullable
  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) throws ServiceException
  {
    return mZALStoreManager.getMailboxBlob(
      new org.openzal.zal.Mailbox(mbox),
      itemId,
      revision,
      locator
    ).toZimbra(MailboxBlob.class);
  }

  @Nullable
  public InputStream getContent(MailboxBlob mboxBlob) throws IOException
  {
    return getContent(mboxBlob.getLocalBlob());
  }

  public InputStream getContent(Blob blob) throws IOException
  {
    return mZALStoreManager.getContent(
      BlobWrap.wrapZimbraObject(
        blob
      )
    );
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
    Future<Boolean> future;
    try
    {
      future = mZALStoreManager.delete(mailbox, blobsCollection).sync();

      if (!future.isSuccess())
      {
        throw future.cause();
      }
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }

    return future.getNow();
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
