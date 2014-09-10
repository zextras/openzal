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
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeInfo;
import com.zimbra.cs.account.AttributeManager;

public class ZEAttributeInfo
{
  private final AttributeInfo mAttributeInfo;

  protected ZEAttributeInfo(AttributeInfo attributeInfo)
  {
    mAttributeInfo = attributeInfo;
  }

  public void checkValue(Object value, boolean checkImmutable)
  {
    try
    {
      /* $if ZimbraVersion > 6.0.7 $ */
      mAttributeInfo.checkValue(value, checkImmutable, null);
      /* $else $
      mAttributeInfo.checkValue(value, checkImmutable);
      /* $endif $ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean isDeprecated()
  {
    /* $if ZimbraVersion >= 7.0.1 $ */
    return mAttributeInfo.isDeprecated();
    /* $else $
    return mAttributeInfo.getDeprecatedSince() != null;
    /* $endif $ */
  }

  public static ZEAttributeInfo getAttributeInfo( String key )
  {
    try
    {
      AttributeInfo attributeInfo = AttributeManager.getInstance().getAttributeInfo(key);
      if (attributeInfo == null)
      {
        return null;
      }
      return new ZEAttributeInfo(attributeInfo);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
