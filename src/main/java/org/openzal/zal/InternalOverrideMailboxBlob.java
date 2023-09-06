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

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.MailboxBlob;

import java.io.IOException;

class InternalOverrideMailboxBlob extends com.zimbra.cs.store.MailboxBlob
{
  private final org.openzal.zal.MailboxBlob mZalMailboxBlob;

  public InternalOverrideMailboxBlob(org.openzal.zal.MailboxBlob zalMailboxBlob)
  {
    super(null, 0, 0, null);
    mZalMailboxBlob = zalMailboxBlob;
  }

  @Override
  public int getItemId()
  {
    return mZalMailboxBlob.getItemId();
  }

  @Override
  public int getRevision()
  {
    return mZalMailboxBlob.getRevision();
  }

  @Override
  public String getLocator()
  {
    return mZalMailboxBlob.getVolumeId();
  }

  @Override
  public String getDigest() throws IOException
  {
    return mZalMailboxBlob.getDigest();
  }

  @Override
  public MailboxBlob setDigest(String digest)
  {
    mZalMailboxBlob.setDigest(digest);
    return mZalMailboxBlob.toZimbra(MailboxBlob.class);
  }

  @Override
  public long getSize() throws IOException
  {
    return mZalMailboxBlob.getSize();
  }

  @Override
  public MailboxBlob setSize(long size)
  {
    mZalMailboxBlob.setSize(size);
    return mZalMailboxBlob.toZimbra(MailboxBlob.class);
  }

  @Override
  public Mailbox getMailbox()
  {
    return mZalMailboxBlob.getMailbox().toZimbra(Mailbox.class);
  }

  @Override
  public Blob getLocalBlob() throws IOException
  {
    return mZalMailboxBlob.getLocalBlob().toZimbra(Blob.class);
  }

  @Override
  public String toString()
  {
    return mZalMailboxBlob.toString();
  }

  public org.openzal.zal.MailboxBlob getWrappedMailboxBlob()
  {
    return mZalMailboxBlob;
  }

  public static Object wrap(org.openzal.zal.MailboxBlob mailboxBlob)
  {
    return new InternalOverrideMailboxBlob(mailboxBlob);
  }
}
