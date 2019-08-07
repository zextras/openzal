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

import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;

public interface Store
{
  MailboxBlob copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException;
  MailboxBlob link(Blob src, Mailbox destMbox, int destMsgId, int destRevision) throws IOException;
  boolean delete(MailboxBlob blob) throws IOException;
  boolean delete(StagedBlob blob) throws IOException;
  void startup() throws IOException, ZimbraException;
  void shutdown();
  boolean supports(StoreFeature feature);
  InputStream getContent(MailboxBlob blob) throws IOException;
  MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision) throws IOException;
  boolean delete(Mailbox mailbox, @Nullable Iterable blobs) throws IOException, ZimbraException;
  PrimaryStore toPrimaryStore();
  boolean canBePrimary();
  String getVolumeId();
  String getVolumeName();
  String getBlobPath(MailboxData mbox, int itemId, int modContent);
  String getMailboxDirPath(MailboxData mbox);
  String getMailboxDirPath(MailboxData mbox, short type);
  String getRootPath();
  boolean isCompressed();
  long getCompressionThreshold();
}
