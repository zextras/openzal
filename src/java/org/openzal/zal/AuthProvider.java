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


import com.zimbra.cs.account.AuthTokenException;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

/**
 * Create new zimbra authentication tokens and parse existing one.
 * It also verify the validity of the tokens.
 */
public class AuthProvider
{
  /**
   * Create a ZAuthToken to communicate via SOAP.
   * The generated token can be admin level.
   * @param requester the account to create the token for
   * @return the ZAuthToken, to be used with zimbra SOAP library
   */
  @Nonnull
  public static ZAuthToken createZAuthToken(@Nonnull Account requester)
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

  /**
   * Parse the provided encoded token in a zal token.
   * The token will be validated before being returned.
   * @param encoded String containing the encoded token
   * @throws AuthToken.TokenExpired, AuthTokenException
   */
  @Nonnull
  public AuthToken decodeAuthToken(@Nonnull String encoded)
    throws AuthToken.TokenExpired, AuthTokenException
  {
    try
    {
      return AuthToken.validate(com.zimbra.cs.account.AuthToken.getAuthToken(encoded));
    }
    catch (AuthTokenException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  /**
   * Create a non-admin level authentication token for the provided account
   */
  @Nonnull
  public AuthToken createAuthTokenForAccount(@Nonnull Account account)
  {
    return AuthToken.createNewToken(account);
  }

  public AuthToken decodeJwtAuthToken(String zm_auth_jwt, String zm_jwt) throws AuthToken.TokenExpired
  {
    throw new UnsupportedOperationException();
  }
}
