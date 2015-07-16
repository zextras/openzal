/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

public class TargetBy
{
  /* $if MajorZimbraVersion >= 8 $ */
  @NotNull private final com.zimbra.soap.type.TargetBy mTargetBy;
  /* $else $
  private final Object mTargetBy;
  /* $endif $ */

  /* $if MajorZimbraVersion >= 8 $ */
  public static TargetBy id   = new TargetBy(com.zimbra.soap.type.TargetBy.id);
  public static TargetBy name = new TargetBy(com.zimbra.soap.type.TargetBy.name);
  /* $else $
  public static TargetBy id   = null;
  public static TargetBy name = null;
  /* $endif $ */

  TargetBy(@NotNull Object targetBy)
  {
  /* $if MajorZimbraVersion >= 8 $ */
    mTargetBy = (com.zimbra.soap.type.TargetBy) targetBy;
/* $else $
    throw new UnsupportedOperationException();
  /* $endif $ */
  }

  <T> T toZimbra(Class<T> cls)
  {
  /* $if MajorZimbraVersion >= 8 $ */
    return cls.cast(mTargetBy);
  /* $else $
    throw new UnsupportedOperationException();
  /* $endif $ */
  }

  public static TargetBy fromString(String s)
  {
    /* $if MajorZimbraVersion >= 8 $ */
    try
    {
      return new TargetBy(com.zimbra.soap.type.TargetBy.fromString(s));
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
