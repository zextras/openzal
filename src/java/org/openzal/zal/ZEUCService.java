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

import org.jetbrains.annotations.NotNull;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.cs.account.UCService;
/* $else$
import com.zimbra.cs.account.Entry;
 $endif $ */

import java.util.HashSet;
import java.util.Set;

public class ZEUCService extends ZEEntry
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final UCService mUCService;

  protected ZEUCService(@NotNull Object ucService)
  {
    super(ucService);
    mUCService = (UCService)ucService;
  }

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mUCService.getMultiAttrSet(name));
  }

  public String getName()
  {
    return mUCService.getName();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mUCService);
  }
/* $else$

  protected ZEUCService(@NotNull Object ucService)
  {
    super(ucService);
    throw new UnsupportedOperationException();
  }

  public Set<String> getMultiAttrSet(String name)
  {
    throw new UnsupportedOperationException();
  }

  public String getName()
  {
    throw new UnsupportedOperationException();
  }

  public <T> T toZimbra(Class<T> cls)
  {
    throw new UnsupportedOperationException();
  }
  $endif $ */

}