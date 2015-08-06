package com.zimbra.cs.store.file;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.BlobBuilder;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StagedBlob;
import com.zimbra.cs.store.StoreManager;
/* $if ZimbraVersion < 8.0.0 $
import com.zimbra.cs.store.StorageCallback;
/* $endif $ */

import java.io.IOException;
import java.io.InputStream;

public interface FileBlobStoreWrap
{
    void startup() throws IOException, ServiceException;

    void shutdown();

    /* $if ZimbraVersion >= 7.2.0 $ */
    boolean supports(StoreManager.StoreFeature feature);
    /* $endif $ */

    BlobBuilder getBlobBuilder() throws IOException, ServiceException;

    /* $if ZimbraVersion >= 8.0.0 $ */
    Blob storeIncoming(InputStream in, boolean storeAsIs)
    /* $elseif ZimbraVersion >= 7.0.0 $
    Blob storeIncoming(InputStream in, StorageCallback callback, boolean storeAsIs)
    /* $else $
    Blob storeIncoming(InputStream in, long sizeHint, StorageCallback callback, boolean storeAsIs)
    /* $endif $ */
    throws IOException, ServiceException;

    /* $if ZimbraVersion >= 8.0.0 $ */
    VolumeStagedBlob stage(InputStream in, long actualSize, Mailbox mbox)
    /* $else $
    VolumeStagedBlob stage(InputStream in, long actualSize, StorageCallback callback, Mailbox mbox)
    /* $endif $ */
    throws IOException, ServiceException;

    VolumeStagedBlob stage(Blob blob, Mailbox mbox) throws IOException;

    VolumeMailboxBlob copy(MailboxBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException, ServiceException;

    VolumeMailboxBlob copy(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId)
    throws IOException, ServiceException;

    VolumeMailboxBlob link(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException, ServiceException;

    VolumeMailboxBlob link(Blob src, Mailbox destMbox, int destItemId, int destRevision, short destVolumeId)
    throws IOException, ServiceException;

    VolumeMailboxBlob renameTo(StagedBlob src, Mailbox destMbox, int destItemId, int destRevision)
    throws IOException, ServiceException;

    boolean delete(MailboxBlob mblob) throws IOException;

    boolean delete(StagedBlob staged) throws IOException;

    boolean delete(Blob blob) throws IOException;

    MailboxBlob getMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) throws ServiceException;

    InputStream getContent(MailboxBlob mboxBlob) throws IOException;

    InputStream getContent(Blob blob) throws IOException;

    /* $if ZimbraVersion >= 7.2.1 $ */
    boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob.MailboxBlobInfo> blobs) throws IOException, ServiceException;
    /* $elseif ZimbraVersion >= 7.2.0 $
    boolean deleteStore(Mailbox mbox, Iterable<MailboxBlob> blobs) throws IOException, ServiceException;
    /* $else $
    boolean deleteStore(Mailbox mbox) throws IOException, ServiceException;
    /* $endif $ */
}
