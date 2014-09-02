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

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Note;
import org.jetbrains.annotations.NotNull;

public class ZENote extends ZEItem
{
  private final Note mNote;

  class ZERectangle
  {
    final Note.Rectangle mRectangle;

    public ZERectangle(@NotNull Note.Rectangle rectangle)
    {
      mRectangle = rectangle;
    }

    public Note.Rectangle getZimbraRectangle()
    {
      return mRectangle;
    }
  }

  public ZENote(@NotNull Object item)
  {
    super(item);
    mNote = (Note) item;
  }

  public String getSender()
  {
    return mNote.getSender();
  }

  public String getText()
  {
    return mNote.getText();
  }

  public String getName()
  {
    return mNote.getName();
  }

  public ZERectangle getBounds()
  {
    return new ZERectangle(mNote.getBounds());
  }

}
