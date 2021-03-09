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

import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;
import org.openzal.zal.ContinuationThrowable;
import org.openzal.zal.Utils;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.SoapEngine;
import com.zimbra.soap.ZimbraSoapContext;
import org.dom4j.Namespace;
import org.openzal.zal.log.ZimbraLog;

import java.util.Map;

public class InternalDocumentHelper
{
  private final SoapHandler mSoapHandler;

  public InternalDocumentHelper(SoapHandler soapHandler)
  {
    mSoapHandler = soapHandler;
  }

  public static class ElementFactory
  {
    private final ZimbraSoapContext mZimbraSoapContext;

    ElementFactory( ZimbraSoapContext zimbraSoapContext )
    {
      mZimbraSoapContext = zimbraSoapContext;
    }

    Element createElement( @Nonnull QName qName )
    {
      org.dom4j.QName domqName = org.dom4j.QName.get(
        qName.getName(),
        Namespace.get(qName.getNamespace())
      );
      return mZimbraSoapContext.createElement(domqName);
    }
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  public Element handle(Element request, Map<String, Object> context, InternalDocumentHandler.Proxier proxier) throws ZimbraException, ServiceException
  {
    ZimbraContext zimbraContext = new ZimbraContextImpl(request,context,proxier);
    ZimbraSoapContext zimbraSoapContext = (ZimbraSoapContext) context.get(SoapEngine.ZIMBRA_CONTEXT);
    Element element = zimbraSoapContext.createElement("response");
    SoapResponseImpl soapResponse = new SoapResponseImpl(element, new ElementFactory(zimbraSoapContext));
    ZimbraExceptionContainer container = new ZimbraExceptionContainer();

    try
    {
      mSoapHandler.handleRequest(zimbraContext, soapResponse, container);
    }
    catch( ContinuationThrowable continuationThrowable )
    {
      continuationThrowable.throwJettyException();
    }

    Throwable exception = container.getException();

    if( exception != null )
    {
      if (exception instanceof ServiceException)
      {
        throw (ServiceException) exception;
      }

      if (exception instanceof RuntimeException)
      {
        throw (RuntimeException) exception;
      }

      ZimbraLog.extensions.warn("ZAL SOAP Unknown Exception: " + Utils.exceptionToString(exception));
    }

    return soapResponse.getElement();
  }

  public boolean needsAdminAuth(Map<String, Object> context)
  {
    ZimbraContext zimbraContext = new ZimbraContextImpl(context);
    return mSoapHandler.needsAdminAuthentication(zimbraContext);
  }

  public boolean needsAuth(Map<String, Object> context)
  {
    ZimbraContext zimbraContext = new ZimbraContextImpl(context);
    return mSoapHandler.needsAuthentication(zimbraContext);
  }
}
