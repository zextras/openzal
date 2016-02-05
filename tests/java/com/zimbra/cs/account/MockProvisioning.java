package com.zimbra.cs.account;

import java.util.UUID;

import org.openzal.zal.ProvisioningImp;
import org.openzal.zal.redolog.MockRedoLogProvider;
/**
 * Mock implementation of {@link ProvisioningImp} for testing.
 *
 * @author ysasaki
 * Zimbra Collaboration Suite Server
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.zimbra.cs.mime.handler.TextEnrichedHandler;
import com.zimbra.cs.mime.handler.TextHtmlHandler;
import com.zimbra.cs.mime.handler.TextPlainHandler;
import com.zimbra.cs.mime.handler.UnknownTypeHandler;

/* $if MajorZimbraVersion >= 8 $ */
import com.zimbra.common.account.Key;
import com.zimbra.common.account.ProvisioningConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.mime.MockMimeTypeInfo;
import com.zimbra.soap.admin.type.CacheEntryType;
import com.zimbra.soap.admin.type.DataSourceType;

/* $elseif MajorZimbraVersion >= 6 $
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource.Type;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.account.auth.AuthContext.Protocol;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.mime.MockMimeTypeInfo;
import com.zimbra.cs.mime.handler.UnknownTypeHandler;

/* $if MajorZimbraVersion >= 7 $
import com.zimbra.cs.account.auth.AuthContext;
/* $endif $ */
/* $endif $ */

public final class MockProvisioning extends com.zimbra.cs.account.Provisioning
{
  public static final String DEFAULT_ACCOUNT_ID = new UUID(0L, 0L).toString();

  private final Map<String, Account>            id2account   = new HashMap<String, Account>();
  private final Map<String, Account>            name2account = new HashMap<String, Account>();
  private final Map<String, Domain>             id2domain    = new HashMap<String, Domain>();
  private final Map<String, Domain>             name2domain  = new HashMap<String, Domain>();
  private final Map<String, Cos>                id2cos       = new HashMap<String, Cos>();
  private final Map<String, Cos>                name2cos     = new HashMap<String, Cos>();
  private final Map<String, List<MimeTypeInfo>> mimeConfig   = new HashMap<String, List<MimeTypeInfo>>();
  private final Map<String, DistributionList>   id2Dlist     = new HashMap<String, DistributionList>();
  private final Map<String, Zimlet>             id2zimlets   = new HashMap<String, Zimlet>();

  private final Config                    config        = new Config(new HashMap<String, Object>()
  {{
      put("confKey", "configurationValue");
    }}, this);
  /* $if MajorZimbraVersion >= 8 $ */
  private final Map<String, ShareLocator> shareLocators = new HashMap<String, ShareLocator>();

  /* $endif $ */
  private final Server localhost;
  private final Cos    mDefaultCos;

  private int mCounter = 1;

  public MockProvisioning()
  {
    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(A_zimbraServiceHostname, "localhost");
    attrs.put(A_zimbraRedoLogProvider, MockRedoLogProvider.class.getName());
    attrs.put(A_zimbraId, UUID.randomUUID().toString());
    attrs.put(A_zimbraMailMode, MailMode.http.toString());
    attrs.put(A_zimbraSmtpPort, "7025");
    localhost = new Server("localhost", "localhost", attrs, Collections.<String, Object>emptyMap(), this);
    mDefaultCos = new Cos("test", "id", new HashMap<String, Object>(), this);

    initMimeTypes();

    HashMap<String, Object> zimbraAttrs = new HashMap<String, Object>();
    zimbraAttrs.put(A_zimbraId, org.openzal.zal.Provisioning.ZIMBRA_USER_ID);
    zimbraAttrs.put(A_zimbraIsAdminAccount, "TRUE");
    try
    {
      createAccount("zimbra", "", zimbraAttrs);
    }
    catch (ServiceException e)
    {
      throw new RuntimeException(e);
    }
  }

