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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import org.jetbrains.annotations.NotNull;

public class ZEConfig extends ZEEntry
{
  private final Config mConfig;

  public ZEConfig(@NotNull Object config)
  {
    super(config);
    mConfig = (Config)config;
  }

  public ZEConfig(HashMap<String, Object> hashMap, ZEProvisioning provisioning)
  {
    this(
      new Config(
        hashMap,
        provisioning.toZimbra(Provisioning.class)
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

