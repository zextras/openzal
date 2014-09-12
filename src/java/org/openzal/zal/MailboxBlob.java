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

import java.io.IOException;


public class MailboxBlob
{
  @NotNull private final com.zimbra.cs.store.MailboxBlob mMailboxBlob;

  protected MailboxBlob(@NotNull com.zimbra.cs.store.MailboxBlob mailboxBlob)
  {
    if (mailboxBlob == null)
    {
      throw new NullPointerException();
    }
    mMailboxBlob = mailboxBlob;
  }

  public String getDigest()
    throws IOException
  {
    return mMailboxBlob.getDigest();
  }

  public Blob getLocalBlob()
    throws IOException
  {
    return new Blob(mMailboxBlob.getLocalBlob());
  }

  public int getRevision()
  {
    return mMailboxBlob.getRevision();
  }

  public String toString()
  {
    return mMailboxBlob.toString();
  }

  public int getItemId()
  {
    return mMailboxBlob.getItemId();
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mMailboxBlob);
  }
}
