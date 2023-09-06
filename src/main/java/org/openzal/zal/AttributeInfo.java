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

import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

public class AttributeInfo
{
  private final com.zimbra.cs.account.AttributeInfo mAttributeInfo;

  protected AttributeInfo(com.zimbra.cs.account.AttributeInfo attributeInfo)
  {
    mAttributeInfo = attributeInfo;
  }

  public void checkValue(Object value, boolean checkImmutable)
  {
    try
    {
      mAttributeInfo.checkValue(value, checkImmutable, null);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isDeprecated()
  {
    return mAttributeInfo.isDeprecated();
  }

  @Nullable
  public static AttributeInfo getAttributeInfo( String key )
  {
    try
    {
      com.zimbra.cs.account.AttributeInfo attributeInfo =
        com.zimbra.cs.account.AttributeManager.getInstance().getAttributeInfo(key);
      if (attributeInfo == null)
      {
        return null;
      }
      return new AttributeInfo(attributeInfo);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
