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
import org.jetbrains.annotations.NotNull;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.ldap.IAttributes;
/* $endif $ */

import java.util.List;

public class ZEIAttributes
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final IAttributes mIAttributes;

  protected ZEIAttributes(@NotNull Object iattributes)
  {
    if ( iattributes == null )
    {
      throw new NullPointerException();
    }
    mIAttributes = (IAttributes)iattributes;
  }

  /* $else $
  private final Object mIAttributes;

  protected ZEIAttributes(@NotNull Object iattributes)
  {
    throw new UnsupportedOperationException();
  }
  /* $endif $ */



  public List<String> getMultiAttrStringAsList(String attrName, boolean checkbinaryBoolean)
  {
  /* $if ZimbraVersion >= 8.0.0 $ */
    IAttributes.CheckBinary checkBinary;
    if (checkbinaryBoolean)
    {
      checkBinary = IAttributes.CheckBinary.CHECK;
    }
    else
    {
      checkBinary = IAttributes.CheckBinary.NOCHECK;
    }
    try
    {
      return mIAttributes.getMultiAttrStringAsList(attrName, checkBinary);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  /* $else $
    throw new UnsupportedOperationException();
  /* $endif $ */
  }

  public String getAttrString(String attrName)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      return mIAttributes.getAttrString(attrName);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
      throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
