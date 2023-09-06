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

import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.service.AuthProviderException;
import java.util.Optional;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;

public class AuthToken
{
  @Nonnull private final com.zimbra.cs.account.AuthToken mAuthToken;

  public final static String[] sUSER_TOKENS_JWT = {"ZM_AUTH_JWT", "ZM_JWT"};
  public final static String[] sUSER_TOKENS     = {"ZM_AUTH_TOKEN"};
  public final static String[] sADMIN_TOKENS    = {"ZM_ADMIN_AUTH_TOKEN"};

  /**
   * Token is no longer valid
   */
  public static class TokenExpired extends Exception
  {
    private final AuthToken mAuthToken;

    public TokenExpired(String reason, AuthToken authToken)
    {
      super(reason);
      mAuthToken = authToken;
    }

    /**
     * This token is invalid, in case you still want to know who it belongs to
     * @return the invalid auth token
     */
    public AuthToken getAuthToken()
    {
      return mAuthToken;
    }
  }

  private AuthToken(@Nonnull Object authToken)
  {
    if (authToken == null)
    {
      throw new NullPointerException();
    }
    mAuthToken = (com.zimbra.cs.account.AuthToken) authToken;
  }

  /**
   * Internal API.
   * To create an AuthToken it MUST goes through validation to avoid security issues.
   * The only exception is when you create a new AuthToken.
   */
  static AuthToken validate(com.zimbra.cs.account.AuthToken zimbraToken) throws TokenExpired
  {
    AuthToken authToken = new AuthToken(zimbraToken);

    if( zimbraToken.isExpired() )
    {
      throw new TokenExpired("token is expired", authToken);
    }

    if( !zimbraToken.isRegistered() )
    {
      throw new TokenExpired("token is not registered", authToken);
    }

    return authToken;
  }

  /**
   * Internal API.
   * Create a new token for the provided user.
   */
  static AuthToken createNewToken(Account account)
  {
    try {
      return new AuthToken(
        com.zimbra.cs.service.AuthProvider.getAuthToken(
          account.toZimbra(com.zimbra.cs.account.Account.class),
          false
        )
      );
    } catch (
      AuthProviderException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long expireTimestamp()
  {
    return mAuthToken.getExpires();
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

  public boolean isDelegatedAuth()
  {
    return mAuthToken.isDelegatedAuth();
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

  /**
   * @return tokenId if available. May return <code>null</code> if tokenId is missing.
   */
  public Integer getZimbraTokenId() {
    if (ZimbraAuthToken.class.isAssignableFrom(mAuthToken.getClass())) {
      ZimbraAuthToken zat = (ZimbraAuthToken) mAuthToken;
      Integer tokenId;
      tokenId = zat.getProperties().getTokenID();
      if (tokenId == -1) return null;
      else return tokenId;
    } else {
      return null;
    }
  }
}
