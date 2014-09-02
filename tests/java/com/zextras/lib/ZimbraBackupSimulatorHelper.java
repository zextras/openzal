package com.zextras.lib;

import com.zextras.backup.ChatBackupHelper;
import com.zextras.backup.ChatBackupHelperImpl;
import com.zextras.license.LicenseManager;
import com.zextras.license.licenses.ConsoleLicense;
import com.zextras.op.backup.ZimbraAccessorFactory;
import org.openzal.zal.ZEAccount;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZimbraBackupSimulatorHelper extends ZimbraSimulatorHelper
{
  protected ChatBackupHelper      mChatBackupHelper;
  protected ZimbraAccessorFactory mZimbraAccessorFactory;
  protected LicenseManager        mLicenseManager;

  public void setup() throws Exception
  {
    super.setup();
    mChatBackupHelper = mock(ChatBackupHelperImpl.class);
    when(mChatBackupHelper.dumpToContainer(Mockito.any(ZEAccount.class))).
      thenReturn(
        new ContainerImpl()
      );

    mLicenseManager = mock(LicenseManager.class);
    when(mLicenseManager.getLicense()).thenReturn(
      new ConsoleLicense()
    );

    mZimbraAccessorFactory = new ZimbraAccessorFactory(
      mMailboxManager,
      mProvisioning,
      mChatBackupHelper,
      mStoreManager,
      mLicenseManager,
      mStoreRoot
    );
  }
}