  private void initMimeTypes()
  {
    MockMimeTypeInfo mime = new MockMimeTypeInfo();
    mime.setMimeTypes("text/html");
    mime.setFileExtensions("html", "htm");
    mime.setHandlerClass(TextHtmlHandler.class.getName());
    addMimeType("text/html", mime);

    mime = new MockMimeTypeInfo();
    mime.setMimeTypes("text/plain");
    mime.setFileExtensions("txt", "text");
    mime.setHandlerClass(TextPlainHandler.class.getName());
    addMimeType("text/plain", mime);

    mime = new MockMimeTypeInfo();
    mime.setMimeTypes("text/enriched");
    mime.setFileExtensions("txe");
    mime.setHandlerClass(TextEnrichedHandler.class.getName());
    addMimeType("text/enriched", mime);

    mime = new MockMimeTypeInfo();
    mime.setMimeTypes("not/exist");
    mime.setFileExtensions("NotExist");
    mime.setHandlerClass("com.zimbra.cs.mime.handler.NotExist");
    addMimeType("not/exist", mime);

    mime = new MockMimeTypeInfo();
    mime.setHandlerClass(UnknownTypeHandler.class.getName());
    addMimeType("all", mime);
  }

/* $if ZimbraVersion >= 8.5.0 $ */

  public List<AlwaysOnCluster> getAllAlwaysOnClusters()  throws ServiceException
  {
    throw new RuntimeException();
  }

  public AlwaysOnCluster createAlwaysOnCluster(String name, Map<String, Object> attrs) throws ServiceException
  {
    throw new RuntimeException();
  }

  public AlwaysOnCluster get(Key.AlwaysOnClusterBy keyname, String key) throws ServiceException
  {
    throw new RuntimeException();
  }

  public void deleteAlwaysOnCluster(String zimbraId) throws ServiceException
  {
    throw new RuntimeException();
  }
/* $endif$ */


  /* $if MajorZimbraVersion >= 8 $ */
  public Account createAccount(String name)
    throws ServiceException
  {
    return createAccount(name, "", new HashMap<String, Object>());
  }

  public Account createAccount(String email, String password, Map<String, Object> attrs)
    throws ServiceException
  {
    validate(ProvisioningValidator.CREATE_ACCOUNT, email, null, attrs);
    attrs.put("password",password);

    if (!attrs.containsKey(A_zimbraId))
    {
      attrs.put(A_zimbraId, UUID.randomUUID().toString());
      //attrs.put(A_zimbraId, DEFAULT_ACCOUNT_ID);
    }
    if (!attrs.containsKey(A_zimbraMailHost))
    {
      attrs.put(A_zimbraMailHost, "localhost");
    }
    if (!attrs.containsKey(A_zimbraAccountStatus))
    {
      attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
    }
    if (!attrs.containsKey(A_zimbraDumpsterEnabled))
    {
      attrs.put(A_zimbraDumpsterEnabled, TRUE);
    }
    attrs.put(A_zimbraBatchedIndexingSize, Integer.MAX_VALUE); // suppress indexing
    Account account = new Account(email, email, attrs, null, this);
    try
    {
      name2account.put(email, account);
      id2account.put(account.getId(), account);
      return account;
    }
    finally
    {
      validate(ProvisioningValidator.CREATE_ACCOUNT_SUCCEEDED, email, account);
    }
  }

