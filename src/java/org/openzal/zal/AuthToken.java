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
import com.zimbra.cs.account.AuthTokenException;
/* $if ZimbraX == 1 $
import com.zimbra.cs.account.ZimbraJWToken;
import com.zimbra.cs.service.util.JWTUtil;
/* $endif $ */
import java.util.Map;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;

public class AuthToken
{
  @Nonnull private final com.zimbra.cs.account.AuthToken mAuthToken;

  public final static String[] sUSER_TOKENS_JWT = {"ZM_AUTH_JWT", "ZM_JWT"};
  public final static String[] sUSER_TOKENS     = {"ZM_AUTH_TOKEN"};
  public final static String[] sADMIN_TOKENS    = {"ZM_ADMIN_AUTH_TOKEN"};

  protected AuthToken(@Nonnull Object authToken)
  {
    if (authToken == null)
    {
      throw new NullPointerException();
    }
    mAuthToken = (com.zimbra.cs.account.AuthToken) authToken;
  }

  @Deprecated
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

  public static AuthToken getAuthToken(Map<String, String> cookies)
    throws org.openzal.zal.exceptions.AuthTokenException
  {
    try
    {
      /* $if ZimbraX == 1 $
      if( cookies.containsKey("ZM_AUTH_JWT") && cookies.containsKey("ZM_JWT") )
      {
        return new AuthToken(ZimbraJWToken.getJWToken(
          cookies.get("ZM_AUTH_JWT"),
          cookies.get("ZM_JWT")
        ));
      }
      /* $endif $ */
      if( cookies.containsKey("ZM_AUTH_TOKEN") )
      {
        return new AuthToken(com.zimbra.cs.account.AuthToken.getAuthToken(cookies.get("ZM_AUTH_TOKEN")));
      }

      throw new AuthTokenException("Missing auth cookies!");
    }
    catch( AuthTokenException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static AuthToken getAdminAuthToken(Map<String, String> cookies)
    throws org.openzal.zal.exceptions.AuthTokenException
  {
    try
    {
      if( cookies.containsKey("ZM_ADMIN_AUTH_TOKEN") )
      {
        return new AuthToken(com.zimbra.cs.account.AuthToken.getAuthToken(cookies.get("ZM_ADMIN_AUTH_TOKEN")));
      }

      throw new AuthTokenException("Missing admin auth cookies!");
    }
    catch( AuthTokenException e )
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
