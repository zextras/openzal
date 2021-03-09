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
import org.openzal.zal.exceptions.*;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.Metadata;


public class Grant
{
  @Nonnull private final ACL.Grant mGrant;

  public Grant(Object grant)
  {
    mGrant = (ACL.Grant) grant;
  }

  public boolean hasGrantee()
  {
    return mGrant.hasGrantee();
  }

  public String getGranteeId()
  {
    return mGrant.getGranteeId();
  }

  public byte getGranteeType()
  {
    return mGrant.getGranteeType();
  }

  public short getGrantedRights()
  {
    return mGrant.getGrantedRights();
  }

  public String getGranteeName()
  {
    return mGrant.getGranteeName();
  }

  public void setGranteeName(String name)
  {
    mGrant.setGranteeName(name);
  }

  public boolean matches(Account acct)
    throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      return mGrant.matches(acct);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isGrantee(String zimbraId)
  {
    return mGrant.isGrantee(zimbraId);
  }

  public String getPassword()
  {
    return mGrant.getPassword();
  }

  @Nonnull
  public Metadata encode()
  {
    return mGrant.encode();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mGrant);
  }
}
