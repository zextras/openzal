package org.openzal.zal;

public interface StoreAccessorFactory
{
  // TODO will read from Config
  StoreAccessor make(String volumeId);
}
