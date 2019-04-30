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

import com.zimbra.common.soap.Element;
import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.DocumentService;
import org.dom4j.Namespace;
import javax.annotation.Nonnull;
import org.openzal.zal.log.ZimbraLog;

import java.util.Map;

public class InternalOverrideDocumentServiceImpl implements DocumentService
{
  private final OverridenSoapService                  mSoapService;
  private final HandlerMapPublisher                   mHandlerMapPublisher;
  private final Map<org.dom4j.QName, DocumentHandler> mOriginalHandlers;

  public InternalOverrideDocumentServiceImpl(
    OverridenSoapService soapService,
    HandlerMapPublisher handlerMapPublisher,
    Map<org.dom4j.QName, DocumentHandler> originalHandlers
  )
  {
    mSoapService = soapService;
    mHandlerMapPublisher = handlerMapPublisher;
    mOriginalHandlers = originalHandlers;
  }

  @Nonnull
  private DocumentHandler wrapHandler(SoapHandler soapHandler, DocumentHandler originalDocumentHandler )
  {
    if (mSoapService.isAdminService())
    {
      return new InternalOverrideAdminDocumentHandler(soapHandler,originalDocumentHandler);
    }
    else
    {
      return new InternalOverrideDocumentHandler(soapHandler, originalDocumentHandler);
    }
  }

  private SoapHandler unWrapHandler(final DocumentHandler documentHandler)
  {
    return new SoapHandler(){
      @Override
      public void handleRequest(ZimbraContext context, SoapResponse soapResponse, ZimbraExceptionContainer zimbraExceptionContainer)
      {
        ZimbraContextImpl zimbraContext = (ZimbraContextImpl) context;
        try
        {
          Element response = documentHandler.handle(zimbraContext.getRequest().toZimbra(Element.class), zimbraContext.getContext());
          soapResponse.setResponse(
            new SoapResponseImpl(
              response,
              new InternalDocumentHelper.ElementFactory(
                zimbraContext.getZimbraSoapContext()
              )
            )
          );
        }
        catch (Exception ex)
        {
          zimbraExceptionContainer.setException(ex);
        }
      }

      @Override
      public boolean needsAdminAuthentication(ZimbraContext context)
      {
        ZimbraContextImpl zimbraContext = (ZimbraContextImpl) context;
        return documentHandler.needsAdminAuth(zimbraContext.getContext());
      }

      @Override
      public boolean needsAuthentication(ZimbraContext context)
      {
        ZimbraContextImpl zimbraContext = (ZimbraContextImpl) context;
        return documentHandler.needsAuth(zimbraContext.getContext());
      }
    };
  }


  @Override
  public void registerHandlers(DocumentDispatcher dispatcher)
  {
    // these are latest original handlers, they may be already overriden
    Map<org.dom4j.QName, DocumentHandler> oringinalHandlers = dispatcher.getHandlers();
    mHandlerMapPublisher.receivedHandlerMap(oringinalHandlers);

    Map<QName, OverridenSoapHandler> services = mSoapService.getServices();
    for (Map.Entry<QName, OverridenSoapHandler> entry : services.entrySet())
    {
      QName qName = entry.getKey();
      org.dom4j.QName zimbraQName = new org.dom4j.QName(
        qName.getName(),
        Namespace.get(qName.getNamespace())
      );

      DocumentHandler originalDocumentHandler = null;
      if (mOriginalHandlers.containsKey(zimbraQName))
      {
        originalDocumentHandler = mOriginalHandlers.get(zimbraQName);
        entry.getValue().setOriginalHandler(
          unWrapHandler(
            originalDocumentHandler
          )
        );
      }

      if( originalDocumentHandler != null )
      {
        dispatcher.registerHandler(
          zimbraQName,
          wrapHandler(entry.getValue(), originalDocumentHandler)
        );
      }
      else
      {
        ZimbraLog.extensions.warn("Unable to proxy SOAP Request: "+zimbraQName.toString());
      }
    }
  }
}
