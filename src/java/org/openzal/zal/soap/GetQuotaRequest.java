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

package org.openzal.zal.soap;

import com.zimbra.soap.admin.message.GetQuotaUsageRequest;
import javax.annotation.Nonnull;

public class GetQuotaRequest
{
  @Nonnull private final GetQuotaUsageRequest mGetQuotaUsageRequest;

  public GetQuotaRequest(
    String domain, Boolean allServers, Integer limit, Integer offset,
    String sortBy, Boolean sortAscending, Boolean refresh
  )
  {
    mGetQuotaUsageRequest = new GetQuotaUsageRequest(
      domain, allServers, limit, offset, sortBy, sortAscending, refresh
    );
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mGetQuotaUsageRequest);
  }

}
