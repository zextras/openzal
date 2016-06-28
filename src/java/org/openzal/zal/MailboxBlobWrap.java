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


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class MailboxBlobWrap implements MailboxBlob
{
  @NotNull private final com.zimbra.cs.store.MailboxBlob mMailboxBlob;

  public MailboxBlobWrap(@NotNull Object mailboxBlob)
  {
    if (mailboxBlob == null)
    {
      throw new NullPointerException();
    }
    mMailboxBlob = (com.zimbra.cs.store.MailboxBlob) mailboxBlob;
  }

  @NotNull
  public Object getWrappedObject()
  {
    return mMailboxBlob;
  }

  @Override
  public String getDigest()
  {
    try
    {
      return mMailboxBlob.getDigest();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public long getSize()
  {
    return 0;
  }

  @Override
  public InputStream getInputStream() throws IOException
  {
    return null;
  }

  @Override
  public Blob getLocalBlob()
  {
    try
    {
      return BlobWrap.wrapZimbraBlob(mMailboxBlob.getLocalBlob(), mMailboxBlob.getLocator());
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Mailbox getMailbox()
  {
    return new Mailbox(mMailboxBlob.getMailbox());
  }

  @Override
  public int getRevision()
  {
    return mMailboxBlob.getRevision();
  }

  @Override
  public String toString()
  {
    return mMailboxBlob.toString();
  }

  @Override
  public int getItemId()
  {
    return mMailboxBlob.getItemId();
  }

  @Override
  public String getVolumeId()
  {
    return mMailboxBlob.getLocator();
  }

  @Override
  public void renameTo(String newPath) throws IOException
  {
    mMailboxBlob.getLocalBlob().renameTo(newPath);
  }

  @Override
  public String getKey()
  {
    try
    {
      return mMailboxBlob.getLocalBlob().getPath();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public File getFile()
  {
    try
    {
      return mMailboxBlob.getLocalBlob().getFile();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mMailboxBlob);
  }

  public static MailboxBlob wrapZimbraObject(Object mailboxBlob)
  {
    if (mailboxBlob instanceof InternalOverrideMailboxBlob)
    {
      return ((InternalOverrideMailboxBlob) mailboxBlob).getWrappedMailboxBlob();
    }

    return new MailboxBlobWrap(mailboxBlob);
  }
}
