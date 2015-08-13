package org.openzal.zal;

import com.zimbra.cs.store.file.StoreManagerSimulator;

public class StoreManagerTestUtil extends StoreManagerImp
{
  public StoreManagerTestUtil()
  {
    super(com.zimbra.cs.store.StoreManager.getInstance());
  }

  public FileBlobStoreWrap getFileBlobStore()
  {
    return new FileBlobStoreSimulatorWrap((StoreManagerSimulator) com.zimbra.cs.store.StoreManager.getInstance());
  }
}
