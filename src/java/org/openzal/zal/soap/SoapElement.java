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

import org.openzal.zal.ZimbraListWrapper;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

import java.util.List;

public class SoapElement
{
  private final Element mElement;

  public SoapElement(Object element)
  {
    mElement = (Element)element;
  }

  public List<SoapElement> getPathElementList(String[] xpath)
  {
    return ZimbraListWrapper.wrapElements(mElement.getPathElementList(xpath));

  }

  public String getAttribute(String key)
  {
    try
    {
      return mElement.getAttribute(key);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getAttributeLong(String key)
  {
    try
    {
      return mElement.getAttributeLong(key);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
