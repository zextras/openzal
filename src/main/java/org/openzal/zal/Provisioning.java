/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.NoSuchAccountException;
import org.openzal.zal.exceptions.NoSuchGrantException;
import org.openzal.zal.exceptions.NoSuchGroupException;
import org.openzal.zal.exceptions.UnableToFindDistributionListException;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.Filter;

import java.util.*;

public interface Provisioning
{
  String A_member = com.zimbra.cs.account.Provisioning.A_member;
  String ZIMBRA_USER_ID = "e0fafd89-1360-11d9-8661-000a95d98ef2";
  String DEFAULT_COS_ID = "e00428a1-0c00-11d9-836a-000d93afea2a";
  String DEFAULT_EXTERNAL_COS_ID = "f27456a8-0c00-11d9-280a-286d93afea2g";

  boolean isValidUid(@Nonnull String uid);

  @Nonnull
  Account getZimbraUser()
    throws ZimbraException;

  OperationContext createZContext();

  @Nullable
  DistributionList getDistributionListById(String id)
    throws ZimbraException;

  @Nullable
  DistributionList getDistributionListByName(String name)
    throws ZimbraException;

  void visitAllAccounts(@Nonnull SimpleVisitor<Account> visitor)
      throws ZimbraException;

  void visitAllLocalAccountsNoDefaults(@Nonnull SimpleVisitor<Account> visitor)
        throws ZimbraException;

  void visitAllLocalAccounts(@Nonnull SimpleVisitor<Account> visitor)
        throws ZimbraException;

  void visitAllAccounts(@Nonnull SimpleVisitor<Account> visitor, @Nonnull Filter<Account> filterAccounts)
          throws ZimbraException;

  void visitAllLocalAccountsSlow(
    @Nonnull SimpleVisitor<Account> visitor,
    @Nonnull Filter<Account> filterAccounts
  )
            throws ZimbraException;

  void visitAccountByIdNoDefaults(SimpleVisitor<Account> visitor, ZimbraId accountId);

  void visitAllDomains(@Nonnull SimpleVisitor<Domain> visitor) throws ZimbraException;

  void visitDomain(@Nonnull SimpleVisitor<Account> visitor, @Nonnull Domain domain) throws ZimbraException;

  void visitDomainsWithAttributes(@Nonnull SimpleVisitor<Domain> visitor, Map<String, Object> attributes) throws ZimbraException;

  Collection<String> getGroupMembers(String list) throws UnableToFindDistributionListException;

  void authAccount(@Nonnull Account account, String password, @Nonnull Protocol protocol, Map<String, Object> context)
              throws ZimbraException;

  void authAccountWithLdap(@Nonnull Account account, String password, Map<String, Object> context)
    throws ZimbraException;

  void authAccountSkippingCustom(@Nonnull Account account, String password, Map<String, Object> context, @Nullable String customName)
      throws ZimbraException;

  Account getAccountByAccountIdOrItemId(String id);

  @Nullable
  Account getAccountById(String accountId)
    throws ZimbraException;

  @Nonnull
  Server getLocalServer()
    throws ZimbraException;

  @Nullable
  Domain getDomainByName(String domainName)
    throws ZimbraException;

  List<Domain> getAllDomains()
      throws ZimbraException;

  void modifyAttrs(@Nonnull Entry entry, Map<String, Object> attrs)
      throws ZimbraException;

  @Nullable
  Domain getDomainById(String domainId)
    throws ZimbraException;

  @Nullable
  Domain getDomainByVirtualHostname(String host)
    throws ZimbraException;

  List<DistributionList> getAllDistributionLists(@Nonnull Domain domain)
      throws ZimbraException;

  List<Group> getAllGroups(Domain domain)
    throws ZimbraException;

  @Nullable
  Cos getCosById(String cosId)
    throws ZimbraException;

  List<Cos> getAllCos()
      throws ZimbraException;

  @Nullable
  Cos getCosByName(String cosStr)
    throws ZimbraException;

  @Nullable
  DistributionList get(@Nonnull ProvisioningKey.ByDistributionList id, String dlStr)
    throws ZimbraException;

  @Nullable
  Account get(@Nonnull ProvisioningKey.ByAccount by, String target)
    throws ZimbraException;

  @Nonnull
  Account assertAccountByName(String accountStr)
    throws NoSuchAccountException;

  @Nonnull
  Account assertAccountById(String accountStr)
    throws NoSuchAccountException;

  @Nullable
  Account getAccountByName(String accountStr)
    throws NoSuchAccountException;

  List<Account> getAllAdminAccounts()
    throws ZimbraException;

  Locale getLocale(Entry entry)
    throws ZimbraException;

  //instant-kill big infrastructures
  @Deprecated
  List<Account> getAllAccounts(@Nonnull Domain domain)
    throws ZimbraException;

  List<Server> getAllServers()
    throws ZimbraException;

  List<Server> getAllServers(String service)
    throws ZimbraException;

  List<Server> getAllReachableMailboxes() throws ZimbraException;;

  List<CalendarResource> getAllCalendarResources(@Nonnull Domain domain)
    throws ZimbraException;

