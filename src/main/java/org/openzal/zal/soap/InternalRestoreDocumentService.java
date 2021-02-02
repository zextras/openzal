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

class InternalRestoreDocumentService implements DocumentService
{
  private final SoapService mSoapService;
  private final Map<org.dom4j.QName, DocumentHandler> mOriginalHandlersMap;

  public InternalRestoreDocumentService(
    SoapService soapService,
    Map<org.dom4j.QName, DocumentHandler> originalHandlersMap
  )
  {
    mSoapService = soapService;
    mOriginalHandlersMap = originalHandlersMap;
  }

  @Override
  public void registerHandlers(@Nonnull DocumentDispatcher dispatcher)
  {
    Map<QName, ? extends SoapHandler> services = mSoapService.getServices();
    for( QName qName : services.keySet() )
    {
      org.dom4j.QName zimbraQName = new org.dom4j.QName(
        qName.getName(),
        Namespace.get(qName.getNamespace())
      );

      dispatcher.unRegisterHandler(
        zimbraQName
      );

      if( mOriginalHandlersMap.containsKey(zimbraQName) )
      {
        dispatcher.registerHandler(
          zimbraQName,
          mOriginalHandlersMap.get(zimbraQName)
        );
      }
    }
  }
}
