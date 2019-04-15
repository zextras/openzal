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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.cs.account.AuthTokenException;
import javax.annotation.Nonnull;

public class AuthToken
{
  @Nonnull private final com.zimbra.cs.account.AuthToken mAuthToken;

  protected AuthToken(@Nonnull Object authToken)
  {
    if (authToken == null)
    {
      throw new NullPointerException();
    }
    mAuthToken = (com.zimbra.cs.account.AuthToken) authToken;
  }

  public static AuthToken getAuthToken(String encoded) throws org.openzal.zal.exceptions.AuthTokenException
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

  public String getAccountId()
  {
    return mAuthToken.getAccountId();
  }

  public boolean isAdmin()
  {
    return mAuthToken.isAdmin();
  }

  public boolean isDelegatedAdmin()
  {
    return mAuthToken.isDelegatedAdmin();
  }

  public String toString()
  {
    return mAuthToken.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mAuthToken);
  }

  public String getEncoded()
  {
    try
    {
      return mAuthToken.getEncoded();
    }
    catch (AuthTokenException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
