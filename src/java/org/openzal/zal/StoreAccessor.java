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

import io.netty.util.concurrent.Future;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;

public interface StoreAccessor
{
  //boolean hasVolume(short volumeId);
  MailboxBlob getMailboxBlob(Mailbox mbox, int msgId, int revision, String locator);
  Future<MailboxBlob> copy(Blob src, Mailbox destMbox, int destMsgId, int destRevision, String volumeId);
  Future<MailboxBlob> link(Blob src, Mailbox destMbox, int destMsgId, int destRevision, String volumeId);
  String getBlobPath(int mboxId, int itemId, int revision, String volumeId);
  Future<Boolean> delete(Blob blob);
  void startup() throws IOException, ZimbraException;
  void shutdown();
  boolean supports(StoreFeature feature);
  @Nullable InputStream getContent(Blob blob) throws IOException;
  Future<Boolean> delete(Mailbox mailbox, @Nullable Iterable blobs, String volumeId);
}
