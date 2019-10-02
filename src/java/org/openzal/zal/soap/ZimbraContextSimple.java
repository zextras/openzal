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

package org.openzal.zal.soap;

import javax.annotation.Nonnull;
import org.openzal.zal.Continuation;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ZimbraContextSimple implements ZimbraContext
{
  private final String  mTargetAccountId;
  private final String  mAuthenticatedAccountId;
  private final String  mRequesterIp;
  private final boolean mDelegatedAuth;
  private final Map<String, String> mParameters;
  private final HttpServletRequest mRequest;

  public ZimbraContextSimple()
  {
    this("","","",false,new HashMap<String, String>(), null);
  }

  public ZimbraContextSimple(
    String targetAccountId,
    String authenticatedAccountId,
    String requesterIp,
    boolean delegatedAuth,
    Map<String, String> parameters,
    HttpServletRequest request
  )
  {
    mTargetAccountId = targetAccountId;
    mAuthenticatedAccountId = authenticatedAccountId;
    mRequesterIp = requesterIp;
    mDelegatedAuth = delegatedAuth;
    mParameters = parameters;
    mRequest = request;
  }

  public ZimbraContextSimple(
    String targetAccountId,
    String authenticatedAccountId,
    String requesterIp,
    boolean delegatedAuth,
    Map<String, String> parameters
  )
  {
    this(targetAccountId, authenticatedAccountId, requesterIp, delegatedAuth, parameters, null);
  }

  @Override
  public SoapResponse proxyRequestTo(String accountId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getTargetAccountId()
  {
    return mTargetAccountId;
  }

  @Override
  public String getAuthenticatedAccontId()
  {
    return mAuthenticatedAccountId;
  }

  @Override
  public String getRequesterIp()
  {
    return mRequesterIp;
  }

  @Nonnull
  @Override
  public SoapResponse execLocalRequest()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpServletRequest getHttpServletRequest()
  {
    return mRequest;
  }

  @Override
  public Continuation getContinuation()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDelegatedAuth()
  {
    return mDelegatedAuth;
  }

  @Override
  public InternalDocumentHelper.ElementFactory getElementFactory()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SoapElement getRequest()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasParameter(String key)
  {
    return mParameters.containsKey(key);
  }

  @Override
  public SoapNode getSubNode(String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, String> getParameterMap()
  {
    return mParameters;
  }

  @Override
  public String getParameter(String key, String def)
  {
    String value = mParameters.get(key);
    return value == null ? def : value;
  }

  @Override
  public String getNodeName()
  {
    return "";
  }

  public ZimbraContextSimple set(String key, String value)
  {
    mParameters.put(key, value);
    return this;
  }

  @Override
  public String getText()
  {
    return "";
  }
}