  List<XMPPComponent> getAllXMPPComponents()
    throws ZimbraException;

  @Nullable
  GlobalGrant getGlobalGrant()
    throws ZimbraException;

  @Nonnull
  Config getConfig()
    throws ZimbraException;

  @Nullable
  CalendarResource getCalendarResourceByName(String resourceName)
    throws ZimbraException;

  @Nullable
  CalendarResource getCalendarResourceById(String resourceId)
    throws ZimbraException;

  @Nullable
  Domain createDomain(String currentDomainName, Map<String, Object> stringObjectMap)
    throws ZimbraException;

  @Nullable
  Cos createCos(String cosname, Map<String, Object> stringObjectMap)
    throws ZimbraException;

  @Nullable
  DistributionList createDistributionList(String dlistName)
    throws ZimbraException;

  @Nullable
  DistributionList createDistributionList(String dlistName, Map<String, Object> stringObjectMap)
    throws ZimbraException;

  @Nullable
  Group createDynamicGroup(String groupName)
    throws ZimbraException;

  @Nullable
  Group createDynamicGroup(String groupName, Map<String, Object> stringObjectMap)
    throws ZimbraException;

  @Nullable
  Account createCalendarResource(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException;

  @Nullable
  Account createAccount(String dstAccount, @Nullable String newPassword, Map<String, Object> attrs)
    throws ZimbraException;

  Account createGalAccount(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException;

  Account createFakeAccount(Map<String, Object> attrs)
    throws ZimbraException;

  void restoreAccount(String emailAddress, Map<String, Object> attrs);

  DataSource restoreDataSource(Account account, DataSourceType dsType, String dsName, Map<String, Object> dataSourceAttrs);

  Identity restoreIdentity(Account account, String identityName, Map<String, Object> identityAttrs);

  Signature restoreSignature(Account account, String signatureName, Map<String, Object> signatureAttrs);

  void restoreCos(Map<String,Object> attributes);

  void restoreDomain(Map<String, Object> attributes);

  void restoreDistributionList(String name, Map<String, Object> attributes);


  @Nullable
  Server createServer(String name, Map<String, Object> attrs)
          throws ZimbraException;

  void modifyIdentity(@Nonnull Account newAccount, String identityName, Map<String, Object> newAttrs)
      throws ZimbraException;

  void grantRight(
    String targetType, @Nonnull Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right
  ) throws ZimbraException;

  void grantRight(
    String targetType, @Nonnull Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right, RightModifier rightModifier
  ) throws ZimbraException;

  void revokeRight(
    String targetType, Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right
  ) throws NoSuchGrantException;

  void revokeRight(
    String targetType, Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right, RightModifier rightModifier
  ) throws NoSuchGrantException;

  boolean checkRight(
    String targetType,
    Targetby targetBy,
    String target,
    GrantedBy granteeBy,
    String granteeVal,
    String right
  );

  @Nullable
  Grants getGrants(
    String targetType,
    Targetby targetBy,
    String target,
    String granteeType,
    GrantedBy granteeBy,
    String grantee,
    boolean granteeIncludeGroupsGranteeBelongs
  );

  <T> T toZimbra(@Nonnull Class<T> cls);

  @Nullable
  Domain getDomain(@Nonnull Account account)
    throws ZimbraException;

  void flushCache(@Nonnull CacheEntryType cacheEntryType, @Nullable Collection<CacheEntry> cacheEntries)
      throws ZimbraException;

  ProvisioningImp.CountAccountResult countAccount(@Nonnull Domain domain)
        throws ZimbraException;

  long getAccountsOnCos(@Nonnull Domain domain, @Nonnull Cos cos);

  long getMaxAccountsOnCos(@Nonnull Domain domain, @Nonnull Cos cos);

  @Nullable
  Server getServer(@Nonnull Account acct)
    throws ZimbraException;

  @Nullable
  Server getServerById(String id)
    throws ZimbraException;

  @Nullable
  Server getServerByName(String name)
    throws ZimbraException;

  boolean onLocalServer(@Nonnull Account userAccount)
      throws ZimbraException;

  long getEffectiveQuota(@Nonnull Account account);

  List<Account> getAllDelegatedAdminAccounts() throws ZimbraException;

  void visitAllDelegatedAdminAccounts(SimpleVisitor<Account> visitor) throws ZimbraException;

  @Nullable
  Group getGroupById(String dlStr)
    throws ZimbraException;

  @Nullable
  Group getGroupByName(String dlStr)
    throws ZimbraException;

  void removeGranteeId(
    String target,
    String grantee_id,
    String granteeType,
    String right
  ) throws ZimbraException;

  @Nullable
  Grants getGrants(
    @Nonnull org.openzal.zal.provisioning.TargetType targetType,
    Targetby name,
    String targetName,
    boolean granteeIncludeGroupsGranteeBelongs
  );

  String getGranteeName(
    String grantee_id,
    @Nonnull String grantee_type
  ) throws ZimbraException;

  @Nonnull
  GalSearchResult galSearch(@Nonnull Account account, String query, int skip, int limit);

  @Nonnull
  GalSearchResult galSearch(@Nonnull Account account, Domain domain, String query, int skip, int limit);

  @Nonnull
  Domain assertDomainById(String domainId);

  @Nonnull
  Domain assertDomainByName(String domainId);

  @Nonnull
  DistributionList assertDistributionListById(String targetId);

  void deleteAccountByName(String id);

  @Nonnull
  void deleteAccountById(String id);

  @Nonnull
  void deleteDomainById(String id);

  @Nonnull
  void deleteCosById(String id);

  Collection<Domain> getDomainAliases(Domain domain);

  void invalidateAllCache();

  void purgeMemcachedAccounts(List<String> accounts);

  void rawQuery(String base, String query, LdapVisitor visitor);

  void rawQuery(String base, String query, LdapVisitor visitor, String[] fields);

  int rawCountQuery(String base, String query);

  void registerChangePasswordListener(ChangePasswordListener listener);

  long getLastLogonTimestampFrequency();

  @Nonnull
  Group assertGroupById(String groupId)
    throws NoSuchGroupException;

  @Nonnull
  Group assertGroupByName(String groupName)
    throws NoSuchGroupException;

  boolean doExternalLdapAuth(Domain domain, String account, String password, Map<String, Object> context);

  Account getForeignAccount(String principal);

  Account autoProvisioningAndAuthenticate(Domain domain, String account, String password);

  class CountAccountByCos
  {
    private final com.zimbra.cs.account.Provisioning.CountAccountResult.CountAccountByCos mCountAccountByCos;

    public CountAccountByCos(com.zimbra.cs.account.Provisioning.CountAccountResult.CountAccountByCos countAccountByCos)
    {
      mCountAccountByCos = countAccountByCos;
    }

    public String getCosId()
    {
      return mCountAccountByCos.getCosId();
    }

    public String getCosName()
    {
      return mCountAccountByCos.getCosName();
    }

    public long getCount()
    {
      return mCountAccountByCos.getCount();
    }
  }

  class GalSearchResult
  {
    @Nonnull private final LinkedList<ProvisioningImp.GalSearchResult.GalContact> mContactList;
    private                int                                                    mTotal;
    private                boolean                                                mHasMore;

    void setTotal(int total)
    {
      mTotal = total;
    }

    void setHasMore(boolean hasMore)
    {
      mHasMore = hasMore;
    }

    public static class GalContact
    {
      private final com.zimbra.cs.account.GalContact mGalContact;

      public GalContact(Object galContact)
      {
        mGalContact = (com.zimbra.cs.account.GalContact)galContact;
      }

      public String getSingleAttr(String key)
      {
        return mGalContact.getSingleAttr(key);
      }

      public List<String> match(String regex)
      {
        Map<String,Object> attr = mGalContact.getAttrs();
        List<String> values = new ArrayList<String>();

        for (String key : attr.keySet())
        {
          if (key.matches(regex))
          {
            values.add(getSingleAttr(key));
          }
        }

        return values;
      }

      /**
       *
       * @param regex
       * @return A list of key-value attributes that match with regex.
       */
      public List<Pair<String, String>> matchAttrs(String regex)
      {
        Map<String,Object> attr = mGalContact.getAttrs();
        List<Pair<String, String>> values = new ArrayList<>(0);

        for (String key : attr.keySet())
        {
          if (key.matches(regex))
          {
            values.add(new Pair<>(key, getSingleAttr(key)));
          }
        }

        return values;
      }

      public String getId()
      {
        return mGalContact.getId();
      }
    }

    public GalSearchResult()
    {
      mContactList = new LinkedList<ProvisioningImp.GalSearchResult.GalContact>();
    }

    @Nonnull
    public List<ProvisioningImp.GalSearchResult.GalContact> getContactList()
    {
      return mContactList;
    }

    public boolean hasMore()
    {
      return mHasMore;
    }

    public int getTotal()
    {
      return mTotal;
    }

    public void addContact(ProvisioningImp.GalSearchResult.GalContact galContact)
    {
      if (!alreadyAddedByEmail(galContact))
      {
        mContactList.add(galContact);
      }
    }

    private boolean alreadyAddedByEmail(ProvisioningImp.GalSearchResult.GalContact galContact)
    {
      String email = galContact.getSingleAttr("email");
      if (email == null)
      {
        email = "";
      }

      for (ProvisioningImp.GalSearchResult.GalContact current : mContactList)
      {
        if (email.equalsIgnoreCase(current.getSingleAttr("email")))
        {
          return true;
        }
      }
      return false;
    }
  }

  class CountAccountResult
  {
    private final com.zimbra.cs.account.Provisioning.CountAccountResult mCountAccountResult;

    protected CountAccountResult(com.zimbra.cs.account.Provisioning.CountAccountResult countAccountResult)
    {
      mCountAccountResult = countAccountResult;
    }

    public List<CountAccountByCos> getCountAccountByCos()
    {
      return ZimbraListWrapper.wrapCountAccountByCosList(mCountAccountResult.getCountAccountByCos());
    }
  }

  void reloadAccount(Account account);
}
