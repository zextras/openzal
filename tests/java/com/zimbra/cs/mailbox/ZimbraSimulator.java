package com.zimbra.cs.mailbox;

import com.zextras.lib.log.ZELog;
import com.zextras.lib.vfs.ramvfs.RamFS;
import com.zimbra.cs.store.file.StoreManagerSimulator;
import org.junit.rules.ExternalResource;
import org.openzal.zal.*;
import org.openzal.zal.extension.StoreManagerImpl;
import org.openzal.zal.extension.Zimbra;
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
  private VolumeManager mVolumeManager;
  private StoreManager  mStoreManager;
  private StoreManagerSimulator mStoreSimulator;

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
  }

  /*
    junit @Rule implementation
   */
  protected void before() throws Throwable
  {
  }

  protected void after()
  {
    try
    {
      cleanup();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  Zimbra mZimbra;

  private void init()
  {
    try
    {
      initProperties();
      initIndexing();
      initProvisioning();
      initHSQLDatabase();
      initMailboxManager();
      mZimbra = new Zimbra();
      initStorageManager();

      /* $if ZimbraVersion < 8.0.0 $
      Volume.reloadVolumes();
       $endif$ */

      try
      {
        ScheduledTaskManager.getTask("", "", -1);
      }
      catch (Throwable ex)
      {
        ScheduledTaskManager.startup();
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void initProperties()
    throws ConfigException, DocumentException
  {
    System.setProperty("mail.mime.decodetext.strict",       "false");
    System.setProperty("mail.mime.encodefilename",          "true");
    System.setProperty("mail.mime.charset",                 "utf-8");
    System.setProperty("mail.mime.base64.ignoreerrors",     "true");
    System.setProperty("mail.mime.ignoremultipartencoding", "false");
    System.setProperty("mail.mime.multipart.allowempty",    "true");

    System.setProperty("zimbra.native.required", "false");
    System.setProperty("log4j.configuration", "it/data/zimbra-config/log4j-test.properties");
    System.setProperty("zimbra.config", "it/data/zimbra-config/localconfig-test.xml");

    LC.zimbra_attrs_directory.setDefault("it/data/zimbra-attrs/" + ZimbraVersion.current.toString());
    LC.zimbra_rights_directory.setDefault("it/data/zimbra-rights/" + ZimbraVersion.current.toString());
    ZimbraLog.toolSetupLog4j("INFO", "it/data/zimbra-config/log4j-test.properties");
  }

  private void initMailboxManager() throws Exception
  {
    com.zimbra.cs.mailbox.MailboxManager.setInstance((MailboxManager) Class.forName(LC.zimbra_class_mboxmanager.value()).newInstance());
  }

  private void initIndexing()
  {
    MailboxIndex.startup();
  }

  private void initStorageManager() throws Exception
  {
    //LC.zimbra_class_store.setDefault(StoreManagerSimulator.class.getName());
    //com.zimbra.cs.store.StoreManager.getInstance().startup();
    mVolumeManager = new VolumeManager();
    mStoreSimulator = new StoreManagerSimulator();
    mStoreSimulator.startup();
    mStoreRoot = mStoreSimulator.getStoreRoot();
    FileBlobStoreWrap storeManagerSimulator = new FileBlobStoreSimulatorWrap(mStoreSimulator);
    mStoreManager = new StoreManagerImpl(
      storeManagerSimulator,
      mVolumeManager
    );

    mZimbra.overrideZimbraStoreManager(mStoreManager);
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

  /*
  private static Field                      sVolumeManagerInstance;
  private static Constructor<VolumeManager> sVolumeManagerBuilder;
  private static Field                      sId2Volume;

  static
  {
    try
    {
      sVolumeManagerBuilder = VolumeManager.class.getDeclaredConstructor();
      sVolumeManagerBuilder.setAccessible(true);

      sVolumeManagerInstance = VolumeManager.class.getDeclaredField("SINGLETON");
      sVolumeManagerInstance.setAccessible(true);
      removeFinal(sVolumeManagerInstance);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  private static void removeFinal(Field field) throws NoSuchFieldException, IllegalAccessException
  {
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, sVolumeManagerInstance.getModifiers() & ~Modifier.FINAL);
  }
  */

  public void cleanup() throws Exception
  {
    HSQLZimbraDatabase.clearDatabase();
    mStoreRoot.getRoot().removeContent();
    mZimbra.restoreZimbraStoreManager();
    mStoreRoot.emptyRamFS();
    //sVolumeManagerInstance.set(null, sVolumeManagerBuilder.newInstance());
    //((StoreManagerSimulator) com.zimbra.cs.store.StoreManager.getInstance()).shutdown();
  }

  public Provisioning getProvisioning() throws Exception
  {
    return new ProvisioningImp(com.zimbra.cs.account.Provisioning.getInstance());
  }

  public MockProvisioning getMockProvisioning()
  {
    return (MockProvisioning) com.zimbra.cs.account.Provisioning.getInstance();
  }

  public org.openzal.zal.MailboxManager getMailboxManager() throws Exception
  {
    return new MailboxManagerImp(com.zimbra.cs.mailbox.MailboxManager.getInstance());
  }

  public StoreManager getStoreManager()
  {
    return mStoreManager;
  }

  public VolumeManager getVolumeManager()
  {
    return mVolumeManager;
  }

  public Zimbra getZimbra()
  {
    return mZimbra;
  }

  public void useMVCC(org.openzal.zal.Mailbox mbox) throws Exception
  {
    HSQLZimbraDatabase.useMVCC(mbox.toZimbra(Mailbox.class));
  }
}