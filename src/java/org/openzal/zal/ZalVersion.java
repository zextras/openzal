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

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lib.Version;

public class ZalVersion
{
  public static final    Version current = new Version(ZalBuildInfo.VERSION);
  @NotNull public static Version target  = new Version(8, 0, 7);

  static
  {
    String implementationVersion = ZalVersion.class.getPackage().getImplementationVersion();
    if (implementationVersion != null && !implementationVersion.isEmpty())
    {
      target = new Version(implementationVersion);
    }
  }

  public static void main(String args[])
  {
    System.out.println("zal_version: " + current.toString());
    System.out.println("zal_commit: " + ZalBuildInfo.COMMIT);
    System.out.println("target_zimbra_version: " + target.toString());

    System.exit(0);
  }
}
