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

/* $if ZimbraVersion >= 8.0.0 $ */
/* $endif $ */
import org.jetbrains.annotations.NotNull;

public class Link extends Document
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  @NotNull private final com.zimbra.cs.mailbox.Link mLink;

  public Link(@NotNull Object item)
  {
    super(item);
    mLink = (com.zimbra.cs.mailbox.Link) item;
  }

  public int getRemoteId()
  {
    return mLink.getRemoteId();
  }

  public String getOwnerId()
  {
    return mLink.getOwnerId();
  }
/* $else$
  public Link(@NotNull Object item)
  {
    super(item);
    throw new UnsupportedOperationException("Link MailItem doesn't exist in zimbra < 8.0.0");
  }

  public int getRemoteId()
  {
    throw new UnsupportedOperationException("Link MailItem doesn't exist in zimbra < 8.0.0");
  }

  public String getOwnerId()
  {
    throw new UnsupportedOperationException("Link MailItem doesn't exist in zimbra < 8.0.0");
  }
 $endif $ */
}
