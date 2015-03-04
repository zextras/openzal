package com.zimbra.cs.mailbox;

import com.zextras.lib.Error.UnableToRegisterDatabaseDriverError;
import com.zextras.lib.log.ZELog;
import com.zextras.lib.vfs.ramvfs.RamFS;
import org.junit.rules.ExternalResource;
import org.openzal.zal.*;
import org.openzal.zal.lib.ZimbraVersion;
import org.openzal.zal.Provisioning;

import com.zimbra.common.localconfig.ConfigException;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.MockProvisioning;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.db.HSQLZimbraDatabase;
import com.zimbra.cs.ldap.ZLdapFilterFactorySimulator;
import com.zimbra.cs.store.StoreManagerSimulator;
import org.dom4j.DocumentException;

/* $if ZimbraVersion >= 8.0.0 $ */
/* $else$
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.store.file.Volume;
import com.zimbra.cs.index.MailboxIndex;
/* $endif$ */


// for testing purpose only
public class ZimbraSimulator extends ExternalResource
{
  private final StoreManagerImp mStoreManager;

  public RamFS getStoreRoot()
  {
    return mStoreRoot;
  }

  private RamFS mStoreRoot;

  public ZimbraSimulator()
  {
    try
    {
      Class.forName("org.hsqldb.jdbcDriver");
    }
    catch (Exception e)
    {
      ZELog.chat.err("Error loading DB Driver: " + Utils.exceptionToString(e));
      throw new RuntimeException(e);
    }

    init();

    mStoreManager = new StoreManagerImp(
      com.zimbra.cs.store.StoreManager.getInstance()
    );
  }

/*
  junit @Rule implementation
 */
  protected void before() throws Throwable {
  }

  protected void after() {
    try
    {
      cleanup();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void init()
  {
    try
    {
      initProperties();
      initIndexing();
      initStorageManager();
      initProvisioning();
      initHSQLDatabase();
      initMailboxManager();

      /* $if ZimbraVersion < 8.0.0 $
      Volume.reloadVolumes();
       $endif$ */

      ScheduledTaskManager.startup();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void initProperties()
    throws ConfigException, DocumentException
  {
    System.setProperty("zimbra.native.required", "false");
    System.setProperty("log4j.configuration", "it/data/zimbra-config/log4j-test.properties");
    System.setProperty("zimbra.config", "it/data/zimbra-config/localconfig-test.xml");

    LC.zimbra_attrs_directory.setDefault("it/data/zimbra-attrs/" + ZimbraVersion.current.toString());
    ZimbraLog.toolSetupLog4j("INFO", "it/data/zimbra-config/log4j-test.properties");
  }

  private void initMailboxManager() throws ServiceException
  {
    LC.zimbra_class_mboxmanager.setDefault(MailboxManager.class.getName());
  }

  private void initIndexing()
  {
    MailboxIndex.startup();
  }

  private void initStorageManager() throws Exception
  {
    LC.zimbra_class_store.setDefault(StoreManagerSimulator.class.getName());
    com.zimbra.cs.store.StoreManager.getInstance().startup();
    mStoreRoot = ((StoreManagerSimulator) com.zimbra.cs.store.StoreManager.getInstance()).getStoreRoot();
  }

  private void initProvisioning() throws Exception
  {
    com.zimbra.cs.account.Provisioning.setInstance(new MockProvisioning());
/* $if ZimbraVersion >= 8.0.0 $*/
    ZLdapFilterFactorySimulator.setInstance();
/* $endif $*/
  }

  public void initHSQLDatabase() throws Exception
  {
    LC.zimbra_class_database.setDefault(HSQLZimbraDatabase.class.getName());
    DbPool.startup();
    HSQLZimbraDatabase.createDatabase();
  }

  public void cleanup() throws Exception
  {
    HSQLZimbraDatabase.clearDatabase();
  }

  public Provisioning getProvisioning() throws Exception
  {
    return new Provisioning(com.zimbra.cs.account.Provisioning.getInstance());
  }

  public MockProvisioning getMockProvisioning()
  {
    return (MockProvisioning)com.zimbra.cs.account.Provisioning.getInstance();
  }

  public org.openzal.zal.MailboxManager getMailboxManager() throws Exception
  {
    return new org.openzal.zal.MailboxManager(com.zimbra.cs.mailbox.MailboxManager.getInstance());
  }

  public StoreManager getStoreManager()
  {
    return mStoreManager;
  }
}