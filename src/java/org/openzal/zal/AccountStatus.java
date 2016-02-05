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

import com.zimbra.cs.account.Provisioning;
import org.jetbrains.annotations.NotNull;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.account.ZAttrProvisioning;
/* $else $
import com.zimbra.cs.account.ZAttrProvisioning;
/* $endif $ */

public class AccountStatus
{
  public static String ACCOUNT_STATUS_MAINTENANCE = Provisioning.ACCOUNT_STATUS_MAINTENANCE;
  public static String ACCOUNT_STATUS_LOCKED      = Provisioning.ACCOUNT_STATUS_LOCKOUT;
  public static String ACCOUNT_STATUS_LOCKOUT     = Provisioning.ACCOUNT_STATUS_LOCKOUT;
  public static String ACCOUNT_STATUS_ACTIVE      = Provisioning.ACCOUNT_STATUS_ACTIVE;
  public static String ACCOUNT_STATUS_CLOSED      = Provisioning.ACCOUNT_STATUS_CLOSED;

  @NotNull private final ZAttrProvisioning.AccountStatus mAccountStatus;

  public static AccountStatus maintenance = new AccountStatus(ZAttrProvisioning.AccountStatus.maintenance);

  protected AccountStatus(@NotNull Object accountStatus)
  {
    if (accountStatus == null)
    {
      throw new NullPointerException();
    }
    mAccountStatus = (ZAttrProvisioning.AccountStatus) accountStatus;
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
}
