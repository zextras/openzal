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

package org.openzal.zal.lib;

import com.zimbra.cs.util.BuildInfo;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ZimbraVersion extends Version
{
  public static String BUILDNUM = BuildInfo.BUILDNUM;
  public static String HOST     = BuildInfo.HOST;
  public static String DATE     = BuildInfo.DATE;
  public static String PLATFORM = BuildInfo.PLATFORM;
  public static String FULL_VERSION = BuildInfo.FULL_VERSION;


  private ZimbraVersion(Version v) {
    super(v.getMajor(), Optional.of(v.getMinor()), v.getPatch());
  }

  public ZimbraVersion(int major, int minor, int micro)
  {
    super(major, Optional.of(minor), Optional.of(String.valueOf(micro)));
  }

  public static ZimbraVersion current = new ZimbraVersion(
    Integer.parseInt(BuildInfo.MAJORVERSION),
    Integer.parseInt(BuildInfo.MINORVERSION),
    Integer.parseInt(BuildInfo.MICROVERSION)
  );

  public static void restoreVersion()
  {
    current = new ZimbraVersion(
      Integer.parseInt(BuildInfo.MAJORVERSION),
      Integer.parseInt(BuildInfo.MINORVERSION),
      Integer.parseInt(BuildInfo.MICROVERSION)
    );
  }

  public static ZimbraVersion parse(String v) throws NumberFormatException {
    return new ZimbraVersion(Version.parse(v));
  }
}
