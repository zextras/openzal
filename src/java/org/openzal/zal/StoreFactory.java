package org.openzal.zal;

public interface StoreFactory
{
  // TODO will read from Config
  Store make(String volumeId);
}
