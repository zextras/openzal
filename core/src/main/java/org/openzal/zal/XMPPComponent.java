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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XMPPComponent extends Entry implements Comparable<XMPPComponent>
{
  @Nonnull private final com.zimbra.cs.account.XMPPComponent mXmppComponent;

  protected XMPPComponent(@Nonnull Object xmppComponent)
  {
    super(xmppComponent);
    mXmppComponent = (com.zimbra.cs.account.XMPPComponent) xmppComponent;
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
  public int compareTo(XMPPComponent o)
  {
    return mXmppComponent.compareTo(o.toZimbra(com.zimbra.cs.account.XMPPComponent.class));
  }
}
