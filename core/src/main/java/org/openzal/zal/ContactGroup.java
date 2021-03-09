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

import javax.annotation.Nonnull;

public class ContactGroup
{
  private final com.zimbra.cs.mailbox.ContactGroup mContactGroup;

  ContactGroup(@Nonnull Object contactGroup)
  {
    if (contactGroup == null)
    {
      throw new NullPointerException();
    }
    mContactGroup = (com.zimbra.cs.mailbox.ContactGroup) contactGroup;
  }

  public ContactGroup()
  {
    try
    {
      mContactGroup = com.zimbra.cs.mailbox.ContactGroup.init();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public enum Type
  {
    CONTACT_REFERENCE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.CONTACT_REF),
    GAL_REFEFERENCE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.GAL_REF),
    INLINE(com.zimbra.cs.mailbox.ContactGroup.Member.Type.INLINE);

    private final com.zimbra.cs.mailbox.ContactGroup.Member.Type mZimbraType;

    <T> T toZimbra(@Nonnull Class<T> cls)
    {
      return cls.cast(mZimbraType);
    }

    Type(com.zimbra.cs.mailbox.ContactGroup.Member.Type zimbraType)
    {
      mZimbraType = zimbraType;
    }
  }

  public String encode()
  {
    return mContactGroup.encode();
  }

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

  public void addMember(@Nonnull Type type, String value)
  {
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
  }
}
