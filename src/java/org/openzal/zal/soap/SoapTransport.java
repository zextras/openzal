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

import com.zimbra.common.soap.Element;
import org.openzal.zal.XMLElement;
import org.openzal.zal.ZAuthToken;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapHttpTransport;

import java.io.IOException;

public class SoapTransport
{
  private final SoapHttpTransport mSoapHttpTransport;

  public SoapTransport(String adminUrl)
  {
    mSoapHttpTransport = new SoapHttpTransport(adminUrl);
  }

  public void setAuthToken(ZAuthToken authToken)
  {
    mSoapHttpTransport.setAuthToken(
      authToken.toZimbra(com.zimbra.common.auth.ZAuthToken.class)
    );
  }

  public SoapElement invoke(XMLElement request) throws IOException
  {
    try
    {
      return new SoapElement(
        mSoapHttpTransport.invoke(
          request.toZimbra(Element.XMLElement.class)
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
