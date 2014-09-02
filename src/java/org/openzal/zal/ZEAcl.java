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

import com.zimbra.cs.account.Account;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.mailbox.ACL;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ZEAcl
{
  public static final byte GRANTEE_USER     = ACL.GRANTEE_USER;
  public static final byte GRANTEE_GROUP    = ACL.GRANTEE_GROUP;
  public static final byte GRANTEE_AUTHUSER = ACL.GRANTEE_AUTHUSER;
  public static final byte GRANTEE_DOMAIN   = ACL.GRANTEE_DOMAIN;
  public static final byte GRANTEE_COS      = ACL.GRANTEE_COS;
  public static final byte GRANTEE_PUBLIC   = ACL.GRANTEE_PUBLIC;
  public static final byte GRANTEE_GUEST    = ACL.GRANTEE_GUEST;
  public static final byte GRANTEE_KEY      = ACL.GRANTEE_KEY;

  private final ACL mAcl;

  public ZEAcl()
  {
    this(new ACL());
  }

  ZEAcl(@NotNull Object acl)
  {
    if ( acl == null )
    {
      throw new NullPointerException();
    }
    mAcl = (ACL)acl;
  }

  public Short getGrantedRights(ZEAccount authuser)
    throws ZimbraException
  {
    try
    {
      return mAcl.getGrantedRights(authuser.toZimbra(Account.class));
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

  public ZEGrant grantAccess(String zimbraId, byte type, short rights, String secret)
    throws ZimbraException
  {
    ACL.Grant  grant;
    try
    {
      grant = mAcl.grantAccess(zimbraId, type, rights, secret);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new ZEGrant(grant);
  }

  public ZEAcl duplicate()
  {
    return new ZEAcl(mAcl.duplicate());
  }

  public List<ZEGrant> getGrants()
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
  public ZEGrant grantAccess(String zimbraId, byte type, short rights, String secret, long expiry)
    throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return new ZEGrant(mAcl.grantAccess(zimbraId, type, rights, secret, expiry));
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

  public ZEGrant grantAccess(String zimbraId, byte type, short rights, String secret, long expiry)
    throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return new ZEGrant(mAcl.grantAccess(zimbraId, type, rights, secret));
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
