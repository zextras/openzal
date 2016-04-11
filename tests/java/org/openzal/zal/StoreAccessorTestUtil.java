package org.openzal.zal;

import com.zimbra.cs.store.file.StoreManagerSimulator;

public class StoreAccessorTestUtil extends ZimbraStoreWrap
{
  public StoreAccessorTestUtil()
  {
    super(com.zimbra.cs.store.StoreManager.getInstance(), new VolumeManager());
  }

  public FileBlobStoreWrap getFileBlobStore()
  {
    return new FileBlobStoreSimulatorWrap((StoreManagerSimulator) com.zimbra.cs.store.StoreManager.getInstance());
  }
}
