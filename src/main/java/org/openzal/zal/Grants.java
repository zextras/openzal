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

import com.zimbra.cs.account.accesscontrol.RightCommand;
import javax.annotation.Nonnull;

import java.util.Set;

public class Grants
{
  @Nonnull private final RightCommand.Grants mGrants;

  protected Grants(@Nonnull Object grants)
  {
    if (grants == null)
    {
      throw new NullPointerException();
    }
    mGrants = (RightCommand.Grants) grants;
  }

  @Nonnull
  public Set<ACE> getACEs()
  {
    return ZimbraListWrapper.wrapACEs(mGrants.getACEs());
  }
}
