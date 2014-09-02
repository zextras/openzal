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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.mailbox.ContactGroup;
/* $endif $ */
import org.jetbrains.annotations.NotNull;

public class ZEContactGroup
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final ContactGroup mContactGroup;
  /* $endif $ */

  ZEContactGroup(@NotNull Object contactGroup)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    if ( contactGroup == null )
    {
      throw new NullPointerException();
    }
    mContactGroup = (ContactGroup)contactGroup;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEContactGroup()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      mContactGroup = ContactGroup.init();
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
    CONTACT_REFERENCE(ContactGroup.Member.Type.CONTACT_REF),
    GAL_REFEFERENCE(ContactGroup.Member.Type.GAL_REF),
    INLINE(ContactGroup.Member.Type.INLINE);

    private final ContactGroup.Member.Type mZimbraType;

    <T> T toZimbra(Class<T> cls)
    {
      return cls.cast(mZimbraType);
    }

    Type(ContactGroup.Member.Type zimbraType)
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
  public ContactGroup init()
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
  public Object init()
  {
    throw new UnsupportedOperationException();
  }
  /* $endif $ */

  public void addMember(Type type, String value)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      mContactGroup.addMember(
        type.toZimbra(ContactGroup.Member.Type.class),
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
