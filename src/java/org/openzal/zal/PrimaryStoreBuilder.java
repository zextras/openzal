package org.openzal.zal;

public interface PrimaryStoreBuilder
{
  PrimaryStore build(FileBlobStoreWrap fileBlobStore, StoreVolume storeVolume);
}
