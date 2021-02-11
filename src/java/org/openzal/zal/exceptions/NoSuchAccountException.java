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

package org.openzal.zal.exceptions;

import com.zimbra.cs.account.AccountServiceException;

public class NoSuchAccountException extends ZimbraException {

  private String account;

  protected NoSuchAccountException(Exception exception) {
    super(exception);
  }

  public NoSuchAccountException(String account) {
    super(AccountServiceException.NO_SUCH_ACCOUNT(account));
    this.account = account;
  }

  public String getAccount() {
    return account;
  }
}
