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
import com.zimbra.cs.account.Provisioning;
import javax.annotation.Nonnull;
import com.zimbra.common.account.ZAttrProvisioning;
import org.openzal.zal.exceptions.ExceptionWrapper;

public class AccountStatus
{
  public static String ACCOUNT_STATUS_MAINTENANCE = Provisioning.ACCOUNT_STATUS_MAINTENANCE;
  public static String ACCOUNT_STATUS_LOCKED      = Provisioning.ACCOUNT_STATUS_LOCKED;
  public static String ACCOUNT_STATUS_LOCKOUT     = Provisioning.ACCOUNT_STATUS_LOCKOUT;
  public static String ACCOUNT_STATUS_ACTIVE      = Provisioning.ACCOUNT_STATUS_ACTIVE;
  public static String ACCOUNT_STATUS_CLOSED      = Provisioning.ACCOUNT_STATUS_CLOSED;

  @Nonnull private final ZAttrProvisioning.AccountStatus mAccountStatus;

  public static AccountStatus maintenance = new AccountStatus(ZAttrProvisioning.AccountStatus.maintenance);
  public static AccountStatus active = new AccountStatus(ZAttrProvisioning.AccountStatus.active);

  protected AccountStatus(@Nonnull Object accountStatus)
  {
    if (accountStatus == null)
    {
      throw new NullPointerException();
    }
    mAccountStatus = (ZAttrProvisioning.AccountStatus) accountStatus;
  }

  public AccountStatus(@Nonnull String status)
  {
    try
    {
      mAccountStatus = ZAttrProvisioning.AccountStatus.fromString(status);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public int hashCode()
  {
    return mAccountStatus.hashCode();
  }

  @Override
  public boolean equals(Object object)
  {
    return mAccountStatus.equals(object);
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mAccountStatus);
  }

  @Override
  public String toString()
  {
    return mAccountStatus.toString();
  }
}
