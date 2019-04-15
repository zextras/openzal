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

import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

import java.util.*;


public class DistributionList extends Group
{
  @Nonnull private final com.zimbra.cs.account.DistributionList mDistributionList;

  DistributionList(@Nonnull Object distributionList)
  {
    super(distributionList);
    mDistributionList = (com.zimbra.cs.account.DistributionList) distributionList;
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

  public void removeMembers(@Nullable Collection<String> members)
  {
    if (members != null)
    {
      try
      {
        mDistributionList.removeMembers(members.toArray(new String[members.size()]));
      }
      catch (ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
  }

  public void setPrefAllowAddressForDelegatedSender(@Nonnull Collection<String> zimbraPrefAllowAddressForDelegatedSender)
  {
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
  }

  @Nonnull
  public Collection<String> getPrefAllowAddressForDelegatedSender()
  {
    return Arrays.asList(mDistributionList.getPrefAllowAddressForDelegatedSender());
  }

  @Nonnull
  com.zimbra.cs.account.DistributionList toZimbra()
  {
    return mDistributionList;
  }
}

