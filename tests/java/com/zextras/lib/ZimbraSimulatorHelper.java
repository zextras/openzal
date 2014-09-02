package com.zextras.lib;

import com.zextras.lib.vfs.ZxVFS;
import com.zextras.license.TestClock;
import org.openzal.zal.*;
import com.zimbra.cs.mailbox.ZimbraSimulator;

import java.util.HashMap;

public abstract class ZimbraSimulatorHelper
{
  protected ZimbraSimulator          mZimbraSimulator;
  protected ZimbraConnectionProvider mZimbraConnectionProvider;
  protected ZEProvisioning           mProvisioning;
  protected ZEMailboxManager         mMailboxManager;
  protected ZEAccount                mZimbraAccount;
  protected ZEOperationContext       mOperationContext;
  protected DatabaseAccessor         mDatabaseAccessor;
  protected TestClock                mClock;
  protected ZEStoreManager           mStoreManager;
  protected ZxVFS                    mStoreRoot;

  public void setup() throws Exception
  {
    mZimbraSimulator = new ZimbraSimulator();
    mZimbraConnectionProvider = new ZimbraConnectionProvider();
    mProvisioning = mZimbraSimulator.getProvisioning();
    mMailboxManager = mZimbraSimulator.getMailboxManager();
    mZimbraAccount = mProvisioning.getZimbraUser();
    mStoreManager = mZimbraSimulator.getStoreManager();
    mOperationContext = new ZEOperationContext(mZimbraAccount);
    mProvisioning.createCos("cos", new HashMap<String, Object>());
    mDatabaseAccessor = new DatabaseAccessor(new ZimbraConnectionProvider());
    mStoreRoot = mZimbraSimulator.getStoreRoot();
  }

  public ZEAccount createAccount(String mail, String password, HashMap<String, Object> attrs) throws org.openzal.zal.exceptions.ZimbraException
  {
    return mProvisioning.createAccount(mail, password, attrs);
  }

  public void cleanup() throws Exception
  {
    mZimbraSimulator.cleanup();
  }
}
