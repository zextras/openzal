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

class SoapResponseImpl implements SoapResponse
{
  public Element getElement()
  {
    return mElement;
  }

  private Element mElement;
  private final InternalDocumentHelper.ElementFactory mElementFactory;

  public SoapResponseImpl(
    Element element,
    InternalDocumentHelper.ElementFactory elementFactory
  )
  {
    mElement = element;
    mElementFactory = elementFactory;
  }

  @Override
  public void setValue(String key, String value)
  {
    mElement.addAttribute(key, value);
  }

  @Override
  public void setValue(String key, boolean value)
  {
    mElement.addAttribute(key, value);
  }

  @Override
  public void setValue(String key, long value)
  {
    mElement.addAttribute(key, value);
  }

  @Override
  public void setQName(QName qName)
  {
   mElement = mElementFactory.createElement(qName);
  }

  @Override
  public void setResponse(SoapResponse soapResponse)
  {
    SoapResponseImpl response = (SoapResponseImpl)soapResponse;
    mElement = response.mElement;
  }

  @Override
  public SoapResponse createNode(String name)
  {
    return new SoapResponseImpl(mElement.addElement(name), mElementFactory);
  }
}
