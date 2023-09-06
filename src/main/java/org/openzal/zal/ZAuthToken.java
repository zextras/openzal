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

public class ZAuthToken
{
  @Nonnull private final com.zimbra.common.auth.ZAuthToken mZAuthToken;

  protected ZAuthToken(@Nonnull Object zAuthToken)
  {
    if (zAuthToken == null)
    {
      throw new NullPointerException();
    }
    mZAuthToken = (com.zimbra.common.auth.ZAuthToken) zAuthToken;
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZAuthToken);
  }
}
