/*
 * ZAL - An abstraction layer for Zimbra.
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

package org.openzal.zal.soap;

/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.soap.admin.message.GetQuotaUsageRequest;
/* $endif $ */

public class ZEGetQuotaUsageRequest
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final GetQuotaUsageRequest mGetQuotaUsageRequest;
  /* $else $
  private final Object mGetQuotaUsageRequest;
  /* $endif $ */

  public ZEGetQuotaUsageRequest(
    String domain, Boolean allServers, Integer limit, Integer offset,
    String sortBy, Boolean sortAscending, Boolean refresh)
  {
  /* $if ZimbraVersion >= 8.0.0 $ */
    mGetQuotaUsageRequest = new GetQuotaUsageRequest(
      domain, allServers, limit, offset, sortBy, sortAscending, refresh
    );
  /* $else $
    mGetQuotaUsageRequest = null;
  /* $endif $ */
  }

  protected <T> T toZimbra(Class<T> cls)
  {
/* $if ZimbraVersion >= 8.0.0 $ */
    return cls.cast(mGetQuotaUsageRequest);
/* $else $
    throw new UnsupportedOperationException();
   $endif $ */
  }

}
