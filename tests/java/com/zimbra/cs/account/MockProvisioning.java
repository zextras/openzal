package com.zimbra.cs.account;

import java.io.IOException;
import java.util.*;

import com.zextras.modules.mobile.v2.as.events.utils.SearchGalProperty;
import com.zextras.modules.mobile.v2.engine.actions.GALSearchAction;
import com.zimbra.cs.account.accesscontrol.RightModifier;
import com.zimbra.cs.account.ldap.LdapDomainProxy;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.cs.gal.GalSearchResultCallback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.ProvisioningImp;
import org.openzal.zal.redolog.MockRedoLogProvider;

/**
 * Mock implementation of {@link ProvisioningImp} for testing.
 *
 * @author ysasaki
 * Zimbra Collaboration Suite Server
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.cs.mime.handler.TextEnrichedHandler;
import com.zimbra.cs.mime.handler.TextHtmlHandler;
import com.zimbra.cs.mime.handler.TextPlainHandler;
import com.zimbra.cs.mime.handler.UnknownTypeHandler;

import javax.annotation.Nullable;

import com.zimbra.cs.ldap.ZLdapFilterFactorySimulator;
import com.zimbra.cs.ldap.ZAttributes;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.common.account.Key;
import com.zimbra.common.account.ProvisioningConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.AuthContext;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.mime.MockMimeTypeInfo;
import com.zimbra.soap.admin.type.CacheEntryType;
import com.zimbra.soap.admin.type.DataSourceType;
import com.zimbra.soap.type.TargetBy;
/* $if ZimbraVersion < 8.0.6 $
import com.zimbra.common.account.Key.GranteeBy;
   $endif$ */

/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $endif$ */

class MockDistributionList extends DistributionList
{
  private Set<String> mMembers;

  public MockDistributionList(String name, String id, Map<String, Object> attrs, Provisioning prov)
  {
    super(name, id, attrs, prov);
    mMembers = new HashSet<String>();
  }

  public void addMembers(String[] members) throws ServiceException {
    for (String member: members)
    {
      mMembers.add(member);
    }
  }

  public void removeMembers(String[] members) throws ServiceException {
    for (String member: members)
    {
      mMembers.remove(member);
    }
  }

  public String[] getAllMembers() throws ServiceException {
    return mMembers.toArray(new String[0]);
  }

