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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SoapResponseSimple implements SoapResponse
{
  @NotNull private final HashMap<String, Object> mMap;

  public SoapResponseSimple()
  {
    mMap = new HashMap<String, Object>();
  }

  @Override
  public void setValue(String key, String value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setValue(String key, boolean value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setValue(String key, long value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setQName(QName qName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setResponse(SoapResponse soapResponse)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SoapResponse createNode(String name)
  {
    throw new UnsupportedOperationException();
  }

  public Object getAttribute(String responses)
  {
    return mMap.get(responses);
  }
}
