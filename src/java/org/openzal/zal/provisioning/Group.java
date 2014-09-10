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

import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Group
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final com.zimbra.cs.account.Group mGroup;
  /* $endif $ */

  public Group(@NotNull Object group)
  {
    if ( group == null )
    {
      throw new NullPointerException();
    }
  /* $if ZimbraVersion >= 8.0.0 $ */
    mGroup = (com.zimbra.cs.account.Group) group;
  /* $else $
    throw new UnsupportedOperationException();
   $endif $ */

  }

  public Set<String> getAllMembersSet()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      return mGroup.getAllMembersSet();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

}
