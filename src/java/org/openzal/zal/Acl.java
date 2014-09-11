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

import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.mailbox.ACL;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Acl
{
  public static byte GRANTEE_USER     = ACL.GRANTEE_USER;
  public static byte GRANTEE_GROUP    = ACL.GRANTEE_GROUP;
  public static byte GRANTEE_AUTHUSER = ACL.GRANTEE_AUTHUSER;
  public static byte GRANTEE_DOMAIN   = ACL.GRANTEE_DOMAIN;
  public static byte GRANTEE_COS      = ACL.GRANTEE_COS;
  public static byte GRANTEE_PUBLIC   = ACL.GRANTEE_PUBLIC;
  public static byte GRANTEE_GUEST    = ACL.GRANTEE_GUEST;
  public static byte GRANTEE_KEY      = ACL.GRANTEE_KEY;

  @NotNull private final ACL mAcl;

  public Acl()
  {
    this(new ACL());
  }

  Acl(@NotNull Object acl)
  {
    if (acl == null)
    {
      throw new NullPointerException();
    }
    mAcl = (ACL) acl;
  }

  public Short getGrantedRights(Account authuser)
    throws ZimbraException
  {
    try
    {
      return mAcl.getGrantedRights(authuser.toZimbra(com.zimbra.cs.account.Account.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isEmpty()
  {
    return getGrants().isEmpty();
  }

  public Grant grantAccess(String zimbraId, byte type, short rights, String secret)
    throws ZimbraException
  {
    ACL.Grant grant;
    try
    {
      grant = mAcl.grantAccess(zimbraId, type, rights, secret);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new Grant(grant);
  }

  public Acl duplicate()
  {
    return new Acl(mAcl.duplicate());
  }

  public List<Grant> getGrants()
  {
    return ZimbraListWrapper.wrapGrants(mAcl.getGrants());
  }

  public static String typeToString(byte type)
  {
    return ACL.typeToString(type);
  }

  public static String rightsToString(short rights)
  {
    return ACL.rightsToString(rights);
  }

  public boolean revokeAccess(String zimbraId)
  {
    return mAcl.revokeAccess(zimbraId);
  }


  /* $if MajorZimbraVersion >= 8 $ */
  public Grant grantAccess(String zimbraId, byte type, short rights, String secret, long expiry)
    throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return new Grant(mAcl.grantAccess(zimbraId, type, rights, secret, expiry));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getInternalGrantExpiry()
  {
    return mAcl.getInternalGrantExpiry();
  }

  public long getGuestGrantExpiry()
  {
    return mAcl.getGuestGrantExpiry();
  }
/* $else$

  public Grant grantAccess(String zimbraId, byte type, short rights, String secret, long expiry)
    throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return new Grant(mAcl.grantAccess(zimbraId, type, rights, secret));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getInternalGrantExpiry()
  {
    return 0L;
  }

  public long getGuestGrantExpiry()
  {
    return 0L;
  }
/* $endif$ */

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mAcl);
  }
}
