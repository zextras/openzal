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


import com.zimbra.cs.store.file.BlobWrap;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


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

  @Override
  public String getDigest()
    throws IOException
  {
    return mMailboxBlob.getDigest();
  }

  @Override
  public Blob getLocalBlob()
    throws IOException
  {
    return BlobWrap.wrap(mMailboxBlob.getLocalBlob(), Short.parseShort(mMailboxBlob.getLocator()));
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

  //@Override
  public short getVolumeId()
  {
    return Short.valueOf(mMailboxBlob.getLocator());
  }

  //@Override
  public long getSize() throws IOException
  {
    return mMailboxBlob.getSize();
  }

  @Override
  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mMailboxBlob);
  }

  public static MailboxBlob wrap(Object mailboxBlob)
  {
    if (mailboxBlob instanceof InternalOverrideMailboxBlob)
    {
      return ((InternalOverrideMailboxBlob) mailboxBlob).getWrappedMailboxBlob();
    }

    return new MailboxBlobWrap(mailboxBlob);
  }
}
