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

/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.mailbox.Comment;
/* $endif $ */
import com.zimbra.cs.mailbox.MailItem;
import org.jetbrains.annotations.NotNull;

public class ZEComment extends ZEItem
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final Comment mComment;

  public ZEComment(@NotNull Object item)
  {
    super(item);
    mComment = (Comment)item;
  }

  public String getText()
  {
    return mComment.getText();
  }

  public String getCreatorAccountId()
  {
    return mComment.getCreatorAccountId();
  }
/* $else$
  public ZEComment(@NotNull Object item)
  {
    super(item);
    throw new UnsupportedOperationException();
  }

  public String getText()
  {
    throw new UnsupportedOperationException();
  }

  public String getCreatorAccountId()
  {
    throw new UnsupportedOperationException();
  }
 $endif $ */
}
