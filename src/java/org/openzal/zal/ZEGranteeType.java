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
import com.zimbra.cs.account.accesscontrol.GranteeType;
/* $if ZimbraVersion >= 8.0.0 $ */
/* $endif $ */

public class ZEGranteeType
{
  public static ZEGranteeType GT_USER  = new ZEGranteeType(GranteeType.GT_USER);
  public static ZEGranteeType GT_GROUP = new ZEGranteeType(GranteeType.GT_GROUP);
  /* $if ZimbraVersion >= 8.0.0 $ */
  public static ZEGranteeType GT_EXT_GROUP = new ZEGranteeType(GranteeType.GT_EXT_GROUP);
  /* $else $
  public static ZEGranteeType GT_EXT_GROUP = new ZEGranteeType(null);
  /* $endif $ */
  public static ZEGranteeType GT_DOMAIN = new ZEGranteeType(GranteeType.GT_DOMAIN);

  private final GranteeType mGranteeType;

  public ZEGranteeType(GranteeType granteeType)
  {
    mGranteeType = granteeType;
  }

  public String getCode()
  {
    if (mGranteeType != null)
    {
      return mGranteeType.getCode();
    }
    return null;
  }

  public static ZEGranteeType fromCode(String code)
  {
    try
    {
      return new ZEGranteeType(GranteeType.fromCode(code));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  /*
  public ZEAccount lookupAccountGrantee(
    ZEProvisioning provisioning,
    ZEGranteeType type, GranteeSelector.GranteeBy by,
    String grantee
  )
  {
    try
    {
      return new ZEAccount( (Account)
        mGranteeType.lookupGrantee(
          provisioning.getProxiedObject(),
          GranteeType.GT_USER, by, grantee
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
  */

}
