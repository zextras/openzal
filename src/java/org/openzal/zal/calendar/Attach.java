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

package org.openzal.zal.calendar;

public class Attach
{
  public static Attach fromUnencodedAndContentType(byte[] rawBytes, String contentType)
  {
    {
      return new Attach(com.zimbra.common.calendar.Attach.fromUnencodedAndContentType(rawBytes, contentType));
    }
  }

  public static Attach fromEncodedAndContentType(byte[] binaryB64Data, String contentType)
  {
    {
      return new Attach(com.zimbra.common.calendar.Attach.fromEncodedAndContentType(binaryB64Data, contentType));
    }
  }

  public static Attach fromEncodedAndContentType(String binaryB64Data, String contentType)
  {
    {
      return new Attach(com.zimbra.common.calendar.Attach.fromEncodedAndContentType(binaryB64Data, contentType));
    }
  }

  public static Attach fromUriAndContentType(String uri, String contentType)
  {
    {
      return new Attach(com.zimbra.common.calendar.Attach.fromUriAndContentType(uri, contentType));
    }
  }

  private com.zimbra.common.calendar.Attach mZAttach;

  public Attach(Object zAttach)
  {
    {
      mZAttach = (com.zimbra.common.calendar.Attach) zAttach;
    }
  }

  public String getUri()
  {
    {
      return mZAttach.getUri();
    }
  }

  public String getFileName()
  {
    {
      return mZAttach.getFileName();
    }
  }

  public void setFileName(String fileName)
  {
    {
      mZAttach.setFileName(fileName);
    }
  }

  public String getContentType()
  {
    {
      return mZAttach.getContentType();
    }
  }

  public void setContentType(String contentType)
  {
    {
      mZAttach.setContentType(contentType);
    }
  }

  public String getBinary64Data()
  {
    {
      return mZAttach.getBinaryB64Data();
    }
  }

  public byte[] getDecodedData()
  {
    {
      return mZAttach.getDecodedData();
    }
  }

  public <T> T toZimbra(Class<T> targetClass)
  {
    {
      return targetClass.cast(mZAttach);
    }
  }
}