package org.openzal.zal;

import com.zimbra.cs.store.file.StoreManagerSimulator;

public class StoreManagerTestImp extends StoreManagerImp
{
  public StoreManagerTestImp()
  {
    super(com.zimbra.cs.store.StoreManager.getInstance());
  }

  public FileBlobStoreWrap getFileBlobStore()
  {
    return new FileBlobStoreSimulatorWrap((StoreManagerSimulator) com.zimbra.cs.store.StoreManager.getInstance());
  }
}
