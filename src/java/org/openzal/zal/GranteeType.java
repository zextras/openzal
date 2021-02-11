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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;


public class GranteeType
{
  public static GranteeType GT_USER      = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_USER);
  public static GranteeType GT_GROUP     = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_GROUP);
  public static GranteeType GT_EXT_GROUP = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_EXT_GROUP);
  public static GranteeType GT_DOMAIN    = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_DOMAIN);
  public static GranteeType GT_AUTHUSER    = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_AUTHUSER);
  public static GranteeType GT_PUBLIC    = new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.GT_PUBLIC);

  private final com.zimbra.cs.account.accesscontrol.GranteeType mGranteeType;

  public GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType granteeType)
  {
    mGranteeType = granteeType;
  }

  @Nullable
  public String getCode()
  {
    if (mGranteeType != null)
    {
      return mGranteeType.getCode();
    }
    return null;
  }

  @Nonnull
  public static GranteeType fromCode(String code)
  {
    try
    {
      return new GranteeType(com.zimbra.cs.account.accesscontrol.GranteeType.fromCode(code));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mGranteeType);
  }
}
