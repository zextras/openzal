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

public class FullItemInfo extends PlacedItemInfo
{
  public final byte   type;
  public final int    parentId;
  public final int    indexId;
  public final int    imapId;
  public final long   size;
  public final String locator;
  public final String blobDigest;
  public final int    unreadCount;
  public final int    flags;
  public final String tags;
  public final String subject;
  public final String name;
  public final String metadata;
  public final int    modContent;
  public final long   dateChanged;
  public final String uuid;
  public final int    mailboxId;

  public FullItemInfo(int itemId, byte type, int parent_id, int folder_id, int index_id, int imap_id, long date, long size, String locator, String blob_digest, int unread_count, int flags, String tags, String subject, String name, String metadata, int mod_metadata, int mod_content, long dateChanged, String uuid, int mailbox_id)
  {
    super(folder_id, itemId, mod_metadata, date);
    this.type = type;
    this.parentId = parent_id;
    this.indexId = index_id;
    this.imapId = imap_id;
    this.size = size;
    this.locator = locator;
    this.blobDigest = blob_digest;
    this.unreadCount = unread_count;
    this.flags = flags;
    this.tags = tags;
    this.subject = subject;
    this.name = name;
    this.metadata = metadata;
    this.modContent = mod_content;
    this.dateChanged = dateChanged;
    this.uuid = uuid;
    this.mailboxId = mailbox_id;
  }

  @Override
  public boolean equals(@Nullable Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    FullItemInfo that = (FullItemInfo) o;

    if (dateChanged != that.dateChanged)
    {
      return false;
    }
    if (flags != that.flags)
    {
      return false;
    }
    if (imapId != that.imapId)
    {
      return false;
    }
    if (indexId != that.indexId)
    {
      return false;
    }
    if (modContent != that.modContent)
    {
      return false;
    }
    if (parentId != that.parentId)
    {
      return false;
    }
    if (size != that.size)
    {
      return false;
    }
    if (type != that.type)
    {
      return false;
    }
    if (unreadCount != that.unreadCount)
    {
      return false;
    }
    if (blobDigest != null ? !blobDigest.equals(that.blobDigest) : that.blobDigest != null)
    {
      return false;
    }
    if (locator != null ? !locator.equals(that.locator) : that.locator != null)
    {
      return false;
    }
    if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null)
    {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null)
    {
      return false;
    }
    if (subject != null ? !subject.equals(that.subject) : that.subject != null)
    {
      return false;
    }
    if (tags != null ? !tags.equals(that.tags) : that.tags != null)
    {
      return false;
    }
    if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (int) type;
    result = 31 * result + parentId;
    result = 31 * result + indexId;
    result = 31 * result + imapId;
    result = 31 * result + (int) (size ^ (size >>> 32));
    result = 31 * result + (locator != null ? locator.hashCode() : 0);
    result = 31 * result + (blobDigest != null ? blobDigest.hashCode() : 0);
    result = 31 * result + unreadCount;
    result = 31 * result + flags;
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    result = 31 * result + (subject != null ? subject.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
    result = 31 * result + modContent;
    result = 31 * result + (int) (dateChanged ^ (dateChanged >>> 32));
    result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
    return result;
  }
}
