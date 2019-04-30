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

import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.common.account.ZAttrProvisioning;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

public class PrefExternalSendersType
{
  @Nonnull public static PrefExternalSendersType ALLNOTINAB =
    new PrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.ALLNOTINAB);
  @Nonnull public static PrefExternalSendersType ALL        =
    new PrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.ALL);

  private ZAttrProvisioning.PrefExternalSendersType mValue;

  PrefExternalSendersType(@Nonnull ZAttrProvisioning.PrefExternalSendersType value)
  {
    if (value == null)
    {
      throw new NullPointerException();
    }
    mValue = value;
  }

  public String toString()
  {
    return mValue.toString();
  }

  @Nonnull
  public static PrefExternalSendersType fromString(String s)
    throws ZimbraException
  {
    try
    {
      return new PrefExternalSendersType(ZAttrProvisioning.PrefExternalSendersType.fromString(s));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isALLNOTINAB() { return this == ALLNOTINAB;}
  public boolean isALL() { return this == ALL;}

  protected <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mValue);
  }
}
