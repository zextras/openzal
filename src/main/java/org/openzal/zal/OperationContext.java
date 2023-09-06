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
import org.openzal.zal.calendar.Attendee;


public class OperationContext
{
  @Nonnull private final com.zimbra.cs.mailbox.OperationContext mOperationContext;

  public OperationContext(@Nonnull Account account, boolean enableAdminRights)
  {
    mOperationContext = new com.zimbra.cs.mailbox.OperationContext(
      account.toZimbra(com.zimbra.cs.account.Account.class), enableAdminRights
    );
  }

  public OperationContext(@Nonnull Account account)
  {
    mOperationContext = new com.zimbra.cs.mailbox.OperationContext(account.toZimbra(com.zimbra.cs.account.Account.class), false);
  }

  OperationContext(@Nonnull Object operationContext)
  {
    if (operationContext == null)
    {
      throw new NullPointerException();
    }
    mOperationContext = (com.zimbra.cs.mailbox.OperationContext) operationContext;
  }

  public static OperationContext buildFromZimbra(Object operationContext) {
    return new OperationContext(operationContext);
  }

  @Nonnull
  com.zimbra.cs.mailbox.OperationContext getOperationContext()
  {
    return mOperationContext;
  }

  public Account getAccount()
  {
    return new Account(mOperationContext.getAuthenticatedUser());
  }

  public OperationContext setRequestIP (@Nonnull String addr)
  {
    mOperationContext.setRequestIP(addr);
    return this;
  }
}
