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


import java.io.IOException;
import java.util.Collection;

public interface StoreManager
{
  void register(CacheableStoreBuilder cacheableStoreBuilder, String volumeId);
  void unregister(String volumeId);
  void makeActive(String volumeId);
  void startup() throws IOException;
  void shutdown();
  PrimaryStore getPrimaryStore();
  Store getStore(String locator);
  Store getStoreByName(String name);
  Collection<Store> getAllStores();
  void setPrimaryStoreBuilder(PrimaryStoreBuilder primaryStoreBuilder);
}
