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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Blob
{
  void renameTo(String newPath) throws IOException;
  String getKey();
  File getFile();
  <T> T toZimbra(Class<T> cls);
  String getDigest() throws IOException;
  long getSize() throws IOException;
  String getVolumeId();
  InputStream getInputStream() throws IOException;
  boolean hasMailboxInfo();
  MailboxBlob toMailboxBlob();
  Blob setDigest(String digest);
  Blob setSize(long size);
  long getStoredFileSize() throws IOException;
  boolean isCompressed() throws IOException;
}