  /* $else $
  public List<Zimlet> getObjectTypes() {
    return Collections.emptyList();
  }

  public List<NamedEntry> searchAccounts(String query, String[] returnAttrs,
                                         String sortAttr, boolean sortAscending, int flags) {
    throw new UnsupportedOperationException();
  }

  public List<NamedEntry> searchCalendarResources(EntrySearchFilter filter,
                                                  String[] returnAttrs, String sortAttr, boolean sortAscending) {
    throw new UnsupportedOperationException();
  }

  public List<NamedEntry> searchAccounts(Domain d, String query,
                                         String[] returnAttrs, String sortAttr, boolean sortAscending,
                                         int flags) {
    throw new UnsupportedOperationException();
  }

  public List<NamedEntry> searchDirectory(SearchOptions options) {
    List<Account> accounts = getAllAccounts(null);
    List<NamedEntry> namedEntries = new ArrayList<NamedEntry>();
    String searchId = null;
    String query = options.getQuery();
    if ( query != null && query.startsWith("("+Provisioning.A_zimbraId+"=") )
    {
      searchId = query.replace("(" + Provisioning.A_zimbraId + "=", "").replace(")", "");
    }
    for (Account account : accounts)
    {
      if (searchId == null || searchId.equals(account.getId()))
      {
        namedEntries.add(account);
      }
    }
    return namedEntries;
  }

  public void searchAccountsOnServer(Server server, Provisioning.SearchOptions searchOptions, NamedEntry.Visitor visitor)
   throws ServiceException
  {
    for( Account account : getAllAccounts(null))
    {
      String serverName = account.getAttr(Provisioning.A_zimbraMailHost);
      if ( serverName != null && ! serverName.equals("localhost"))
      {
        continue;
      }
      visitor.visit(account);
    }
  }

$if ZimbraVersion >= 7.0.0$
  public SearchGalResult searchGal(Domain d, String query,
                                   GalSearchType type, String token) {
    throw new UnsupportedOperationException();
  }

  public SearchGalResult autoCompleteGal(Domain d, String query,
                                         GalSearchType type, int limit) {
    throw new UnsupportedOperationException();
  }
$else $
    public SearchGalResult searchGal(Domain d, String query, GAL_SEARCH_TYPE type, String token) throws ServiceException
    {
      throw new UnsupportedOperationException();
    }
    public SearchGalResult autoCompleteGal(Domain d, String query, Provisioning.GAL_SEARCH_TYPE type, int limit) throws ServiceException
    {
      throw new UnsupportedOperationException();
    }
$endif $

  public List<NamedEntry> searchCalendarResources(Domain d,
                                                  EntrySearchFilter filter, String[] returnAttrs, String sortAttr,
                                                  boolean sortAscending) {
    throw new UnsupportedOperationException();
  }

  private void addBasicAccountAttrs( Map<String,Object> attrs )
  {
    if (!attrs.containsKey(A_zimbraId))
    {
      attrs.put(A_zimbraId, UUID.randomUUID().toString());
      //attrs.put(A_zimbraId, DEFAULT_ACCOUNT_ID);
    }
    if (!attrs.containsKey(A_zimbraMailHost))
    {
      attrs.put(A_zimbraMailHost, "localhost");
    }
    if (!attrs.containsKey(A_zimbraAccountStatus))
    {
      attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
    }
    attrs.put(A_zimbraBatchedIndexingSize, Integer.MAX_VALUE); // suppress indexing
  }

  $if MajorZimbraVersion >= 7 $
  public Account createAccount(
    String email,
    String password,
    Map<String, Object> attrs
  )
    throws ServiceException
  {
    validate(ProvisioningValidator.CREATE_ACCOUNT, email, null, attrs);

    addBasicAccountAttrs( attrs );
    attrs.put("password",password);
    Account account = new Account(email, email, attrs, null, this);
    try {
      name2account.put(email, account);
      id2account.put(account.getId(), account);
      return account;
    } finally {
      validate(ProvisioningValidator.CREATE_ACCOUNT_SUCCEEDED, email, account);
    }
  }
  $else $
  public Account createAccount(String email, String password,
                               Map<String, Object> attrs) throws ServiceException
  {

    // validate("createAccount", email, Collections.emptySet(), attrs);
    // validate("createAccountCheckDomainCosAndFeature", email, attrs);

    addBasicAccountAttrs( attrs );
    attrs.put("password",password);

    Account account = new Account(email, email, attrs, null, this);
    try {
      name2account.put(email, account);
      id2account.put(account.getId(), account);
      return account;
    } finally {
      // validate("createAccountSucceeded", email, account);
    }
  }
  $endif $
  $endif $ */

  /* $if MajorZimbraVersion >= 8 $ */
  public Account get(Key.AccountBy keyType, String key)
  /* $else $
  public Account get(AccountBy keyType, String key)
  /* $endif $ */
  {
    switch (keyType)
    {
      case name:
        return name2account.get(key);
      case id:
      default:
        return id2account.get(key);
    }
  }

  /* $if MajorZimbraVersion >= 8 $ */
  @Override
  public List<MimeTypeInfo> getMimeTypes(String mime)
  {
    List<MimeTypeInfo> result = mimeConfig.get(mime);
    if (result != null)
    {
      return result;
    }
    else
    {
      MockMimeTypeInfo info = new MockMimeTypeInfo();
      info.setHandlerClass(UnknownTypeHandler.class.getName());
      return Collections.<MimeTypeInfo>singletonList(info);
    }
  }
  /* $else $
  public List<MimeTypeInfo> getMimeTypes(String mime) {
    return mimeConfig.get(mime);
  }
  /* $endif $ */

  @Override
  public List<MimeTypeInfo> getAllMimeTypes()
  {
    List<MimeTypeInfo> result = new ArrayList<MimeTypeInfo>();
    for (List<MimeTypeInfo> entry : mimeConfig.values())
    {
      result.addAll(entry);
    }
    return result;
  }

