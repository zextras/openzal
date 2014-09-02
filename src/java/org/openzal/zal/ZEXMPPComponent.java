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

import com.zimbra.cs.account.XMPPComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ZEXMPPComponent extends ZEEntry implements Comparable<ZEXMPPComponent>
{
  private final XMPPComponent mXmppComponent;

  protected ZEXMPPComponent(@NotNull Object xmppComponent)
  {
    super(xmppComponent);
    mXmppComponent = (XMPPComponent)xmppComponent;
  }

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mXmppComponent.getMultiAttrSet(name));
  }

  public String getName()
  {
    return mXmppComponent.getName();
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mXmppComponent);
  }

  @Override
  public int compareTo(ZEXMPPComponent o)
  {
    return mXmppComponent.compareTo(o.toZimbra(XMPPComponent.class));
  }
}
