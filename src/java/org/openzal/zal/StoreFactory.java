package org.openzal.zal;

import com.zextras.lib.Container;

public interface StoreFactory
{
  // TODO will read from Config
  Store make(String volumeId);
}
