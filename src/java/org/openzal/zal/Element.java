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

package org.openzal.zal;

import com.zimbra.common.soap.XmlParseException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.io.InputStream;

public class Element
{
  @NotNull private final com.zimbra.common.soap.Element mElement;

  public Element(String name)
  {
    mElement = new com.zimbra.common.soap.Element.XMLElement(name);
  }

  public Element(@NotNull Object element)
  {
    if (element == null)
    {
      throw new NullPointerException();
    }
    mElement = (com.zimbra.common.soap.Element) element;
  }

  public Element clone()
  {
    return new Element(mElement.clone());
  }

  public void addAttribute(String key, Boolean value)
  {
    mElement.addAttribute(key, value);
  }

  public void addAttribute(String key, Integer value)
  {
    mElement.addAttribute(key, value);
  }

  public void addAttribute(String key, String value)
  {
    mElement.addAttribute(key, value);
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mElement);
  }

  public static Element parseXML(String xml)
  {
    try
    {
      return new Element(com.zimbra.common.soap.Element.parseXML(xml));
    }
    catch (XmlParseException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static Element parseXML(InputStream is)
  {
    try
    {
      return new Element(com.zimbra.common.soap.Element.parseXML(is));
    }
    catch (XmlParseException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
