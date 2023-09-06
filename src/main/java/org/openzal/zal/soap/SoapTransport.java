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
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.soap.JaxbUtil;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.openzal.zal.XMLElement;
import org.openzal.zal.ZAuthToken;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapHttpTransport;

import java.io.IOException;

public class SoapTransport
{
  @Nonnull private final SoapHttpTransport mSoapHttpTransport;

  public SoapTransport(String adminUrl)
  {
    mSoapHttpTransport = new SoapHttpTransport(adminUrl);
  }

  public void shutdown() {
    mSoapHttpTransport.shutdown();
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

  public SoapElement invokeWithoutSession(XMLElement request) throws IOException
  {
    try
    {
      return new SoapElement(
        mSoapHttpTransport.invokeWithoutSession(
          request.toZimbra(Element.XMLElement.class)
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <T> T invoke(Object jaxbObject)
    throws IOException
  {
    return (T) invoke(jaxbObject, SoapProtocol.Soap12);
  }

  public <T> T invoke(Object jaxbObject, SoapProtocol proto) throws IOException
  {
    try
    {
      Element req = JaxbUtil.jaxbToElement(jaxbObject, proto.getFactory());
      Element res = mSoapHttpTransport.invoke(req);
      return (T) JaxbUtil.elementToJaxb(res);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <T> T invokeWithoutSession(Object jaxbObject)
    throws IOException
  {
    return (T) invokeWithoutSession(jaxbObject, SoapProtocol.Soap12);
  }

  public <T> T invokeWithoutSession(Object jaxbObject, SoapProtocol proto) throws IOException
  {
    try
    {
      Element req = JaxbUtil.jaxbToElement(jaxbObject, proto.getFactory());
      Element res = mSoapHttpTransport.invokeWithoutSession(req);
      return (T) JaxbUtil.elementToJaxb(res);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void setTargetAcctId(String targetAcctId)
  {
    mSoapHttpTransport.setTargetAcctId(targetAcctId);
  }

  public void setCustomHeader(String header,String value)
  {
    Map<String, String> customHeaders = mSoapHttpTransport.getCustomHeaders();
    customHeaders.put(header, value);
  }

  public void invokeAsync(Object requestObject, Object callback) throws IOException {
    try  {
      Element req = JaxbUtil.jaxbToElement(requestObject, SoapProtocol.Soap12.getFactory());
      mSoapHttpTransport.invokeAsync(req, (FutureCallback<HttpResponse>)callback);
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