  public void addMimeType(String mime, MimeTypeInfo info)
  {
    List<MimeTypeInfo> list = mimeConfig.get(mime);
    if (list == null)
    {
      list = new ArrayList<MimeTypeInfo>();
      mimeConfig.put(mime, list);
    }
    list.add(info);
  }

  private void initializeMimeHandlers()
  {

  }

  public void clearMimeHandlers()
  {
    mimeConfig.clear();
  }

  public Config getConfig()
  {
    return config;
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public void modifyAttrs(Entry entry, Map<String, ? extends Object> attrs, boolean checkImmutable)
  {
    Map<String, Object> map = entry.getAttrs(false);
    for (Map.Entry<String, ? extends Object> attr : attrs.entrySet())
    {
      if (attr.getValue() != null)
      {
        Object value = attr.getValue();
        if (value instanceof List)
        { // Convert list to string array.
          List<?> list = (List<?>) value;
          String[] strArray = new String[list.size()];
          for (int i = 0; i < list.size(); i++)
          {
            strArray[i] = list.get(i).toString();
          }
          value = strArray;
        }
        map.put(attr.getKey(), value);
      }
      else
      {
        map.remove(attr.getKey());
      }
    }
  }
  /* $else $
  public void modifyAttrs(Entry entry, Map<String, ? extends Object> attrs,
                          boolean checkImmutable) {

    Map<String, Object> map = entry.getAttrs(false);
    for (Map.Entry<String, ? extends Object> attr : attrs.entrySet()) {
      if (attr.getValue() != null) {
        map.put(attr.getKey(), attr.getValue());
      } else {
        map.remove(attr.getKey());
      }
    }
  }
  /* $endif $ */


  public Server getLocalServer()
  {
    return localhost;
  }

  public void modifyAttrs(Entry e, Map<String, ? extends Object> attrs,
                          boolean checkImmutable, boolean allowCallback)
  {
    throw new UnsupportedOperationException();
  }

  public void reload(Entry e)
  {
  }

  public boolean inDistributionList(Account acct, String zimbraId)
  {
    throw new UnsupportedOperationException();
  }

  public Set<String> getDistributionLists(Account acct)
  {
    throw new UnsupportedOperationException();
  }

  public Set<String> getDirectDistributionLists(Account acct)
    throws ServiceException
  {
    throw new UnsupportedOperationException();
  }

  public List<DistributionList> getDistributionLists(Account acct,
                                                     boolean directOnly, Map<String, String> via)
  {
    return Collections.<DistributionList>emptyList();
  }

  public List<DistributionList> getDistributionLists(DistributionList list,
                                                     boolean directOnly, Map<String, String> via)
  {
    throw new UnsupportedOperationException();
  }

  public boolean healthCheck()
  {
    throw new UnsupportedOperationException();
  }

  public GlobalGrant getGlobalGrant()
  {
    return null;
  }

  public Account restoreAccount(String emailAddress, String password,
                                Map<String, Object> attrs, Map<String, Object> origAttrs)
  {
    throw new UnsupportedOperationException();
  }

  public void deleteAccount(String zimbraId)
  {
    Account account = id2account.remove(zimbraId);
    if (account != null)
    {
      name2account.remove(account.getName());
    }
  }

  public void renameAccount(String zimbraId, String newName)
  {
    throw new UnsupportedOperationException();
  }

  public List<Account> getAllAdminAccounts()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Cos getCOS(Account acct) throws ServiceException
  {
    return mDefaultCos;
  }

  public void setCOS(Account acct, Cos cos)
  {
    throw new UnsupportedOperationException();
  }

  public void modifyAccountStatus(Account acct, String newStatus)
  {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public void authAccount(Account acct, String password, AuthContext.Protocol proto)
  /* $else $
  public void authAccount(Account acct, String password, Protocol proto)
  /* $endif $ */
  {
    if( password.equals(acct.getAttr("password")) ) {
      return;
    }
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public void authAccount(Account acct, String password, AuthContext.Protocol proto, Map<String, Object> authCtxt)
  /* $else $
  public void authAccount(Account acct, String password, Protocol proto, Map<String, Object> authCtxt)
  /* $endif $ */
  {
    if( password.equals(acct.getAttr("password")) ) {
      return;
    }
    throw new UnsupportedOperationException();
  }

  public void preAuthAccount(Account acct, String accountName, String accountBy, long timestamp, long expires,
                             String preAuth, Map<String, Object> authCtxt) {
    throw new UnsupportedOperationException();
  }

  public void changePassword(Account acct, String currentPassword, String newPassword) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public SetPasswordResult setPassword(Account acct, String newPassword) {
    throw new UnsupportedOperationException();
  }

  public void ssoAuthAccount(Account acct, AuthContext.Protocol proto, Map<String, Object> authCtxt) {
    throw new UnsupportedOperationException();
  }

  /* $elseif ZimbraVersion >= 7.0.1 $
  public void ssoAuthAccount(Account acct, AuthContext.Protocol proto, Map<String, Object> authCtxt) {
    throw new UnsupportedOperationException();
  }

  public SetPasswordResult setPassword(Account acct, String newPassword) {
    throw new UnsupportedOperationException();
  }
  $else $
  public void setPassword(Account acct, String newPassword) {
    throw new UnsupportedOperationException();
  }
  $endif $ */


  public void checkPasswordStrength(Account acct, String password) {
    throw new UnsupportedOperationException();
  }

  public void addAlias(Account acct, String alias) {
    try
    {
      ArrayList<String> list = new ArrayList<String>(Arrays.asList(acct.getMailAlias()));
      list.add(alias);
      acct.setMailAlias(list.toArray(new String[0]));
    }
    catch (ServiceException e)
    {
      throw new RuntimeException(e);
    }
  }

  public void removeAlias(Account acct, String alias) {
    throw new UnsupportedOperationException();
  }

  public Domain createDomain(String name, Map<String, Object> attrs) throws ServiceException {
    name = name.trim().toLowerCase();
    if (name2domain.get(name) != null)
    {
      throw AccountServiceException.DOMAIN_EXISTS(name);
    }

    String id = (String) attrs.get(A_zimbraId);
    if (id == null) {
      attrs.put(A_zimbraId, id = UUID.randomUUID().toString());
    }
    if (!attrs.containsKey(A_zimbraSmtpHostname)) {
      attrs.put(A_zimbraSmtpHostname, "localhost");
    }

    Domain domain = new Domain(name, id, attrs, null, this){
      public String getGalSearchBase(String searchBaseSpec) throws ServiceException {
        return searchBaseSpec;
      }
    };
    id2domain.put(id, domain);
    name2domain.put(name, domain);
    return domain;
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Domain get(Key.DomainBy keyType, String key)
  /* $else $
  public Domain get(DomainBy keyType, String key)
  /* $endif $ */
  {
    switch (keyType) {
      case id:
        return id2domain.get(key);

      case name:
        for (Domain domain : id2domain.values()) {
          if (domain.getName().equals(key)) {
            return domain;
          }
        }
        break;
    }

    return null;
  }

  public List<Domain> getAllDomains() {
    return new ArrayList<Domain>(id2domain.values());
  }

  public void getAllDomains(NamedEntry.Visitor visitor, String[] retAttrs) throws ServiceException
  {
    for( Domain domain : getAllDomains() )
    {
      visitor.visit(domain);
    }
  }

  public void deleteDomain(String zimbraId) {
    id2domain.remove(zimbraId);
  }

  public Cos createCos(String name, Map<String, Object> attrs) throws ServiceException {
    name = name.trim().toLowerCase();
    if (name2cos.get(name) != null) {
      throw AccountServiceException.COS_EXISTS(name);
    }

    String id = (String) attrs.get(A_zimbraId);
    if (id == null) {
      attrs.put(A_zimbraId, id = UUID.randomUUID().toString());
    }
    attrs.put("cn", name);

    Cos cos = new Cos(name, id, attrs, this);
    id2cos.put(id, cos);
    name2cos.put(name, cos);
    return cos;
  }

  public Cos copyCos(String srcCosId, String destCosName) {
    throw new UnsupportedOperationException();
  }

  public void renameCos(String zimbraId, String newName) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Cos get(Key.CosBy keyType, String key)
  /* $else $
  public Cos get(CosBy keyType, String key)
  /* $endif $ */
  {
    switch (keyType) {
      case id:
        return id2cos.get(key);

      case name:
        for (Cos cos : id2cos.values()) {
          if (cos.getName().equals(key)) {
            return cos;
          }
        }
        break;
    }

    return null;
  }

  public List<Cos> getAllCos() {
    return new ArrayList<Cos>(id2cos.values());
  }

  public void deleteCos(String zimbraId) {
    throw new UnsupportedOperationException();
  }

  public Server createServer(String name, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Server get(Key.ServerBy keyName, String key)
  /* $else $
  public Server get(ServerBy keyName, String key)
  /* $endif $ */
  {
    switch (keyName) {
      case id:
        return localhost.getId().equals(key) ? localhost : null;
      case name:
        return localhost.getName().equals(key) ? localhost : null;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public List<Server> getAllServers() {
    return Arrays.asList(localhost);
  }

  public List<Server> getAllServers(String service) {
    throw new UnsupportedOperationException();
  }
  public List<Server> getAllServers(String service, String clusterId)
  {
    throw new UnsupportedOperationException();
  }


  public void deleteServer(String zimbraId) {
    throw new UnsupportedOperationException();
  }

  public DistributionList createDistributionList(String name, Map<String, Object> listAttrs) throws AccountServiceException
  {
     /* $if MajorZimbraVersion >= 8 $ */
    DistributionList list = new DistributionList(name, name, listAttrs, this) {};
    id2Dlist.put(name, list);
    return list;
    /* $else $
    throw new UnsupportedOperationException();
    $endif $ */
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public DistributionList get(Key.DistributionListBy keyType, String key)
  /* $else $
  public DistributionList get(DistributionListBy keyType, String key)
  /* $endif $ */
  {
    return id2Dlist.get(key);
  }

  public void deleteDistributionList(String zimbraId) {
    throw new UnsupportedOperationException();
  }

  public void addAlias(DistributionList dl, String alias) {
    throw new UnsupportedOperationException();
  }

  public void removeAlias(DistributionList dl, String alias) {
    throw new UnsupportedOperationException();
  }

  public void renameDistributionList(String zimbraId, String newName) {
    throw new UnsupportedOperationException();
  }

  public Zimlet getZimlet(String name) {
    throw new UnsupportedOperationException();
  }

  public List<Zimlet> listAllZimlets() {
    return new ArrayList<Zimlet>(id2zimlets.values());
  }

  public Zimlet createZimlet(String name, Map<String, Object> attrs) {

    Zimlet zimlet = new Zimlet(name, name, attrs, this);

    id2zimlets.put(name,zimlet);
    return zimlet;
  }

  public void deleteZimlet(String name) {
    throw new UnsupportedOperationException();
  }

  public CalendarResource createCalendarResource(String emailAddress, String password, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void deleteCalendarResource(String zimbraId) {
    throw new UnsupportedOperationException();
  }

  public void renameCalendarResource(String zimbraId, String newName) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public CalendarResource get(Key.CalendarResourceBy keyType, String key)
  /* $else $
  public CalendarResource get(CalendarResourceBy keyType, String key)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  public List<Account> getAllAccounts(Domain d) {
    return new ArrayList<Account>(name2account.values());
  }

  public void getAllAccounts(Domain d, NamedEntry.Visitor visitor) {
    throw new UnsupportedOperationException();
  }

  public void getAllAccounts(Domain d, Server s, NamedEntry.Visitor visitor) {
    throw new UnsupportedOperationException();
  }

  public List<?> getAllCalendarResources(Domain d) {
    throw new UnsupportedOperationException();
  }

  public void getAllCalendarResources(Domain d, NamedEntry.Visitor visitor) {
    throw new UnsupportedOperationException();
  }

  public void getAllCalendarResources(Domain d, Server s, NamedEntry.Visitor visitor) {
    throw new UnsupportedOperationException();
  }

  public List<?> getAllDistributionLists(Domain d) {
    return new ArrayList(id2Dlist.values());
  }

  public void addMembers(DistributionList list, String[] members) {
    throw new UnsupportedOperationException();
  }

  public void removeMembers(DistributionList list, String[] member) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Identity getDefaultIdentity(Account account) {
    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(A_zimbraPrefIdentityName, ProvisioningConstants.DEFAULT_IDENTITY_NAME);
    attrs.put(A_zimbraPrefIdentityId, account.getId());
    return new Identity(account, ProvisioningConstants.DEFAULT_IDENTITY_NAME, account.getId(), attrs, this);
  }
  /* $endif $ */

  public Identity createIdentity(Account account, String identityName, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public Identity restoreIdentity(Account account, String identityName, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void modifyIdentity(Account account, String identityName, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void deleteIdentity(Account account, String identityName) {
    throw new UnsupportedOperationException();
  }

  public List<Identity> getAllIdentities(Account account) {
    return Collections.<Identity>emptyList();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Identity get(Account account, Key.IdentityBy keyType, String key)
  /* $else $
  public Identity get(Account account, IdentityBy keyType, String key)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  public Signature createSignature(Account account, String signatureName, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public Signature restoreSignature(Account account, String signatureName, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void modifySignature(Account account, String signatureId, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void deleteSignature(Account account, String signatureId) {
    throw new UnsupportedOperationException();
  }

  public List<Signature> getAllSignatures(Account account) {
    return Collections.<Signature>emptyList();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public Signature get(Account account, Key.SignatureBy keyType, String key)
  /* $else $
  public Signature get(Account account, SignatureBy keyType, String key)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs)
  /* $else $
  public DataSource createDataSource(Account account, Type type, String dataSourceName, Map<String, Object> attrs)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs,
                                     boolean passwdAlreadyEncrypted)
  /* $else $
  public DataSource createDataSource(Account account, Type type, String dataSourceName, Map<String, Object> attrs,
                                     boolean passwdAlreadyEncrypted)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public DataSource restoreDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs)
  /* $else $
  public DataSource restoreDataSource(Account account, Type type, String dataSourceName, Map<String, Object> attrs)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  public void modifyDataSource(Account account, String dataSourceId,
                               Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public void deleteDataSource(Account account, String dataSourceId) {
    throw new UnsupportedOperationException();
  }

  public List<DataSource> getAllDataSources(Account account) {
    // Don't throw UnsupportedOperationException because Mailbox.updateRssDataSource()
    // calls this method.
    return Collections.emptyList();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public DataSource get(Account account, Key.DataSourceBy keyType, String key)
  /* $else $
  public DataSource get(Account account, DataSourceBy keyType, String key)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  public XMPPComponent createXMPPComponent(String name, Domain domain, Server server, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public XMPPComponent get(Key.XMPPComponentBy keyName, String key)
  /* $else $
  public XMPPComponent get(XMPPComponentBy keyName, String key)
  /* $endif $ */
  {
    throw new UnsupportedOperationException();
  }

  public List<XMPPComponent> getAllXMPPComponents() {
    throw new UnsupportedOperationException();
  }

  public void deleteXMPPComponent(XMPPComponent comp) {
    throw new UnsupportedOperationException();
  }

  public void flushCache(CacheEntryType type, CacheEntry[] entries) {
    throw new UnsupportedOperationException();
  }

  /* $if MajorZimbraVersion >= 8 $ */
  public ShareLocator get(Key.ShareLocatorBy keyType, String key) throws ServiceException {
    return shareLocators.get(key);
  }

  public ShareLocator createShareLocator(String id, Map<String, Object> attrs) throws ServiceException {
    ShareLocator shloc = new ShareLocator(id, attrs, this);
    shareLocators.put(id, shloc);
    return shloc;
  }

  public void deleteShareLocator(String id) throws ServiceException {
    shareLocators.remove(id);
  }

  public UCService createUCService(String name, Map<String, Object> attrs)
    throws ServiceException {
    throw new UnsupportedOperationException();
  }

  public void deleteUCService(String zimbraId) throws ServiceException {
    throw new UnsupportedOperationException();
  }

  public UCService get(Key.UCServiceBy keyName, String key) throws ServiceException {
    throw new UnsupportedOperationException();
  }

  public List<UCService> getAllUCServices() throws ServiceException {
    throw new UnsupportedOperationException();
  }

  public void renameUCService(String zimbraId, String newName) throws ServiceException {
    throw new UnsupportedOperationException();
  }

  public void searchAccountsOnServer(Server server, SearchAccountsOptions options, NamedEntry.Visitor visitor) throws ServiceException
  {
    for( Account account : getAllAccounts(null))
    {
      String serverName = account.getAttr(com.zimbra.cs.account.Provisioning.A_zimbraMailHost);
      if (serverName != null && ! serverName.equals("localhost"))
      {
        continue;
      }
      visitor.visit(account);
    }
  }

  public void searchDirectory(SearchDirectoryOptions options, NamedEntry.Visitor visitor) throws ServiceException
  {
    String accountId = options.getFilter().toString();

    for( Account account : getAllAccounts(null))
    {
      if( account.getId().equals(accountId) )
      {
        visitor.visit(account);
      }
    }
  }

  /* $endif $ */
}
