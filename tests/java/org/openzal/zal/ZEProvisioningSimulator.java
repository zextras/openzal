package org.openzal.zal;

import java.util.*;

import org.openzal.zal.exceptions.ZimbraException;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.accesscontrol.RightModifier;
import com.zimbra.cs.account.auth.AuthContext;

/* $if MajorZimbraVersion >= 8 $ */
/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $else$
 import com.zimbra.common.account.Key.GranteeBy;
/* $endif$ */

import com.zimbra.common.account.Key;
import com.zimbra.soap.admin.type.CacheEntryType;
import com.zimbra.soap.type.TargetBy;
import org.mockito.Mockito;
/* $else$
import org.mockito.Mockito;
import com.zimbra.cs.account.Provisioning.CacheEntryType;
$endif$ */

public class ZEProvisioningSimulator extends ZEProvisioning
{
  private Map<String, ZEDomain> mDomainMap;
  private Map<String, ZEAccount> mAccountMap;

  /*************** Simulator methods ********************/
  public ZEProvisioningSimulator()
  {
    super(null);
    mDomainMap = new HashMap<String, ZEDomain>();
    mAccountMap = new HashMap<String, ZEAccount>();
  }

  public void addDomain(String domain)
  {
    if (mDomainMap.containsKey(domain))
    {
      return;
    }
    mDomainMap.put(domain, createFakeDomain(domain));
  }

  public void addUserWithAliases(String address, List<String> aliases)
  {
    addUser(address);
    ZEAccount account = mAccountMap.get(address);

    for (String alias : aliases)
    {
      mAccountMap.put(alias, account);
    }
  }

  public ZEAccount addUser(String address)
  {
    return addUser(address, address);
  }

  public ZEAccount addUser(String name, String address)
  {
    if (mAccountMap.containsKey(address))
    {
      return mAccountMap.get(address);
    }

    int domainIdx = address.indexOf('@');
    if (domainIdx == -1)
    {
      throw new RuntimeException("Invalid address " + address);
    }

    String domain = address.substring(domainIdx + 1);
    addDomain(domain);

    ZEAccount account = createFakeAccount(name, address);
    mAccountMap.put(address, account);

    return account;
  }

  public ZEDomain createFakeDomain(String domain)
  {
    Map<String,Object> attrs = new HashMap<String, Object>();
    Map<String, Object> defaults = new HashMap<String, Object>();

    return new ZEDomain(domain,domain,attrs,defaults,this);
  }

  public ZEAccount createFakeAccount(String accountStr)
  {
    return createFakeAccount(accountStr, accountStr);
  }

  public ZEAccount createFakeAccount(String name, String accountStr)
  {
    return createFakeAccount(name,accountStr,Collections.<String,Object>emptyMap());
  }

  public ZEAccount createFakeAccount(String name, String accountStr, Map<String,Object> extraAttr )
  {
    if( name == null ) {
      name = "mockito";
    }

    if( accountStr == null ) {
      accountStr = "mockito@example.com";
    }

    Map<String,Object> attrs = new HashMap<String, Object>();
    attrs.put(Provisioning.A_mail, accountStr);

    Map<String, Object> defaults = new HashMap<String, Object>();
    defaults.put(Provisioning.A_zimbraAccountStatus,
                 Provisioning.ACCOUNT_STATUS_ACTIVE);
    defaults.put(Provisioning.A_displayName,
                 accountStr);

    return new AccountSimulator(
      name,
      accountStr,
      attrs,
      defaults,
      this
    );
  }

  public ZEAccount getAccountByName(String accountStr)
  {
    return mAccountMap.get(accountStr);
  }

  public ZEDomain getDomainByName( String domain )
  {
    return mDomainMap.get(domain);
  }

  public boolean onLocalServer(ZEAccount userAccount)
    throws ZimbraException {
    return true;
  }

  public Provisioning toZimbra()
  {
    return Mockito.mock(Provisioning.class);
  }

  /******************************************************/

  public ZEAccount getZimbraUser()
    throws ZimbraException {
    throw new RuntimeException("Provisioning method not implemented.");
  }

  public ZEOperationContext createZContext()
    throws ZimbraException {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEDistributionList getDistributionListById(String id)
    throws ZimbraException {
/* $if MajorZimbraVersion >= 8 $ */
    throw new RuntimeException("Provisioning method not implemented");
/* $else$
    throw new RuntimeException("Provisioning method not implemented");
   $endif$ */
  }

  public ZEDistributionList getDistributionListByName(String name)
    throws ZimbraException {
/* $if MajorZimbraVersion >= 8 $ */
    throw new RuntimeException("Provisioning method not implemented");
/* $else$
    throw new RuntimeException("Provisioning method not implemented");
   $endif$ */
  }

  public void visitAllAccounts( NamedEntry.Visitor visitor )
      throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    throw new RuntimeException("Provisioning method not implemented");
    /* $else$
    throw new RuntimeException("Provisioning method not implemented");
    $endif$ */
  }

