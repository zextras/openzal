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

import java.util.HashMap;
import java.util.Map;

public class Signature
{
  @Nonnull private final com.zimbra.cs.account.Signature mSignature;

  protected Signature(@Nonnull Object signature)
  {
    if (signature == null)
    {
      throw new NullPointerException();
    }
    mSignature = (com.zimbra.cs.account.Signature) signature;
  }

  public String getId()
  {
    return mSignature.getId();
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mSignature.getAttrs(applyDefaults));
  }

  public String getAttr(String name)
  {
    return mSignature.getAttr(name);
  }
}

