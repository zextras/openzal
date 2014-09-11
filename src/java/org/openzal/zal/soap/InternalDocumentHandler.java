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

import com.zimbra.common.service.ServiceException;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.DocumentHandler;

import java.util.Map;

class InternalDocumentHandler extends DocumentHandler
{
  private final InternalDocumentHelper mInternalDocumentHelper;

  InternalDocumentHandler(SoapHandler soapHandler)
  {
    mInternalDocumentHelper = new InternalDocumentHelper(soapHandler);
  }

  @Override
  public Element handle(Element request, Map<String, Object> context) throws ServiceException
  {
    return mInternalDocumentHelper.handle(request, context);
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
}