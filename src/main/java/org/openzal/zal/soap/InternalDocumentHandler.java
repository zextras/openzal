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
import com.zimbra.common.soap.Element;
import com.zimbra.soap.DocumentHandler;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.util.Map;

class InternalDocumentHandler extends DocumentHandler
{
  @Nonnull private final InternalDocumentHelper mInternalDocumentHelper;

  InternalDocumentHandler(SoapHandler soapHandler)
  {
    mInternalDocumentHelper = new InternalDocumentHelper(soapHandler);
  }

  public interface Proxier
  {
    Element proxy(String accountId);
  }

  @Override
  public Element handle(final Element request, final Map<String, Object> context) throws ServiceException
  {
    return mInternalDocumentHelper.handle(request, context, new Proxier(){
      @Override
      public Element proxy(String accountId)
      {
        return InternalDocumentHandler.this.proxy(request, context, accountId);
      }
    });
  }

  private Element proxy(Element request, Map<String, Object> context,String accountId)
  {
    try
    {
      return proxyRequest(request, context, accountId);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public boolean needsAdminAuth(Map<String, Object> context)
  {
    return mInternalDocumentHelper.needsAdminAuth(context);
  }

  @Override
  public boolean needsAuth(Map<String, Object> context)
  {
    return mInternalDocumentHelper.needsAuth(context);
  }
}