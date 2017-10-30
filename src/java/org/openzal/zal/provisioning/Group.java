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

package org.openzal.zal.provisioning;

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class Group
{
  @NotNull private final com.zimbra.cs.account.Group mGroup;

  public Group(@NotNull Object group)
  {
    if (group == null)
    {
      throw new NullPointerException();
    }
    mGroup = (com.zimbra.cs.account.Group) group;
  }

  @NotNull
  public Set<String> getAllMembersSet()
  {
    try
    {
      return mGroup.getAllMembersSet();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getName()
  {
    return mGroup.getName();
  }

  public Collection<String> getAliases()
  {
    try
    {
      return Arrays.asList(mGroup.getAliases());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
