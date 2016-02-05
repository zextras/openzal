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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

import org.jetbrains.annotations.NotNull;

public class ContactGroup
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final com.zimbra.cs.mailbox.ContactGroup mContactGroup;
  /* $endif $ */

  ContactGroup(@NotNull Object contactGroup)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    if (contactGroup == null)
    {
      throw new NullPointerException();
    }
    mContactGroup = (com.zimbra.cs.mailbox.ContactGroup) contactGroup;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ContactGroup()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      mContactGroup = com.zimbra.cs.mailbox.ContactGroup.init();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public enum Type
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    CONTACT_REFERENCE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.CONTACT_REF),
    GAL_REFEFERENCE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.GAL_REF),
    INLINE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.INLINE);

    private final com.zimbra.cs.mailbox.ContactGroup.Member.Type mZimbraType;

    <T> T toZimbra(@NotNull Class<T> cls)
    {
      return cls.cast(mZimbraType);
    }

    Type(com.zimbra.cs.mailbox.ContactGroup.Member.Type zimbraType)
    {
      mZimbraType = zimbraType;
    }
    /* $else $
    CONTACT_REFERENCE(null),
    GAL_REFEFERENCE(null),
    INLINE(null);

    private final Object mZimbraType;

    <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mZimbraType);
    }

    Type(Object zimbraType)
    {
      mZimbraType = zimbraType;
    }
    /* $endif $ */
  }

  public String encode()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mContactGroup.encode();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  com.zimbra.cs.mailbox.ContactGroup init()
  {
    try
    {
      return mContactGroup.init();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
  /* $else $
  Object init()
  {
    throw new UnsupportedOperationException();
  }
  /* $endif $ */

  public void addMember(@NotNull Type type, String value)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      mContactGroup.addMember(
        type.toZimbra(com.zimbra.cs.mailbox.ContactGroup.Member.Type.class),
        value
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
