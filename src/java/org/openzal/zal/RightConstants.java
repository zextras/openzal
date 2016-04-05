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

import com.zimbra.cs.account.accesscontrol.generated.RightConsts;

public class RightConstants
{
  public static String RT_sendAs = RightConsts.RT_sendAs;
  public static String RT_domainAdminCosRights = RightConsts.RT_domainAdminCosRights;
  public static String RT_loginAs = RightConsts.RT_loginAs;
  public static String RT_domainAdminRights = RightConsts.RT_domainAdminRights;
  public static String RT_adminLoginAs = RightConsts.RT_adminLoginAs;
  public static String RT_configureQuota = RightConsts.RT_configureQuota;
  public static String RT_domainAdminConsoleAccountsFeaturesTabRights =
    RightConsts.RT_domainAdminConsoleAccountsFeaturesTabRights;
  public static String RT_listZimlet = RightConsts.RT_listZimlet;
  public static String RT_getZimlet = RightConsts.RT_getZimlet;
  public static String RT_domainAdminZimletRights = RightConsts.RT_domainAdminZimletRights;
  public static String RT_setAdminSavedSearch = RightConsts.RT_setAdminSavedSearch;
  public static String RT_viewAdminSavedSearch = RightConsts.RT_viewAdminSavedSearch;
  public static String RT_domainAdminConsoleDLSharesTabRights = RightConsts.RT_domainAdminConsoleDLSharesTabRights;
  /* $if ZimbraVersion >= 8.0.0 $ */
  public static String RT_sendOnBehalfOf = RightConsts.RT_sendOnBehalfOf;
  public static String RT_sendOnBehalfOfDistList = RightConsts.RT_sendOnBehalfOfDistList;
  public static String RT_sendAsDistList = RightConsts.RT_sendAsDistList;
  /* $else $
  public static String RT_sendOnBehalfOf = null;
  public static String RT_sendOnBehalfOfDistList = null;
  public static String RT_sendAsDistList = null;
  /* $endif $ */
}
