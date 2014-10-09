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

package org.openzal.zal.provisioning;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum TargetType
{
  account("account"),
  calresource("calresource"),
  cos("cos"),
  dl("dl"),
  domain("domain"),
  server("server"),
  xmppcomponent("xmppcomponent"),
  zimlet("zimlet"),
  config("config"),
  group("group"),
  global("global");

  @NotNull
  private static Map<String, TargetType> TARGET_TYPES = new HashMap<String, TargetType>()
  {{
      put("account", account);
      put("calresource", calresource);
      put("cos", cos);
      put("dl", dl);
      put("domain", domain);
      put("server", server);
      put("xmppcomponent", xmppcomponent);
      put("zimlet", zimlet);
      put("config", config);
      put("group", group);
      put("global", global);
    }};

  @NotNull
  private final String mType;

  TargetType(@NotNull String type)
  {
    mType = type;
  }

  @NotNull
  public String getCode()
  {
    return mType;
  }

  public static TargetType fromString(String type)
  {
    TargetType targetType = TARGET_TYPES.get(type);
    if( targetType == null ) {
      throw new RuntimeException();
    }
    return targetType;
  }
}