  public Set<String> getAllMembersSet() throws ServiceException {
    return mMembers;
  }

}

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
  private final Map<String, DistributionList>   name2Dlist   = new HashMap<String, DistributionList>();
  private final Map<String, Zimlet>             id2zimlets   = new HashMap<String, Zimlet>();
  private final Map<String, Signature>          id2signatue  = new HashMap<String, Signature>();

  private final Config                    config        = new Config(new HashMap<String, Object>()
  {{
      put("confKey", "configurationValue");
    }}, this);

  private final Map<String, ShareLocator> shareLocators = new HashMap<String, ShareLocator>();

  private final Server        mLocalhost;
  private final List<Server>  mServers;
  private final Cos           mDefaultCos;

  private int mCounter = 1;

  public MockProvisioning()
  {
    mServers = new ArrayList<Server>();
    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(A_zimbraServiceHostname, "localhost");
    mLocalhost = createServer("localhost",attrs);
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
    if (!attrs.containsKey(A_zimbraDomainId))
    {
      int idx = email.indexOf("@");
      if (idx >= 0)
      {
        String domainName = email.substring(idx+1);
        if (domainName == null || domainName.isEmpty())
          domainName = "unknown.com";

        Domain domain = getDomainByName(domainName);
        if (domain == null)
        {
          domain = createDomain(domainName, new HashMap<String, Object>());
        }
        attrs.put(A_zimbraDomainId, domain.getId());
      }
    }
    if (!attrs.containsKey("email"))
    {
      attrs.put("email",email);
    }
    attrs.put(A_zimbraBatchedIndexingSize, Integer.toString(Integer.MAX_VALUE)); // suppress indexing
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

  public Account get(Key.AccountBy keyType, String key)
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

    entry.setAttrs(map);
  }


  public Server getLocalServer()
  {
    return mLocalhost;
  }

  public Server getLocalServerIfDefined()
  {
    return null;
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

  public void authAccount(Account acct, String password, AuthContext.Protocol proto)
  {
    if( password.equals(acct.getAttr("password")) ) {
      return;
    }
    throw new UnsupportedOperationException();
  }

  public void authAccount(Account acct, String password, AuthContext.Protocol proto, Map<String, Object> authCtxt)
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

  public SetPasswordResult setPassword(Account acct, String newPassword) {
    throw new UnsupportedOperationException();
  }

  public void ssoAuthAccount(Account acct, AuthContext.Protocol proto, Map<String, Object> authCtxt) {
    throw new UnsupportedOperationException();
  }

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
    if (id == null)
    {
      attrs.put(A_zimbraId, id = UUID.randomUUID().toString());
    }
    if (!attrs.containsKey(A_zimbraSmtpHostname))
    {
      attrs.put(A_zimbraSmtpHostname, "localhost");
    }
    if (!attrs.containsKey("zimbraDomainName)"))
    {
      attrs.put("zimbraDomainName",name);
    }
    MockZAttributes zAttributes = new MockZAttributes(attrs);

    Domain domain;
    try
    {
      domain = new LdapDomainProxy(name, zAttributes, attrs, this)
      {
        public String getGalSearchBase(String searchBaseSpec) throws ServiceException
        {
          return searchBaseSpec;
        }

        public ZLdapFilter getDnSubtreeMatchFilter() throws ServiceException
        {
          return ZLdapFilterFactorySimulator.getInstance().dnSubtreeMatch("example.com");
        }
      };
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }

    id2domain.put(id, domain);
    name2domain.put(name, domain);
    return domain;
  }

  public void searchGal(GalSearchParams params) throws ServiceException {
    String query = params.getQuery().toLowerCase();
    SearchGalResult result = params.getResult();
    GalSearchResultCallback callback = params.createResultCallback();

    String regex = RegexUtil.sqlPatternToRegex(query);
    for (String name:name2account.keySet())
    {
      {
        Account account = name2account.get(name);
        GalContact zimbraContact = new GalContact(account.getName(), account.getAttrs());
        ProvisioningImp.GalSearchResult.GalContact contact = new ProvisioningImp.GalSearchResult.GalContact(zimbraContact);
        try
        {
          boolean found = name.toLowerCase().matches(regex);

          if (!found)
          {
            List<SearchGalProperty> properties = GALSearchAction.getGalProperties(contact);

            for (SearchGalProperty property : properties)
            {
              if (property.getValue().toLowerCase().matches(regex))
              {
                found = true;
                break;
              }
            }
          }

          if (found)
          {
            callback.visit(zimbraContact);
            result.addMatch(zimbraContact);
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }

    callback.setHasMoreResult(false);
  }

  public Domain get(Key.DomainBy keyType, String key)
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

  public void deleteDomainAfterRename(String s) throws ServiceException
  {
    deleteDomain(s);
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

  public Cos get(Key.CosBy keyType, String key)
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

  public Server createServer(String name, Map<String, Object> attrs)
  {
    if (!attrs.containsKey(A_zimbraServiceHostname))
    {
      throw new RuntimeException(A_zimbraServiceHostname+" not specified");
    }
    if (!attrs.containsKey(A_zimbraRedoLogProvider))
    {
      attrs.put(A_zimbraRedoLogProvider, MockRedoLogProvider.class.getName());
    }
    if (!attrs.containsKey(A_zimbraId))
    {
      attrs.put(A_zimbraId, UUID.randomUUID().toString());
    }
    if (!attrs.containsKey(A_zimbraMailMode))
    {
      attrs.put(A_zimbraMailMode, MailMode.http.toString());
    }
    if (!attrs.containsKey(A_zimbraSmtpPort))
    {
      attrs.put(A_zimbraSmtpPort, "7025");
    }
    if (!attrs.containsKey(A_zimbraMessageCacheSize))
    {
      attrs.put(A_zimbraMessageCacheSize, "0");
    }

    Server server = new Server(name, name, attrs, Collections.<String, Object>emptyMap(), this);
    mServers.add(server);
    return server;
  }

  public Server get(final Key.ServerBy keyName,final String key)
  {
    switch (keyName) {
      case id:
        return (Server)CollectionUtils.find(mServers, new Predicate() {
          @Override
          public boolean evaluate(Object o)
          {
            return ((Server)o).getId().equals(key);
          }
        });
      case name:
        return (Server)CollectionUtils.find(mServers, new Predicate() {
          @Override
          public boolean evaluate(Object o)
          {
            return ((Server)o).getName().equals(key);
          }
        });
      default:
        throw new UnsupportedOperationException();
    }
  }

  public List<Server> getAllServers() {
    return mServers;
  }

  public List<Server> getAllServers(String service) {
    return Collections.<Server>emptyList();
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
    DistributionList list = new MockDistributionList(name, name, listAttrs, this);
    name2Dlist.put(name, list);
    id2Dlist.put(list.getId(), list);
    return list;
  }

  public DistributionList get(Key.DistributionListBy keyType, String key)
  {
    if( keyType.toString().equalsIgnoreCase("id") )
    {
      return id2Dlist.get(key);
    }
    else
    {
      return name2Dlist.get(key);
    }
  }

  public Group getGroupBasic(Key.DistributionListBy keyType, String key) throws ServiceException
  {
    if( keyType.toString().equalsIgnoreCase("id") )
    {
      return id2Dlist.get(key);
    }
    else
    {
      return name2Dlist.get(key);
    }
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

  @Nullable
  public Zimlet getZimlet(String name) {
    for( Zimlet current : id2zimlets.values() )
    {
      if( name.equalsIgnoreCase(current.getName()))
      {
        return current;
      }
    }

    return null;
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

  public CalendarResource get(Key.CalendarResourceBy keyType, String key)
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

  public void addMembers(DistributionList list, String[] members) throws ServiceException
  {
    DistributionList dlist = id2Dlist.get(list.getName());
    dlist.addMembers(members);
  }

  public void removeMembers(DistributionList list, String[] member) {
    throw new UnsupportedOperationException();
  }

  public Identity getDefaultIdentity(Account account) {
    Map<String, Object> attrs = new HashMap<String, Object>();
    attrs.put(A_zimbraPrefIdentityName, ProvisioningConstants.DEFAULT_IDENTITY_NAME);
    attrs.put(A_zimbraPrefIdentityId, account.getId());
    return new Identity(account, ProvisioningConstants.DEFAULT_IDENTITY_NAME, account.getId(), attrs, this);
  }

  public Identity createIdentity(Account account, String identityName, Map<String, Object> attrs) {
    return new Identity(account, identityName, account.getId(), attrs, this);
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

  public Identity get(Account account, Key.IdentityBy keyType, String key)
  {
    throw new UnsupportedOperationException();
  }

  public Signature createSignature(Account account, String signatureName, Map<String, Object> attrs) {
    Signature signature = new Signature(account, signatureName, signatureName, attrs, this);
    id2signatue.put(account.getId() + "\\" + signatureName, signature);
    return signature;
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

  public Signature get(Account account, Key.SignatureBy keyType, String key)
  {
    return id2signatue.get(account.getId() + "\\" + key);
  }

  public DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs)
  {
    throw new UnsupportedOperationException();
  }

  public DataSource createDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs,
                                     boolean passwdAlreadyEncrypted)
  {
    throw new UnsupportedOperationException();
  }

  public DataSource restoreDataSource(Account account, DataSourceType type, String dataSourceName, Map<String, Object> attrs)
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

  public DataSource get(Account account, Key.DataSourceBy keyType, String key)
  {
    throw new UnsupportedOperationException();
  }

  public XMPPComponent createXMPPComponent(String name, Domain domain, Server server, Map<String, Object> attrs) {
    throw new UnsupportedOperationException();
  }

  public XMPPComponent get(Key.XMPPComponentBy keyName, String key)
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
    //throw new UnsupportedOperationException();
  }

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

  private Entry getEntry(String entryKey, boolean isId, String type)
  {
    if( type.equalsIgnoreCase("usr") || type.equalsIgnoreCase("account") )
    {
      if( isId )
      {
        return id2account.get(entryKey);
      }
      else
      {
        return name2account.get(entryKey);
      }
    }
    else if( type.equalsIgnoreCase("grp") )
    {
      if( isId )
      {
        return id2Dlist.get(entryKey);
      }
      else
      {
        return name2Dlist.get(entryKey);
      }
    }
    else if( type.equalsIgnoreCase("domain") )
    {
      if( isId )
      {
        return id2domain.get(entryKey);
      }
      else
      {
        return name2domain.get(entryKey);
      }
    }
    else
    {
      throw new UnsupportedOperationException();
    }
  }

  public void grantRight(String targetType,
                         TargetBy targetBy,
                         String target,
                         String granteeType,
                         GranteeBy granteeBy,
                         String grantee,
                         String secret,
                         String right,
                         RightModifier rightModifier) throws ServiceException
  {
    Entry granteeEntry = getEntry(grantee, granteeBy.equals(GranteeBy.id), granteeType);
    Entry targetEntry = getEntry(target, targetBy.equals(TargetBy.id), targetType);

    Set<String> aceSet = targetEntry.getMultiAttrSet("zimbraACE");
    aceSet.add(
      createAceString(granteeType, granteeEntry.getAttr("zimbraId"), right, rightModifier)
    );

    Map<String, Object> attrs = targetEntry.getAttrs();
    attrs.put("zimbraACE", aceSet.toArray(new String[aceSet.size()]));

    targetEntry.setAttrs(attrs);
  }

  @NotNull
  private String createAceString(String targetType, String target, String right, RightModifier rightModifier)
  {
    String modifier = rightModifier != null ? ""+rightModifier.getModifier() : "";
    return target+" "+targetType+" "+modifier+right;
  }

  public void revokeRight(String targetType,
                          TargetBy targetBy,
                          String target,
                          String granteeType,
                          GranteeBy granteeBy,
                          String grantee,
                          String right,
                          RightModifier rightModifier) throws ServiceException
  {
    Entry granteeEntry = getEntry(grantee, granteeBy.equals(GranteeBy.id), granteeType);
    Entry targetEntry = getEntry(target, targetBy.equals(TargetBy.id), targetType);

    Set<String> aceSet = targetEntry.getMultiAttrSet("zimbraACE");
    aceSet.remove(
      createAceString(granteeType, granteeEntry.getAttr("zimbraId"), right, rightModifier)
    );

    Map<String, Object> attrs = targetEntry.getAttrs();
    attrs.put("zimbraACE", aceSet.toArray(new String[aceSet.size()]));

    targetEntry.setAttrs(attrs);
  }
}

class MockZAttributes
  extends ZAttributes
{
  private Map<String, Object> mAttrs;

  public MockZAttributes(Map<String, Object> attrs)
  {
    mAttrs = attrs;
  }

  public Map<String, Object> getAttrs(Set<String> set)
  {
    return mAttrs;
  }

  protected String getAttrString(String s, boolean b)
  {
    return (String)mAttrs.get(s);
  }

  protected String[] getMultiAttrString(String s, boolean b)
  {
    return (String[])mAttrs.get(s);
  }

  public boolean hasAttribute(String s)
  {
    return mAttrs.containsKey(s);
  }

  public boolean hasAttributeValue(String s, String s1)
  {
    String attr = (String)mAttrs.get(s);
    if (attr != null)
    {
      return attr.equals(s1);
    }
    return false;
  }
}


class RegexUtil {

  static final Pattern BACKSLASH = Pattern.compile("\\\\");
  static final Pattern DOT = Pattern.compile("\\.");

  /**
   * Replaces all backslashes "\" with forward slashes "/". Convenience method to
   * convert path Strings to URI format.
   */
  static String substBackslashes(String string) {
    if (string == null) {
      return null;
    }

    Matcher matcher = BACKSLASH.matcher(string);
    return matcher.find() ? matcher.replaceAll("\\/") : string;
  }

  /**
   * Returns package name for the Java class as a path separated with forward slash
   * ("/"). Method is used to lookup resources that are located in package
   * subdirectories. For example, a String "a/b/c" will be returned for class name
   * "a.b.c.ClassName".
   */
  static String getPackagePath(String className) {
    if (className == null) {
      return "";
    }

    Matcher matcher = DOT.matcher(className);
    if (matcher.find()) {
      String path = matcher.replaceAll("\\/");
      return path.substring(0, path.lastIndexOf("/"));
    }
    else {
      return "";
    }
  }

  /**
   * Converts a SQL-style pattern to a valid Perl regular expression. E.g.:
   * <p>
   * <code>"billing_%"</code> will become <code>^billing_.*$</code>
   * <p>
   * <code>"user?"</code> will become <code>^user.?$</code>
   */
  static String sqlPatternToRegex(String pattern) {
    if (pattern == null) {
      throw new NullPointerException("Null pattern.");
    }

    if (pattern.length() == 0) {
      throw new IllegalArgumentException("Empty pattern.");
    }

    StringBuffer buffer = new StringBuffer();

    // convert * into regex syntax
    // e.g. abc*x becomes ^abc.*x$
    // or abc?x becomes ^abc.?x$
    buffer.append("^");
    for (int j = 0; j < pattern.length(); j++) {
      char nextChar = pattern.charAt(j);
      if (nextChar == '%') {
        nextChar = '*';
      }

      if (nextChar == '*' || nextChar == '?') {
        buffer.append('.');
      }
      // escape special chars
      else if (nextChar == '.'
              || nextChar == '/'
              || nextChar == '$'
              || nextChar == '^') {
        buffer.append('\\');
      }

      buffer.append(nextChar);
    }

    buffer.append("$");
    return buffer.toString();
  }

  private RegexUtil() {
    super();
  }

}