  public void visitAllDomains(NamedEntry.Visitor visitor)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public void visitDomain(NamedEntry.Visitor visitor, ZEDomain domain)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public void authAccount(ZEAccount account, String password, AuthContext.Protocol protocol, Map<String, Object> context)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEAccount getAccountById(String accountId)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented("+accountId+")");
  }

  public ZEServer getLocalServer()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public List<NamedEntry> searchAccountsOnServer(ZEServer localServer, SearchAccountsOptions searchOpts)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  /* $endif$ */

  public List<ZEDomain> getAllDomains()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEZimlet getZimlet(String zimletName)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEDomain getDomainById(String domainId)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public List<ZEDistributionList> getAllDistributionLists(ZEDomain domain)
    throws ZimbraException
  {
    return Collections.emptyList();
  }

  public ZECos getCosById(String cosId)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public List<ZECos> getAllCos()
      throws ZimbraException
  {
    return Collections.emptyList();
  }


  public ZECos getCosByName(String cosStr)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

/* $if MajorZimbraVersion < 8 $
  public List<NamedEntry> searchAccounts(String query, String returnAttrs[], String sortAttr, boolean sortAscending, int flags) throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
 $endif$ */

  /* $if MajorZimbraVersion >= 8 $ */
  public List<ZEAccount> searchDirectory(SearchDirectoryOptions opts)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public void searchDirectory(SearchDirectoryOptions opts, NamedEntry.Visitor visitor)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEDistributionList get(Key.DistributionListBy id, String dlStr)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  /* $endif$ */

  public List<ZEAccount> getAllAdminAccounts()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public List<ZEAccount> getAllAccounts(ZEDomain domain)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public List<ZEServer> getAllServers()
      throws ZimbraException
  {
    return Collections.singletonList(
      new ZEServer(
        "localhost",
        "",
        new HashMap<String, Object>(),
        new HashMap<String, Object>(),
        new ZEProvisioning(mProvisioning)
      ));
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public List<ZECalendarResource> getAllCalendarResources(ZEDomain domain)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  /* $else$
  public List getAllCalendarResources(Domain domain)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  $endif$ */

  public List<ZEZimlet> listAllZimlets()
      throws ZimbraException
  {
    return Collections.emptyList();
  }

  public List<ZEXMPPComponent> getAllXMPPComponents()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEGlobalGrant getGlobalGrant()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEConfig getConfig()
      throws ZimbraException
  {
    return new ZEConfig(
      new HashMap<String, Object>(){{
        put("key", "value");
      }},
      new ZEProvisioning(mProvisioning)
    );
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public List<ZEUCService> getAllUCServices()
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  /* $endif$ */

  public ZECalendarResource getCalendarResourceByName(String resourceName)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZECalendarResource getCalendarResourceById(String resourceId)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEDomain createDomain(String currentDomainName, HashMap<String, Object> stringObjectHashMap)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZECos createCos(String cosname, HashMap<String, Object> stringObjectHashMap)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEDistributionList createDistributionList(String dlistName, HashMap<String, Object> stringObjectHashMap)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEAccount createCalendarResource(String dstAccount, String newPassword, Map<String, Object> attrs)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEAccount createAccount(String dstAccount, String newPassword, Map<String, Object> attrs)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public void modifyIdentity(ZEAccount newAccount, String identityName, Map<String, Object> newAttrs)
      throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  /* $if MajorZimbraVersion == 8 $ */
  public void grantRight(String targetType, TargetBy targetBy, String target,
                         String granteeType, GranteeBy granteeBy, String grantee, String secret,
                         String right, RightModifier rightModifier) throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }
  /* $endif$ */

  public ZEDomain getDomain(ZEAccount account)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public void flushCache(CacheEntryType account, Provisioning.CacheEntry[] cacheEntries)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  public ZEProvisioning.ZECountAccountResult countAccount(ZEDomain domain)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }


  public ZEServer getServer(ZEAccount acct)
    throws ZimbraException
  {
    throw new RuntimeException("Provisioning method not implemented");
  }

  /******************************************************/
  public static class AccountSimulator extends ZEAccount
  {
    public AccountSimulator(String name,
                            String id,
                            Map<String, Object> attrs,
                            Map<String, Object> defaults,
                            ZEProvisioning prov)
    {
      super(name, id, attrs, defaults, prov);
    }

    @Override
    public String getId()
    {
      return getName();
    }

    private void setAttr( String key, Object value )
    {
      Map<String,Object> attrs = getAttrs();
      attrs.put(key, value);
      setAttrs(attrs);
    }

    @Override
    public void setIsDelegatedAdminAccount(boolean value)
    {
      setAttr(Provisioning.A_zimbraIsDelegatedAdminAccount, String.valueOf(value).toUpperCase());
    }


    @Override
    public String getDisplayName()
    {
      return getName();
    }

    @Override
    public void authAccount(String password, Protocol proto)
    {}

    @Override
    public String getAccountStatus(ZEProvisioning prov)
    {
      return "test status";
    }
  }

  public ZECos getCOS(ZEAccount acct) throws ZimbraException
  {
    return new ZECos("test", "id", new HashMap<String, Object>(), this);
  }
}
