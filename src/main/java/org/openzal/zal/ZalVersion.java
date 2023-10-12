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
import org.openzal.zal.lib.Version;
import org.openzal.zal.lib.ZimbraVersion;
import org.openzal.zal.log.ZimbraLog;

public class ZalVersion
{
  public static final    Version current = Version.parse(BuildProperties.getProjectVersion());
  @Nonnull public static Version target;

  static
  {
    String implementationVersion = ZalVersion.class.getPackage().getImplementationVersion();
    if (implementationVersion != null && !implementationVersion.isEmpty())
    {
      target = Version.parse(implementationVersion);
    }
  }

  public static void checkCompatibility()
  {
    if (!ZimbraVersion.current.equals(ZalVersion.target)) {
      if (BuildProperties.isDevBuild()) {
        ZimbraLog.extensions.warn("Carbonio version mismatch - ZAL built for Carbonio: " + ZalVersion.target + " (dev build)");
      } else {
        throw new RuntimeException("Carbonio version mismatch - ZAL built for Carbonio: " + ZalVersion.target.toString());
      }
    }
  }

  public static boolean isZimbraX()
  {
    return false;
  }

  public static void main(String args[])
  {
    System.out.println("zal_version: " + current.toString());
    System.out.println("zal_commit: " + BuildProperties.getCommitFull());
    System.out.println("target_zimbra_version: " + target.toString());
    if (BuildProperties.isDevBuild()) {
      System.out.println("dev build");
    }

    System.exit(0);
  }
}
