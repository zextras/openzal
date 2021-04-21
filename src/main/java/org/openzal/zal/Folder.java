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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;


public class Folder extends Item
{
  public Folder(@Nonnull Object item)
  {
    super((MailItem) item);
  }

  public Folder(@Nonnull Item item)
  {
    super(item);
  }

  @Nullable
  public Acl getACL()
  {
    ACL acl = ((com.zimbra.cs.mailbox.Folder) mMailItem).getACL();
    if (acl == null)
    {
      return null;
    }
    return new Acl(acl);
  }

  @Nonnull
  public String getUrl()
  {
    return ((com.zimbra.cs.mailbox.Folder) mMailItem).getUrl();
  }

  public byte getDefaultView()
  {
    return Item.byteType(((com.zimbra.cs.mailbox.Folder) mMailItem).getDefaultView());
  }

  public byte getAttributes()
  {
    return ((com.zimbra.cs.mailbox.Folder) mMailItem).getAttributes();
  }

  public boolean isParentOf(Folder folder)
    throws ZimbraException
  {
    try
    {
      return ((com.zimbra.cs.mailbox.Folder) mMailItem).isDescendant(
        folder.toZimbra(com.zimbra.cs.mailbox.Folder.class)
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isRoot()
  {
    int id = getId();
    return (id == Mailbox.ID_FOLDER_ROOT || id == Mailbox.ID_FOLDER_USER_ROOT);
  }

  @Nonnull
  public Folder getParent()
    throws ZimbraException
  {
    try
    {
      MailItem item = ((com.zimbra.cs.mailbox.Folder) mMailItem).getParent();
      if( item == null )
      {
        throw new RuntimeException("Root does not have a parent");
      }
      else
      {
        return new Folder(item);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean canAccess(Account account) throws ZimbraException
  {
    try
    {
      return com.zimbra.cs.mailbox.CalendarItem.allowPrivateAccess(
        (com.zimbra.cs.mailbox.Folder) mMailItem,
        (com.zimbra.cs.account.Account) account.toZimbra(),
        false
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isHidden()
  {
     return ((com.zimbra.cs.mailbox.Folder) mMailItem).isHidden();
  }
}
