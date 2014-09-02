/*
 * ZAL - An abstraction layer for Zimbra.
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

package org.openzal.zal;

import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.account.ZAttrProvisioning;
/* $endif $ */
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;

public class ZEPrefExternalSendersType
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  public static ZEPrefExternalSendersType ALLNOTINAB =
    new ZEPrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.ALLNOTINAB);
  public static ZEPrefExternalSendersType ALL        =
    new ZEPrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.ALL);

  private ZAttrProvisioning.PrefExternalSendersType mValue;
  /* $else $

  public static ZEPrefExternalSendersType ALLNOTINAB = null;
  public static ZEPrefExternalSendersType ALL        = null;

  private Object mValue;
  /* $endif $ */

  /* $if ZimbraVersion >= 8.0.0 $ */
  ZEPrefExternalSendersType(@NotNull ZAttrProvisioning.PrefExternalSendersType value)
  /* $else $
  ZEPrefExternalSendersType(@NotNull Object value)
  /* $endif $ */
  {
    if ( value == null )
    {
      throw new NullPointerException();
    }
    mValue = value;
  }

  public String toString()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mValue.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static ZEPrefExternalSendersType fromString(String s) throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      return new ZEPrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.fromString(s));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public boolean isALLNOTINAB() { return this == ALLNOTINAB;}
  public boolean isALL() { return this == ALL;}

  protected <T> T toZimbra(Class<T> cls)
  {
/* $if ZimbraVersion >= 8.0.0 $ */
    return cls.cast(mValue);
/* $else $
    throw new UnsupportedOperationException();
/* $endif $ */
  }
}
