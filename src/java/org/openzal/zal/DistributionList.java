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

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.AlreadyExistsException;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class DistributionList extends Entry
{
  @NotNull private final com.zimbra.cs.account.DistributionList mDistributionList;

  DistributionList(@NotNull Object distributionList)
  {
    super(distributionList);
    mDistributionList = (com.zimbra.cs.account.DistributionList) distributionList;
  }

  @NotNull
  public Collection<String> getAliases()
  {
    try
    {
      return Arrays.asList(mDistributionList.getAliases());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public Set<String> getAllMembersSet()
  {
    Set<String> set;
    try
    {
      set = new HashSet<String>(mDistributionList.getAllMembersSet());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return set;
  }

  public void addMembers(@Nullable Collection<String> members)
  {
    if (members != null)
    {
      try
      {
        mDistributionList.addMembers(members.toArray(new String[members.size()]));
      }
      catch (ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
  }

  public void setPrefAllowAddressForDelegatedSender(@NotNull Collection<String> zimbraPrefAllowAddressForDelegatedSender)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      mDistributionList.setPrefAllowAddressForDelegatedSender(
        zimbraPrefAllowAddressForDelegatedSender.toArray(new String[zimbraPrefAllowAddressForDelegatedSender.size()])
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  @NotNull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mDistributionList.getMultiAttrSet(name));
  }

  @NotNull
  public Collection<String> getPrefAllowAddressForDelegatedSender()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return Arrays.asList(mDistributionList.getPrefAllowAddressForDelegatedSender());
    /* $else $
    return Collections.emptyList();
    /* $endif $ */
  }

  public String getId()
  {
    return mDistributionList.getId();
  }

  public String getName()
  {
    return mDistributionList.getName();
  }

  public void addAlias(String alias) throws AlreadyExistsException
  {
    try
    {
      mDistributionList.addAlias(alias);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @NotNull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mDistributionList.getAttrs(applyDefaults));
  }

  @NotNull
  com.zimbra.cs.account.DistributionList toZimbra()
  {
    return mDistributionList;
  }
}

