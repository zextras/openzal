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

import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

import com.zimbra.soap.type.TargetBy;

public class Targetby
{
  @Nonnull private final TargetBy mTargetBy;

  public static Targetby id   = new Targetby(TargetBy.id);
  public static Targetby name = new Targetby(TargetBy.name);

  Targetby(@Nonnull Object targetBy)
  {
    mTargetBy = (TargetBy) targetBy;
  }

  <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mTargetBy);
  }

  public static Targetby fromString(String s)
  {
    try
    {
      return new Targetby(com.zimbra.soap.type.TargetBy.fromString(s));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
