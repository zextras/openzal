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

import org.openzal.zal.exceptions.ZimbraException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface BlobBuilder
{
  BlobBuilder setSizeHint(long size);
  long getSizeHint();
  long getTotalBytes();
  BlobBuilder disableCompression(boolean disable);
  BlobBuilder disableDigest(boolean disable);
  BlobBuilder init() throws IOException, ZimbraException;
  BlobBuilder append(InputStream in) throws IOException;
  BlobBuilder append(byte[] b, int off, int len) throws IOException;
  BlobBuilder append(byte[] b) throws IOException;
  BlobBuilder append(ByteBuffer bb) throws IOException;
  boolean isFinished();
  void dispose();
  Blob finish() throws IOException, ZimbraException;
  Blob getBlob();
  <T> T toZimbra(Class<T> cls);
}
