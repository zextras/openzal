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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface StoreManager extends PrimaryStoreAccessor
{
  void registerStoreAccessor(StoreAccessorFactory storeAccessorFactory, String volumeId);
  void invalidateStoreAccessor(Collection<String> volumes);
  @Nullable InputStream getContent(Blob blob, String volumeId) throws IOException;
  Future<Boolean> delete(StagedBlob blob);
  Future<Boolean> delete(MailboxBlob blob);
  Future<Boolean> delete(Mailbox mailbox, @Nullable Iterable blobs) throws IOException;
}
