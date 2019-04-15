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

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.DocumentService;
import org.dom4j.Namespace;
import javax.annotation.Nonnull;

import java.util.Map;

class InternalDocumentService implements DocumentService
{
  private final SoapService mSoapService;
  private final HandlerMapPublisher mHandlerMapPublisher;

  public InternalDocumentService(
    SoapService soapService,
    HandlerMapPublisher handlerMapPublisher
  )
  {
    mSoapService = soapService;
    mHandlerMapPublisher = handlerMapPublisher;
  }

  @Nonnull
  private DocumentHandler wrapHandler(SoapHandler soapHandler)
  {
    if ( mSoapService.isAdminService() )
    {
      return new InternalAdminDocumentHandler(soapHandler);
    }
    else
    {
      return new InternalDocumentHandler(soapHandler);
    }
  }

  @Override
  public void registerHandlers(DocumentDispatcher dispatcher)
  {
    mHandlerMapPublisher.receivedHandlerMap( dispatcher.getHandlers() );

    Map<QName, ? extends SoapHandler> services = mSoapService.getServices();
    for (Map.Entry<QName, ? extends SoapHandler> entry : services.entrySet())
    {
      QName qName = entry.getKey();
      org.dom4j.QName zimbraQName = new org.dom4j.QName(
        qName.getName(),
        Namespace.get(qName.getNamespace())
      );

      dispatcher.registerHandler(
        zimbraQName,
        wrapHandler(entry.getValue())
      );
    }
  }
}
