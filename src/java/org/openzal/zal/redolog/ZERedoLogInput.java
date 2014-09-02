/*
 * ZAL - An abstraction layer for Zimbra.
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

package org.openzal.zal.redolog;


import com.zimbra.cs.redolog.RedoLogInput;

import java.io.IOException;
import java.io.RandomAccessFile;


public class ZERedoLogInput
{
  private final RedoLogInput mRedoLogInput;

  public ZERedoLogInput(RandomAccessFile fileHandler, String currentRedoPath)
  {
    mRedoLogInput = new RedoLogInput(fileHandler, currentRedoPath);
  }

  public void readFully(byte[] byte1, int int1, int int2)
    throws IOException
  {
    mRedoLogInput.readFully(byte1, int1, int2);
  }

  public boolean readBoolean()
    throws IOException
  {
    return mRedoLogInput.readBoolean();
  }

  public String[] readUTFArray()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mRedoLogInput.readUTFArray();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public long readLong()
    throws IOException
  {
    return mRedoLogInput.readLong();
  }

  public long getFilePointer()
    throws IOException
  {
    return mRedoLogInput.getFilePointer();
  }

  public int skipBytes(int int1)
    throws IOException
  {
    return mRedoLogInput.skipBytes(int1);
  }

  public int readInt()
    throws IOException
  {
    return mRedoLogInput.readInt();
  }

  public short readShort()
    throws IOException
  {
    return mRedoLogInput.readShort();
  }

  public byte readByte()
    throws IOException
  {
    return mRedoLogInput.readByte();
  }

  public String readUTF()
    throws IOException
  {
    return mRedoLogInput.readUTF();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mRedoLogInput);
  }
}
