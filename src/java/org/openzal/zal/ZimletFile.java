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

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.cs.zimlet.ZimletException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;

public class ZimletFile
{
  @NotNull private final com.zimbra.cs.zimlet.ZimletFile mZimletFile;

  protected ZimletFile(@NotNull Object zimletFile)
  {
    if (zimletFile == null)
    {
      throw new NullPointerException();
    }
    mZimletFile = (com.zimbra.cs.zimlet.ZimletFile) zimletFile;
  }

  public ZimletFile(String zimlet) throws IOException
  {
    try
    {
      mZimletFile = new com.zimbra.cs.zimlet.ZimletFile(zimlet);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimletFile(String name, InputStream is) throws IOException
  {
    try
    {
      mZimletFile = new com.zimbra.cs.zimlet.ZimletFile(name, is);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimletDescription getZimletDescription() throws IOException
  {
    try
    {
      return new ZimletDescription(mZimletFile.getZimletDescription());
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getZimletPath()
  {
    return mZimletFile.getFile().getAbsolutePath();
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZimletFile);
  }

  public String getName()
  {

    return mZimletFile.getName();
  }

  public byte[] getZimletContent()
  {
    return mZimletFile.toByteArray();
  }

  @Nullable
  public InputStream getContentStream(String name) throws IOException
  {
    com.zimbra.cs.zimlet.ZimletFile.ZimletEntry entry = mZimletFile.getEntry(name);
    if (entry == null)
    {
      return null;
    }
    return entry.getContentStream();
  }

  @Nullable
  public byte[] getContent(String name) throws IOException
  {
    com.zimbra.cs.zimlet.ZimletFile.ZimletEntry entry = mZimletFile.getEntry(name);
    if (entry == null)
    {
      return null;
    }
    return entry.getContents();
  }
}
