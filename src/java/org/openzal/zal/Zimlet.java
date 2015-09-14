/*
 * ZAL - The abstraction layer for Zimbra.
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

import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Zimlet extends Entry
{
  @NotNull private final com.zimbra.cs.account.Zimlet mZimlet;

  Zimlet(@NotNull Object zimlet)
  {
    super(zimlet);
    mZimlet = (com.zimbra.cs.account.Zimlet) zimlet;
  }


  public String getName()
  {
    return mZimlet.getName();
  }

  public String getPriority()
  {
    return mZimlet.getPriority();
  }

  public String getAttr(String string1)
  {
    return mZimlet.getAttr(string1);
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    Map<String, Object> attrs = mZimlet.getAttrs(applyDefaults);
    if (attrs != null)
    {
      return attrs;
    }
    return new HashMap<String, Object>();
  }

  @NotNull
  com.zimbra.cs.account.Zimlet toZimbra()
  {
    return mZimlet;
  }
}

