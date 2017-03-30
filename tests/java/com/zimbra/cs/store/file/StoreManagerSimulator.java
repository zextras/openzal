package com.zimbra.cs.store.file;

import com.zextras.lib.vfs.FileStreamWriter;
import com.zextras.lib.vfs.FileStreamWriterDigestCalculator;
import com.zextras.lib.vfs.OutputStreamFileWriterWrapper;
import com.zextras.lib.vfs.RelativePath;
import com.zextras.lib.vfs.VfsError;
import com.zextras.lib.vfs.ramvfs.RamFS;
import com.zextras.utils.TextUtils;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.BlobInputStream;
import com.zimbra.cs.store.FileDescriptorCache;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.zimbra.cs.volume.Volume;
import com.zimbra.cs.volume.VolumeManager;

import org.openzal.zal.*;

public final class StoreManagerSimulator extends StoreManager
{

  private final RamFS mStoreRoot;

  public RamFS getStoreRoot()
  {
    return mStoreRoot;
  }

  public StoreManagerSimulator()
  {
    mStoreRoot = new RamFS();
//        DebugConfig.disableMessageStoreFsync = true;
  }

  public void startup() throws IOException
  {
    BlobInputStream.setFileDescriptorCache(new FileDescriptorCache(null));
  }

  public void shutdown()
  {
    purge();
    BlobInputStream.setFileDescriptorCache(null);
  }

  public boolean supports(StoreFeature feature)
  {
    return false;
  }

  public void purge()
  {
    try
    {
      mStoreRoot.emptyRamFS();
    }
    catch (Exception ignore)
    {}
  }

  public BlobBuilder getBlobBuilder()
  {
    return new MockBlobBuilder();
  }

  public Blob storeIncoming(InputStream data, boolean storeAsIs) throws IOException
  {
    com.zextras.lib.vfs.File file = mStoreRoot.getRoot().resolveFile(
      UUID.randomUUID().toString()
    );
    MockBlob mockblob = createMockBlob();
    mockblob.setFile(file);

    OutputStream writer;
    MessageDigest messageDigest;
    try
    {
      writer = file.openOutputStreamWrapper();
      messageDigest = MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new RuntimeException(e);
    }
    catch (VfsError vfsError)
    {
      throw new RuntimeException(vfsError);
    }
    try
    {
      DigestOutputStream digestOutputStream = new DigestOutputStream(writer, messageDigest);
      int size = IOUtils.copy(data, digestOutputStream);
      mockblob.setDigest(Utils.encodeFSSafeBase64(messageDigest.digest()));
      mockblob.setRawSize(size);
    }
    finally
    {
      IOUtils.closeQuietly(writer);
    }

    return mockblob;
  }

  public StagedBlob stage(InputStream data, long actualSize, Mailbox mbox) throws IOException
  {
    return new MockStagedBlob(mbox,(MockBlob)storeIncoming(data,false));
  }

  public StagedBlob stage(Blob blob, Mailbox mbox)
  {
    return new MockStagedBlob(mbox,(MockBlob)blob);
  }

  private String getBlobDir(short volumeId, int mboxId, int itemId )
  {
    Volume vol;
    try
    {
      vol = VolumeManager.getInstance().getVolume(volumeId);
      return vol.getBlobDir(mboxId, itemId);
    }
    catch (ServiceException e)
    {
      throw new RuntimeException(e);
    }
  }

  public RelativePath getBlobPath(
    long mboxId,
    int itemId,
    int revision,
    short volumeId
  )
  {
    String path = getBlobDir(volumeId, (int)mboxId, itemId);

    int buflen = path.length() + 15 + (revision < 0 ? 0 : 11);
    StringBuilder sb = new StringBuilder(buflen);

    sb.append(path).append(File.separator).append(itemId);
    if( revision >= 0 ) {
      sb.append('-').append(revision);
    }
    sb.append(".msg");

    String finalPath = sb.toString();
    return new RelativePath(finalPath.substring(1,finalPath.length()));
  }

