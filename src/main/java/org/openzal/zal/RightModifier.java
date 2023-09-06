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

import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;

public class RightModifier
{
  @Nonnull private final com.zimbra.cs.account.accesscontrol.RightModifier mRightModifier;

  RightModifier(
    @Nonnull com.zimbra.cs.account.accesscontrol.RightModifier rightModifier
  )
  {
    if (rightModifier == null)
    {
      throw new NullPointerException();
    }

    mRightModifier = rightModifier;
  }

  com.zimbra.cs.account.accesscontrol.RightModifier toZimbra()
  {
    return mRightModifier;
  }

  public char getModifier() {
    return mRightModifier.getModifier();
  }

  public static RightModifier fromChar(char modifier) {
    try {
      return new RightModifier(com.zimbra.cs.account.accesscontrol.RightModifier.fromChar(modifier));
    } catch (ServiceException e) {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
