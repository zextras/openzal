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

import org.openzal.zal.lib.Version;
import org.jetbrains.annotations.NotNull;

public class ZimletDescription
{
  @NotNull private final com.zimbra.cs.zimlet.ZimletDescription mZimletDescription;

  protected ZimletDescription(@NotNull Object zimletDescription)
  {
    if (zimletDescription == null)
    {
      throw new NullPointerException();
    }
    mZimletDescription = (com.zimbra.cs.zimlet.ZimletDescription) zimletDescription;
  }

  public String getName()
  {
    return mZimletDescription.getName();
  }

  @NotNull
  public Version getVersion()
  {
    String version = mZimletDescription.getVersion().toString();
    return new Version(version);
  }
}