  public RelativePath getBlobPath(MailboxBlob mboxBlob)
  {
    if (mboxBlob instanceof MockVolumeMailboxBlob)
    {
      MockVolumeMailboxBlob blob = (MockVolumeMailboxBlob) mboxBlob;
      return getBlobPath(
        blob.getMailbox().getId(),
        blob.getItemId(),
        blob.getRevision(),
        blob.getLocalBlob().getVolumeId()
      );
    }
    MockMailboxBlob blob = (MockMailboxBlob)mboxBlob;
    return getBlobPath(
      blob.getMailbox().getId(),
      blob.getItemId(),
      blob.getRevision(),
      blob.volumeId()
    );
  }

  public short currentVolume()
  {
    return VolumeManager.getInstance().getCurrentMessageVolume().getId();
  }


  public MailboxBlob copy(
    MockBlob src,
    Mailbox destMbox,
    int destItemId,
    int destRevision,
    String locator
  )
    throws IOException
  {
    com.zextras.lib.vfs.File destinationFile = mStoreRoot.getRoot().resolveFile(
      getBlobPath(destMbox.getId(), destItemId, destRevision, Short.valueOf(locator))
    );

    try
    {
      if (!src.getVirtualFile().exists().syncAndGet())
      {
        throw new IOException();
      }
      destinationFile.getParent().createRecursive();
      src.getVirtualFile().copy(destinationFile).sync();
    }
    catch (VfsError e)
    {
      throw new RuntimeException(e);
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }

    MockBlob mockBlob;
    try
    {
      mockBlob = createMockBlob();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    mockBlob.setFile(destinationFile);

    MockStagedBlob mockStagedBlob = new MockStagedBlob(destMbox, mockBlob);

    return new MockMailboxBlob(
      destMbox,
      destItemId,
      destRevision,
      locator,
      mockStagedBlob
    );
  }

  public MailboxBlob copy(MailboxBlob srcOr, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException
  {
    return copy(
      ((MockMailboxBlob)srcOr).getMockStagedBlob().getMockBlob(),
      destMbox,
      destItemId,
      destRevision,
      srcOr.getLocator()
    );
  }

  public MailboxBlob link(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException
  {
    MailboxBlob newBlob = copy(
      ((MockStagedBlob) src).getMockBlob(),
      destMbox,
      destItemId,
      destRevision,
      String.valueOf(currentVolume())
    );
    return newBlob;
  }

  public MailboxBlob link(Blob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException, ServiceException
  {
    if (src instanceof MockVolumeBlob)
    {
      src = ((MockVolumeBlob) src).getMockBlob();
    }
    MailboxBlob newBlob = copy(
      (MockBlob) src,
      destMbox,
      destItemId,
      destRevision,
      String.valueOf(currentVolume())
    );
    return newBlob;
  }

  public MailboxBlob link(MailboxBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException, ServiceException
  {
    MailboxBlob newBlob = copy(
      (MockBlob) src.getLocalBlob(),
      destMbox,
      destItemId,
      destRevision,
      String.valueOf(currentVolume())
    );
    return newBlob;
  }

  public MailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException
  {
    MailboxBlob newBlob = copy(
      ((MockVolumeBlob)((MockVolumeStagedBlob) src).getLocalBlob()).getMockBlob(),
      destMbox,
      destItemId,
      destRevision,
      String.valueOf(currentVolume())
    );

    try
    {
      ((MockVolumeBlob)((MockVolumeStagedBlob) src).getLocalBlob()).getMockBlob().getVirtualFile().remove().syncAndGet();
    }
    catch (VfsError e)
    {
      throw new RuntimeException(e);
    }
    return newBlob;
  }

  public boolean delete(Blob blob) throws IOException
  {
    try
    {
      ((MockBlob)blob).getVirtualFile().remove().syncAndGet();
    }
    catch (VfsError e)
    {
      throw new IOException(e);
    }
    return true;
  }

  public boolean delete(StagedBlob staged)
  {
    try
    {
      ((MockStagedBlob)staged).getMockBlob().getVirtualFile().remove().syncAndGet();
    }
    catch (VfsError e)
    {
      throw new RuntimeException(e);
    }
    return true;
  }

  public boolean delete(MailboxBlob mblob) throws IOException {
    com.zextras.lib.vfs.File file = mStoreRoot.getRoot().resolveFile(
      getBlobPath(mblob)
    );
    try
    {
      file.remove().syncAndGet();
    }
    catch (VfsError vfsError)
    {
      throw vfsError.toIOException();
    }
    return true;
  }

  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator)
  {
    RelativePath path = getBlobPath(mbox.getId(), itemId, revision, Short.valueOf(locator));

    MockBlob mockBlob;
    try
    {
      mockBlob = createMockBlob();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    mockBlob.setFile(mStoreRoot.getRoot().resolveFile(path));

    MockStagedBlob mockStagedBlob = new MockStagedBlob(
      mbox,
      mockBlob
    );

    return new MockMailboxBlob(mbox, itemId, revision, locator, mockStagedBlob);
  }

  public MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator, boolean checkFileExistance) throws ServiceException
  {
    MailboxBlob mailboxBlob = getMailboxBlob(mbox,itemId,revision,locator);

    InputStream inputStream = null;
    try
    {
      inputStream = mailboxBlob.getLocalBlob().getInputStream();
    }
    catch (IOException ex)
    {
      return null;
    }
    finally
    {
      if( inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (IOException ignore){}
      }
    }
    return mailboxBlob;
  }

  public InputStream getContent(MailboxBlob mblob) throws IOException {
    return mblob.getLocalBlob().getInputStream();
  }

  public InputStream getContent(Blob blob) throws IOException {
    return blob.getInputStream();
  }

  public boolean deleteStore(Mailbox mbox) throws IOException
  {
    try
    {
      mStoreRoot.getRoot().removeContent().syncAndGet();
      return true;
    }
    catch (VfsError vfsError)
    {
      throw new IOException(vfsError);
    }
  }

  public boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> mblobs) throws IOException
  {
    throw new UnsupportedOperationException();
  }
  static File sNonExistingPath = new File("/tmp/i/dont/exist");

  public static class MockBlob extends Blob
  {
    private com.zextras.lib.vfs.File mFile;

    public MockBlob(File tempFile) throws IOException
    {
      super(tempFile);
    }

    public void setFile(com.zextras.lib.vfs.File file)
    {
      mFile = file;
    }

    @Override
    public File getFile()
    {
      try
      {
        File tmpFile = super.getFile();
        InputStream inputStream = mFile.openInputStreamWrapper();
        OutputStream outputStream = new FileOutputStream(tmpFile);
        try
        {
          IOUtils.copy(inputStream, outputStream);
        }
        finally
        {
          outputStream.close();
          inputStream.close();
        }
        return tmpFile;
      }
      catch (Exception ex)
      {
        return sNonExistingPath;
      }
    }

    public com.zextras.lib.vfs.File getVirtualFile()
    {
      return mFile;
    }

    public InputStream getInputStream() throws IOException
    {
      try
      {
        return mFile.openInputStreamWrapper();
      }
      catch (VfsError error)
      {
        throw new IOException(error);
      }
    }

    public long getRawSize()
    {
      try
      {
        return mFile.size().syncAndGet();
      }
      catch (VfsError vfsError)
      {
        throw new RuntimeException(vfsError);
      }
    }

    public static MockBlob getMockBlob(Blob src)
    {
      return (MockBlob) src;
    }
  }

  public static class MockStagedBlob extends StagedBlob
  {
    public MockBlob getMockBlob()
    {
      return mMockBlob;
    }

    private final MockBlob mMockBlob;

    public MockStagedBlob(Mailbox mbox, MockBlob mockBlob)
    {
      super(mbox, "xxx", 123);
      mMockBlob = mockBlob;
    }

    public String getStagedLocator()
    {
      return getLocator();
    }

    public String getLocator()
    {
      return "1";
    }
  }

  @SuppressWarnings("serial")
  public static class MockMailboxBlob extends MailboxBlob
  {
    public MockStagedBlob getMockStagedBlob()
    {
      return mMockStagedBlob;
    }

    private final MockStagedBlob mMockStagedBlob;

    public MockMailboxBlob(Mailbox mbox, int itemId, int revision, String locator, MockStagedBlob mockStagedBlob)
    {
      super(mbox, itemId, revision, locator);
      mMockStagedBlob = mockStagedBlob;
    }

    public Blob getLocalBlob() throws IOException
    {
      return new ZalMailboxBlob(
        BlobWrap.wrapZimbraBlob(mMockStagedBlob.getMockBlob()),
        new org.openzal.zal.Mailbox(mMockStagedBlob.getMailbox()),
        getItemId(),
        getRevision()
      )
      {
        @Override
        public org.openzal.zal.Blob getLocalBlob()
        {
          return getLocalBlob(false);
        }

        @Override
        public String getVolumeId()
        {
          return "1";
        }
      }.toZimbra(Blob.class);
    }

    public short volumeId()
    {
      return Short.valueOf(getLocator());
    }
  }

  private static MockBlob createMockBlob()
  {
    try
    {
      File tmpFile = File.createTempFile("fakestore",".tmp");
      tmpFile.deleteOnExit();
      return new MockBlob(tmpFile);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static class MockVolumeMailboxBlob extends VolumeMailboxBlob
  {
    public MockVolumeMailboxBlob(MailboxBlob blob, String volumeId) throws IOException
    {
      super(blob.getMailbox(), blob.getItemId(), blob.getRevision(), blob.getLocator(), new MockVolumeBlob(blob.getLocalBlob(), volumeId));
    }
  }

  public static class MockVolumeBlob extends VolumeBlob
  {
    private final String mVolumeId;
    private final MockBlob mMockBlob;

    MockVolumeBlob(Blob blob, String volumeId)
    {
      super(blob.getFile(), Short.parseShort(volumeId));
      if (blob instanceof MockBlob)
      {
        mMockBlob = (MockBlob) blob;
      }
      else if (blob instanceof MockVolumeBlob)
      {
        mMockBlob = (MockBlob) blob;
      }
      else
      {
        mMockBlob = (MockBlob) ((BlobWrap)((ZalMailboxBlob)(new InternalOverrideBlobProxy(blob).getWrappedObject())).getLocalBlob(false)).getWrappedObject();
      }
      mVolumeId = volumeId;
    }

    public String getDigest() throws IOException
    {
      return mMockBlob.getDigest();
    }

    public MockBlob getMockBlob()
    {
      return mMockBlob;
    }

    public short getVolumeId()
    {
      return Short.parseShort(mVolumeId);
    }
  }

  public static class MockVolumeStagedBlob extends VolumeStagedBlob
  {
    public MockVolumeStagedBlob(Mailbox mbox, MockBlob blob, String volumeId) throws IOException
    {
      super(mbox, new MockVolumeBlob(blob, volumeId));
    }

    public String getDigest()
    {
      try
      {
        return getLocalBlob().getDigest();
      }
      catch (Exception e)
      {
        return "";
      }
    }
  }

  private class MockBlobBuilder extends BlobBuilder
  {
    private ByteArrayOutputStream out;

    protected MockBlobBuilder()
    {
      super(createMockBlob());
    }

    protected OutputStream createOutputStream(File file) throws FileNotFoundException
    {
      assert out == null : "Output stream already created";
      out = new ByteArrayOutputStream();
      return out;
    }

    protected FileChannel getFileChannel()
    {
      return null;
    }

    public Blob finish() throws IOException, ServiceException
    {
      MockBlob mockblob = (MockBlob) super.finish();

      if (out != null)
      {
        byte content[] = out.toByteArray();
        com.zextras.lib.vfs.File file = mStoreRoot.getRoot().resolveFile(
          UUID.randomUUID().toString()
        );
        mockblob.setFile(file);
        FileStreamWriter writer = null;
        try
        {
          writer = file.openWriterStream();
        }
        catch (VfsError missingWritePermissions)
        {
          throw new IOException(missingWritePermissions);
        }
        try
        {
          writer.write(Unpooled.wrappedBuffer(content));
        }
        finally
        {
          writer.close();
        }
        out = null;
      }

      return mockblob;
    }
  }
}