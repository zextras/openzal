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

import java.io.File;
import java.io.IOException;

public class Blob
{
  private final com.zimbra.cs.store.Blob mBlob;

  protected Blob(@NotNull Object blob)
  {
    if ( blob == null )
    {
      throw new NullPointerException();
    }
    mBlob = (com.zimbra.cs.store.Blob)blob;
  }

  public File getFile()
  {
    return mBlob.getFile();
  }

  public void renameTo(String newPath) throws IOException
  {
    mBlob.renameTo(newPath);
  }

  public String getPath()
  {
    return mBlob.getPath();
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mBlob);
  }
}
