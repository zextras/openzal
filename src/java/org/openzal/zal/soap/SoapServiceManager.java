/*
 * ZAL - The abstraction layer for Zimbra.
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

package org.openzal.zal.soap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zimbra.soap.*;
import org.dom4j.QName;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SoapServiceManager
{
  private final Map<QName, DocumentHandler> mOriginalHandlersMap;

  @Inject
  public SoapServiceManager()
  {
    mOriginalHandlersMap = new HashMap<QName, DocumentHandler>(64);
  }

  class InternalHandlerMapPublisher implements HandlerMapPublisher
  {
    @Override
    public void receivedHandlerMap(Map<QName, DocumentHandler> handlerMap)
    {
      for( Map.Entry<QName, DocumentHandler> entry : handlerMap.entrySet() )
      {
        if( !mOriginalHandlersMap.containsKey(entry.getKey()) )
        {
          mOriginalHandlersMap.put(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  public void register(SoapService soapService)
  {
    SoapServlet.addService(
      soapService.getServiceName(),
      new InternalDocumentService(
        soapService,
        new InternalHandlerMapPublisher()
      )
    );
  }

  public void unregister(SoapService soapService)
  {
    SoapServlet.addService(
      soapService.getServiceName(),
      new InternalUnregisterDocumentService(soapService)
    );
  }

  public void overrideZimbraHandler(OverridenSoapService soapService)
  {
    SoapServlet.addService(
      soapService.getServiceName(),
      new InternalOverrideDocumentServiceImpl(
        soapService,
        new InternalHandlerMapPublisher(),
        mOriginalHandlersMap
      )
    );
  }


  public void restoreZimbraHandlers(OverridenSoapService soapService)
  {
    SoapServlet.addService(
      soapService.getServiceName(),
      new InternalRestoreDocumentService(soapService,mOriginalHandlersMap)
    );
  }
}
