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

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;

public class AdministrationConstants
{
  public static  String GET_QUOTA_USAGE_REQUEST = AdminConstants.E_GET_QUOTA_USAGE_REQUEST;
  public static  String A_OFFSET                = AdminConstants.A_OFFSET;
  public static  String A_LIMIT                 = AdminConstants.A_LIMIT;
  public static  String A_SORT_BY               = AdminConstants.A_SORT_BY;
  public static  String A_SORT_ASCENDING        = AdminConstants.A_SORT_ASCENDING;
  public static  String A_REFRESH               = AdminConstants.A_REFRESH;
  public static  String A_DOMAIN                = AdminConstants.A_DOMAIN;
  public static  String ADMIN_SERVICE_URI       = AdminConstants.ADMIN_SERVICE_URI;
  public static  String A_NAME                  = AdminConstants.A_NAME;
  public static  String A_ID                    = AdminConstants.A_ID;
  public static  String A_QUOTA_USED            = AdminConstants.A_QUOTA_USED;
  public static  String A_QUOTA_LIMIT           = AdminConstants.A_QUOTA_LIMIT;
  public static  String E_ACCOUNT               = AdminConstants.E_ACCOUNT;
  public static final String USER_SERVICE_URI        = AccountConstants.USER_SERVICE_URI;
}
