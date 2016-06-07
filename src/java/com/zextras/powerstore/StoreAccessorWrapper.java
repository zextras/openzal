package com.zextras.powerstore;

import com.zextras.lib.Future;
import com.zextras.lib.FutureListener;
import com.zextras.lib.vfs.File;
import com.zextras.lib.vfs.VfsError;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.Blob;
import org.openzal.zal.Mailbox;
import org.openzal.zal.MailboxBlob;
import org.openzal.zal.PrimaryStore;
import org.openzal.zal.Store;
import org.openzal.zal.StoreFeature;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class StoreAccessorWrapper implements Store
{
  private final LocalStoreCache mLocalStoreCache;
  private final StoreAccessor mStoreAccessor;

  public StoreAccessorWrapper(
    LocalStoreCache localStoreCache,
    StoreAccessor storeAccessor
  )
  {
    mLocalStoreCache = localStoreCache;
    mStoreAccessor = storeAccessor;
  }

  @Override
  public MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    Future<MailboxBlob, VfsError> future = mStoreAccessor.copy(mLocalStoreCache.get(src), destMbox.getId(), destMsgId, destRevision);

    future.awaitUninterruptibly();
    if (!future.isSuccess())
    {
      throw future.cause().toIOException();
    }

    return future.getNow();
  }

  @Override
  public MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException
  {
    return copy(src, destMbox, destMsgId, destRevision);
  }

  @Override
  public boolean delete(Blob blob) throws IOException
  {
    return mStoreAccessor.delete(blob.getDigest());
  }

  @Override
  public void startup() throws IOException, ZimbraException
  {}

  @Override
  public void shutdown()
  {}

  @Override
  public boolean supports(StoreFeature feature)
  {
    return false;
  }

  @Override
  public InputStream getContent(Blob blob) throws IOException
  {
    Future<File, VfsError> future = mStoreAccessor.getVfsFile(blob.getDigest()).awaitUninterruptibly();
    if (!future.isSuccess())
    {
      throw future.cause().toIOException();
    }

    try
    {
      return future.getNow().openInputStreamWrapper();
    }
    catch (VfsError vfsError)
    {
      throw vfsError.toIOException();
    }
  }

  @Override
  public MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision) throws IOException
  {
    Future<File, VfsError> future = mStoreAccessor.getVfsFile(mbox.getId(), msgId, revision).awaitUninterruptibly();
    if (!future.isSuccess())
    {
      throw future.cause().toIOException();
    }


  }

  @Override
  public boolean delete(Mailbox mailbox, @Nullable Iterable blobs) throws IOException, ZimbraException
  {
    return false;
  }

  @Override
  public PrimaryStore toPrimaryStore()
  {
    return null;
  }

  @Override
  public String getVolumeId()
  {
    return null;
  }

  @Override
  public String getBlobPath(int mboxId, int itemId, int modContent)
  {
    return null;
  }
}
