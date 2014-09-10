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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

public class Config extends Entry
{
  private final com.zimbra.cs.account.Config mConfig;

  public Config(@NotNull Object config)
  {
    super(config);
    mConfig = (com.zimbra.cs.account.Config) config;
  }

  public Config(HashMap<String, Object> hashMap, Provisioning provisioning)
  {
    this(
      new com.zimbra.cs.account.Config(
        hashMap,
        provisioning.toZimbra(com.zimbra.cs.account.Provisioning.class)
      )
    );
  }

  public String getVersionCheckNotificationEmail()
  {
    return mConfig.getVersionCheckNotificationEmail();
  }

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mConfig.getMultiAttrSet(name));
  }

  public String getAttr(String name)
  {
    return mConfig.getAttr(name);
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mConfig.getAttrs(applyDefaults));
  }

  public String getAttr(String name, String defaultValue)
  {
    return mConfig.getAttr(name, defaultValue);
  }

}

