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

import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.ZimbraException;

public interface StoreManager
{
  @Nullable
  MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision, String locator)
    throws ZimbraException;

  MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, short destVolumeId)
    throws IOException, ZimbraException;

  MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision, short destVolumeId)
    throws IOException, ZimbraException;

  String getBlobPath(int mboxId, int itemId, int revision,
                     short volumeId) throws org.openzal.zal.exceptions.ZimbraException;

  boolean delete(Blob blob) throws IOException;

  boolean delete(MailboxBlob mblob) throws IOException;
}
