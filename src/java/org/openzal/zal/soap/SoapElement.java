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
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapParseException;
import java.util.List;
import java.util.Set;
import org.dom4j.QName;
import org.openzal.zal.ZimbraListWrapper;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;

public class SoapElement
{
  @Nonnull private final Element mElement;

  public SoapElement(Object element)
  {
    mElement = (Element) element;
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
    catch(ServiceException e)
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
    catch(ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Set<SoapElement.Attribute> listAttributes()
  {
    return ZimbraListWrapper.wrapElementAttributes(mElement.listAttributes());
  }

  public static SoapElement parseXML(String xml)
  {
    try
    {
      return new SoapElement(Element.parseXML(xml));
    }
    catch(ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static SoapElement parseJSON(String json)
  {
    try
    {
      return new SoapElement(Element.parseJSON(json));
    }
    catch(SoapParseException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static SoapElement parseJSON(String json, String qname)
  {
    try
    {
      return new SoapElement(Element.JSONElement.parseJSON(
        json,
        new QName(qname),
        new Element.JSONElement("").getFactory()
      ));
    }
    catch( SoapParseException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<SoapElement> listElements()
  {
    return ZimbraListWrapper.wrapElements(mElement.listElements());
  }

  public String getName()
  {
    return mElement.getName();
  }

  public String getText()
  {
    return mElement.getText();
  }

  public SoapElement getElement(String name)
    throws ServiceException
  {
    return new SoapElement(mElement.getElement(name));
  }

  public boolean hasAttribute(String name)
  {
    for(Attribute attribute : listAttributes())
    {
      if(attribute.getKey().equals(name))
      {
        return true;
      }
    }
    return false;
  }

  public <T> T toZimbra(Class<T> clazz)
  {
    return clazz.cast(mElement);
  }

  @Override
  public String toString()
  {
    return mElement.toString();
  }

  public byte[] toUTF8()
  {
    return mElement.toUTF8();
  }

  public static class Attribute
  {
    @Nonnull private final Element.Attribute mAttribute;

    public Attribute(Object attribute)
    {
      mAttribute = (Element.Attribute) attribute;
    }

    public String getKey()
    {
      return mAttribute.getKey();
    }

    public String getValue()
    {
      return mAttribute.getValue();
    }

    public void setValue(String value)
    {
      mAttribute.setValue(value);
    }
  }

  public String toXML()
  {
    return mElement.toXML().asXML();
  }
}
