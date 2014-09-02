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

import org.openzal.zal.ZEQuotaUsage;
import org.openzal.zal.ZimbraListWrapper;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.soap.SoapProvisioning;
import java.util.Collection;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.soap.admin.message.GetQuotaUsageRequest;
import com.zimbra.soap.admin.message.GetQuotaUsageResponse;
/* $endif $ */

public class ZESoapProvisioning
{
  public static ZEGetQuotaUsageResponse invokeJaxb(ZEGetQuotaUsageRequest getQuotaUsageRequest, String server)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      return new ZEGetQuotaUsageResponse(
        (GetQuotaUsageResponse) SoapProvisioning.getAdminInstance().invokeJaxb(
          getQuotaUsageRequest.toZimbra(GetQuotaUsageRequest.class),
          server
        )
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public static Collection<ZEQuotaUsage> getQuotaUsage(String server)
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      return ZimbraListWrapper.wrapQuotaUsages(SoapProvisioning.getAdminInstance().getQuotaUsage(server));
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
