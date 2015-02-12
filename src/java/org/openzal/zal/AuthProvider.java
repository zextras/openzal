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


import com.google.inject.Singleton;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.service.AuthProviderException;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;


@Singleton
public class AuthProvider
{
  @NotNull
  public static ZAuthToken getAuthToken(@NotNull Account requester)
  {
    try
    {
      return new ZAuthToken(
        com.zimbra.cs.service.AuthProvider.getAuthToken(
          requester.toZimbra(com.zimbra.cs.account.Account.class),
          true
        ).toZAuthToken());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public AuthToken decodeAuthToken(@NotNull String encoded)
  {
    try
    {
      return new AuthToken(com.zimbra.cs.account.AuthToken.getAuthToken(encoded));
    }
    catch (AuthTokenException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public AuthToken createAuthTokenForAccount(@NotNull Account account)
  {
    try
    {
      return new AuthToken(
        com.zimbra.cs.service.AuthProvider.getAuthToken(
          account.toZimbra(com.zimbra.cs.account.Account.class),
          true
        )
      );
    }
    catch (AuthProviderException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
