/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

public class Document extends Item
{
  public Document(@NotNull Object item)
  {
    super(item);
  }

  public Document(@NotNull Item item)
  {
    super(item);
  }

  public String getContentType()
  {
    return ((com.zimbra.cs.mailbox.Document)mMailItem).getContentType();
  }

  public String getCreator()
  {
    return ((com.zimbra.cs.mailbox.Document)mMailItem).getCreator();
  }

  public String getDescription()
  {
/* $if MajorZimbraVersion >= 7 $ */
    return ((com.zimbra.cs.mailbox.Document)mMailItem).getDescription();
/* $else$
    return new String();
   $endif$ */
  }

  public boolean isDescriptionEnabled()
  {
/* $if ZimbraVersion >= 7.0.1 $ */
    return ((com.zimbra.cs.mailbox.Document)mMailItem).isDescriptionEnabled();
/* $else$
    return (getDescription().length() > 0 );
   $endif$ */
  }
}