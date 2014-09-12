/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.store.file.FileBlobStore;

/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.volume.Volume;
import com.zimbra.cs.volume.VolumeManager;
/* $else$
import com.zimbra.cs.store.*;
import com.zimbra.cs.store.file.*;
 $endif$ */

import java.io.File;
import java.io.IOException;

public class StoreManagerImp implements StoreManager
{
  protected final com.zimbra.cs.store.StoreManager sm;

  public StoreManagerImp()
  {
    this.sm = com.zimbra.cs.store.StoreManager.getInstance();
  }

  public StoreManagerImp(Object storeManager)
  {
    this.sm = (com.zimbra.cs.store.StoreManager) storeManager;
  }

  @NotNull
  private FileBlobStore getFileBlobStore()
  {
    return (FileBlobStore) sm;
  }

  @NotNull
  public MailboxBlob getMailboxBlob(@NotNull Mailbox mbox, int msgId, int revision, String locator)
    throws ZimbraException
  {
    try
    {
      return new MailboxBlob(
        sm.getMailboxBlob(mbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
                          msgId,
                          revision,
                          locator)
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public MailboxBlob copy(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision, short destVolumeId)
    throws IOException, ZimbraException
  {
    try
    {
      return new MailboxBlob(
        getFileBlobStore().copy(
          src.toZimbra(com.zimbra.cs.store.Blob.class),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          destVolumeId
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public MailboxBlob link(@NotNull Blob src, @NotNull Mailbox destMbox, int destMsgId, int destRevision, short destVolumeId)
    throws IOException, ZimbraException
  {
    try
    {
      return new MailboxBlob(
        getFileBlobStore().link(
          src.toZimbra(com.zimbra.cs.store.Blob.class),
          destMbox.toZimbra(com.zimbra.cs.mailbox.Mailbox.class),
          destMsgId,
          destRevision,
          destVolumeId
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean delete(@NotNull Blob blob) throws IOException
  {
    return sm.delete(blob.toZimbra(com.zimbra.cs.store.Blob.class));
  }

  public boolean delete(@NotNull MailboxBlob mblob) throws IOException
  {
    return sm.delete(mblob.toZimbra(com.zimbra.cs.store.MailboxBlob.class));
  }

  /* $if ZimbraVersion >= 8.0.0 $*/
  private String getBlobDir(short volumeId, int mboxId, int itemId )
  {
    Volume vol;
    try
    {
      vol = VolumeManager.getInstance().getVolume(volumeId);
      return vol.getBlobDir(mboxId, itemId);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw new RuntimeException(e);
    }
  }
/* $else$

  private String getBlobDir(short volumeId, int mboxId, int itemId )
  {
    try
    {
      Volume vol = Volume.getById(volumeId);
      return vol.getBlobDir(mboxId, itemId);
    }
    catch(com.zimbra.common.service.ServiceException e)
    {
      throw new RuntimeException(e);
    }
  }

  $endif$ */


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
}
