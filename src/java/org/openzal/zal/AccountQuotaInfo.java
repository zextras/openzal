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

public class AccountQuotaInfo
{
  private final String mId;
  private final String mName;
  private final long   mQuotaLimit;
  private final long   mQuotaUsed;

  public AccountQuotaInfo(String id, String name, long quotaLimit, long quotaUsed)
  {
    mQuotaLimit = quotaLimit;
    mQuotaUsed = quotaUsed;
    mId = id;
    mName = name;
  }

  public long getQuotaLimit()
  {
    return mQuotaLimit;
  }

  public String getId()
  {
    return mId;
  }

  public String getName()
  {
    return mName;
  }

  public long getQuotaUsed()
  {
    return mQuotaUsed;
  }
}
