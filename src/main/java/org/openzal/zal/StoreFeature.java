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

import com.zimbra.cs.store.StoreManager;
import javax.annotation.Nonnull;

public enum StoreFeature
{
  BULK_DELETE(StoreManager.StoreFeature.BULK_DELETE),
  CENTRALIZED(StoreManager.StoreFeature.CENTRALIZED),
  RESUMABLE_UPLOAD(StoreManager.StoreFeature.RESUMABLE_UPLOAD),
  SINGLE_INSTANCE_SERVER_CREATE(StoreManager.StoreFeature.SINGLE_INSTANCE_SERVER_CREATE),
  CUSTOM_STORE_API(StoreManager.StoreFeature.CUSTOM_STORE_API);

  private final StoreManager.StoreFeature mStoreFeature;

  StoreFeature(Object storeFeature)
  {
    mStoreFeature = (StoreManager.StoreFeature) storeFeature;
  }

  protected <T> T toZimbra(@Nonnull  Class<T> className)
  {
    if (mStoreFeature == null)
    {
      return null;
    }

    return className.cast(mStoreFeature);
  }

  public static StoreFeature fromZimbra(Object storeFeature)
  {
    switch ((StoreManager.StoreFeature) storeFeature)
    {
      case BULK_DELETE:
        return BULK_DELETE;
      case CENTRALIZED:
        return CENTRALIZED;
      case RESUMABLE_UPLOAD:
        return RESUMABLE_UPLOAD;
      case SINGLE_INSTANCE_SERVER_CREATE:
        return SINGLE_INSTANCE_SERVER_CREATE;
      case CUSTOM_STORE_API:
        return CUSTOM_STORE_API;
      default:
        throw new UnsupportedOperationException("Unsupported feature " + storeFeature.toString());
    }
  }
}
