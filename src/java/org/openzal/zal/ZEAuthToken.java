/*
 * ZAL - An abstraction layer for Zimbra.
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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import org.jetbrains.annotations.NotNull;

public class ZEAuthToken
{
  private final AuthToken mAuthToken;

  protected ZEAuthToken(@NotNull Object authToken)
  {
    if ( authToken == null )
    {
      throw new NullPointerException();
    }
    mAuthToken = (AuthToken)authToken;
  }

  public static ZEAuthToken getAuthToken(String encoded) throws org.openzal.zal.exceptions.AuthTokenException
  {
    try
    {
      return new ZEAuthToken(AuthToken.getAuthToken(encoded));
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
}
