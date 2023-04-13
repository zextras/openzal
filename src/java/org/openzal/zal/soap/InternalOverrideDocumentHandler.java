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

import javax.annotation.Nullable;
import org.openzal.zal.Utils;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.session.Session;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.log.ZimbraLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class InternalOverrideDocumentHandler extends DocumentHandler
{
  private final InternalDocumentHelper mInternalDocumentHelper;
  private final SoapHandler            mSoapHandler;
  private final DocumentHandler        mOriginalDocumentHandler;

  public InternalOverrideDocumentHandler(
    SoapHandler soapHandler,
    DocumentHandler originalDocumentHandler
  )
  {
    mSoapHandler = soapHandler;
    mOriginalDocumentHandler = originalDocumentHandler;
    mInternalDocumentHelper = new InternalDocumentHelper(soapHandler);
  }

  @Override
  public Element handle(final Element request, final Map<String, Object> context) throws ServiceException
  {
    return mInternalDocumentHelper.handle(request, context, new InternalDocumentHandler.Proxier()
    {
      @Override
      public Element proxy(String accountId) {
        try
        {
          return InternalOverrideDocumentHandler.this.proxyRequest(request, context, accountId);
        }
        catch (ServiceException e)
        {
          throw ExceptionWrapper.wrap(e);
        }
      }
    });
  }

  @Override
  public boolean needsAdminAuth(Map<String, Object> context)
  {
    return mInternalDocumentHelper.needsAdminAuth(context);
  }

  @Override
  public boolean needsAuth(Map<String, Object> context)
  {
    return mInternalDocumentHelper.needsAdminAuth(context);
  }

  public Boolean canAccessAccountCommon(ZimbraSoapContext zsc, Account target, boolean allowSelf) throws ServiceException
  {
    return mOriginalDocumentHandler.canAccessAccountCommon(zsc, target, allowSelf);
  }

  public boolean canAccessAccount(ZimbraSoapContext zsc, Account target) throws ServiceException
  {
    return mOriginalDocumentHandler.canAccessAccount(zsc, target);
  }

  public boolean canModifyOptions(ZimbraSoapContext zsc, Account acct) throws ServiceException
  {
    return mOriginalDocumentHandler.canModifyOptions(zsc, acct);
  }

  public boolean domainAuthSufficient(Map<String, Object> context)
  {
    return mOriginalDocumentHandler.domainAuthSufficient(context);
  }

  public boolean isAdminCommand()
  {
    return mOriginalDocumentHandler.isAdminCommand();
  }

  public boolean isReadOnly()
  {
    return mOriginalDocumentHandler.isReadOnly();
  }

  @Nullable private static Method sMethod = null;

  /*
    protected Element proxyIfNecessary(Element request, Map<String, Object> context) throws ServiceException {
  */
  static
  {
    try
    {
      Class[] parameters = {
        Element.class,
        Map.class
      };
      sMethod = DocumentHandler.class.getDeclaredMethod("proxyIfNecessary", parameters);
      sMethod.setAccessible(true);
    }
    catch (NoSuchMethodException ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }


  protected Element proxyIfNecessary(Element request, Map<String, Object> context) throws ServiceException
  {
    try
    {
      return (Element) sMethod.invoke(mOriginalDocumentHandler, request, context);
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch (InvocationTargetException e)
    {
      throw (ServiceException) e.getCause();
    }
  }

  public void preProxy(Element request, Map<String, Object> context) throws ServiceException
  {
    mOriginalDocumentHandler.preProxy(request, context);
  }

  public void postProxy(Element request, Element response, Map<String, Object> context) throws ServiceException
  {
    mOriginalDocumentHandler.postProxy(request, response, context);
  }

  public Session updateAuthenticatedAccount(
    ZimbraSoapContext zsc,
    AuthToken authToken,
    Map<String, Object> context,
    boolean getSession
  )
  {
    return mOriginalDocumentHandler.updateAuthenticatedAccount(zsc, authToken, context, getSession);
  }

  public Session.Type getDefaultSessionType()
  {
    return mOriginalDocumentHandler.getDefaultSessionType();
  }

  public void logAuditAccess(String delegatingAcctId, String authedAcctId, String targetAcctId)
  {
    mOriginalDocumentHandler.logAuditAccess(delegatingAcctId, authedAcctId, targetAcctId);
  }

  public boolean defendsAgainstDelegateAdminAccountHarvesting() {
    return mOriginalDocumentHandler.defendsAgainstDelegateAdminAccountHarvesting();
  }

}
