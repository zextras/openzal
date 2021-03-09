/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2017 ZeXtras S.r.l.
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
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.util.Collections;

public class ACLAccessManager
{
  private final com.zimbra.cs.account.accesscontrol.ACLAccessManager mAclAccessManager;

  public ACLAccessManager()
  {
    try
    {
      mAclAccessManager = new com.zimbra.cs.account.accesscontrol.ACLAccessManager();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean canLoginAsAccount(Account authAccount, Account target)
  {
    try
    {
      return mAclAccessManager.canAccessAccount(
        authAccount.toZimbra(com.zimbra.cs.account.Account.class),
        target.toZimbra(com.zimbra.cs.account.Account.class),
        true
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean canModifyAccountStatus(Account authAccount, Account target)
  {
    try
    {
      return mAclAccessManager.canSetAttrs(
        authAccount.toZimbra(com.zimbra.cs.account.Account.class),
        target.toZimbra(com.zimbra.cs.account.Account.class),
        Collections.singleton( "zimbraAccountStatus" ),
        true
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean canAccessGroup(Account authAccount, Group target)
  {
    try
    {
      return mAclAccessManager.canAccessGroup(
        authAccount.toZimbra(com.zimbra.cs.account.Account.class),
        target.toZimbra(com.zimbra.cs.account.Group.class),
        true
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
