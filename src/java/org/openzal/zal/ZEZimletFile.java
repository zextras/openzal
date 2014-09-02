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

package org.openzal.zal;

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.cs.zimlet.ZimletException;
import com.zimbra.cs.zimlet.ZimletFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ZEZimletFile
{
  private final ZimletFile mZimletFile;

  protected ZEZimletFile(@NotNull Object zimletFile)
  {
    if ( zimletFile == null )
    {
      throw new NullPointerException();
    }
    mZimletFile = (ZimletFile)zimletFile;
  }

  public ZEZimletFile(String zimlet) throws IOException
  {
    try
    {
      mZimletFile = new ZimletFile(zimlet);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEZimletFile(String name, InputStream is) throws IOException
  {
    try
    {
      mZimletFile = new ZimletFile(name, is);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEZimletDescription getZimletDescription() throws IOException
  {
    try
    {
      return new ZEZimletDescription(mZimletFile.getZimletDescription());
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
}
