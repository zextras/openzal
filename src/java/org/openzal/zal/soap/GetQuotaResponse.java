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

package org.openzal.zal.soap;

import org.openzal.zal.AccountQuotaInfo;
import org.openzal.zal.ZimbraListWrapper;
import com.zimbra.cs.service.admin.GetQuotaUsage;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.soap.admin.message.GetQuotaUsageResponse;
/* $endif $ */

import java.util.List;

public class GetQuotaResponse
{
  /* $if ZimbraVersion >= 8.0.0 $ */
  private final GetQuotaUsageResponse mGetQuotaUsageResponse;
  /* $endif $ */

  public static final String SORT_TOTAL_USED = GetQuotaUsage.SORT_TOTAL_USED;
  public static final String SORT_QUOTA_LIMIT = GetQuotaUsage.SORT_QUOTA_LIMIT;
  public static final String SORT_PERCENT_USED = GetQuotaUsage.SORT_PERCENT_USED;
  public static final String SORT_ACCOUNT = GetQuotaUsage.SORT_ACCOUNT;

  protected GetQuotaResponse(Object getQuotaUsageResponse)
  {
  /* $if ZimbraVersion >= 8.0.0 $ */
    mGetQuotaUsageResponse = (GetQuotaUsageResponse)getQuotaUsageResponse;
  /* $else $
    throw new UnsupportedOperationException();
  /* $endif $ */
  }

  public List<AccountQuotaInfo> getAccountQuotas()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return ZimbraListWrapper.wrapAccountQuotaInfos(mGetQuotaUsageResponse.getAccountQuotas());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
