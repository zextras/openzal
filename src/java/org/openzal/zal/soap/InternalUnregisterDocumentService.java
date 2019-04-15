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
import com.zimbra.soap.DocumentService;
import org.dom4j.Namespace;
import javax.annotation.Nonnull;

import java.util.Map;

class InternalUnregisterDocumentService implements DocumentService
{
  private final SoapService mSoapService;

  public InternalUnregisterDocumentService(SoapService soapService)
  {
    mSoapService = soapService;
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
    }
  }
}
