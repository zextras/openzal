/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import com.zimbra.common.localconfig.LC;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LocalConfig
{
  @NotNull
  public static List<String> getAllKeys()
  {
    return Arrays.asList(LC.getAllKeys());
  }

  public static String get(String key)
  {
    return LC.get(key);
  }

  public static int getInt(String key)
  {
    return Integer.valueOf(LC.get(key));
  }

  public static void setDefaultTimeZonesFile(String path)
  {
    LC.timezone_file.setDefault(path);
  }
}
