package com.zimbra.cs.ldap;

import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;

public class ZLdapFilterFactorySimulator extends ZLdapFilterFactory {
  public static void setInstance()
  {
    try {
      ZLdapFilterFactory.setInstance(new ZLdapFilterFactorySimulator());
    }
    catch (java.lang.AssertionError ex){
    }
  }

  /* $if ZimbraVersion >= 8.0.8 $ */

  public ZLdapFilter velodromeAllDistributionListsByDomain(String s)
  {
    return new FakeLdapFilter(FilterId.VELODROME_ALL_DISTRIBUTION_LISTS_BY_DOMAIN);
  }

  public ZLdapFilter velodromeAllGroupsByDomain(String s)
  {
    return new FakeLdapFilter(FilterId.VELODROME_ALL_GROUPS_BY_DOMAIN);
  }

  public ZLdapFilter allAddressLists()
  {
    return new FakeLdapFilter(FilterId.ALL_ADDRESS_LISTS);
  }

  public ZLdapFilter addressListById(String s)
  {
    return new FakeLdapFilter(FilterId.ADDRESS_LIST_BY_ID);
  }

  public ZLdapFilter addressListByName(String s)
  {
    return new FakeLdapFilter(FilterId.ADDRESS_LIST_BY_NAME);
  }

  /* $endif$ */

  @Override
  public String encodeValue(String value) {
    return "";
  }

  @Override
  public ZLdapFilter hasSubordinates() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;

//    return new ZLdapFilter(FilterId.HAS_SUBORDINATES) {
//      @Override
//      public String toFilterString() {
//        return "";
//      }
//    };
  }

  class FakeLdapFilter extends ZLdapFilter
  {
    protected FakeLdapFilter(FilterId filterId)
    {
      super(filterId);
    }

    @Override
    public String toFilterString()
    {
      return "";
    }
  }

  @Override
  public ZLdapFilter createdLaterOrEqual(String generalizedTime) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter anyEntry() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter fromFilterString(FilterId filterId, String filterString) throws LdapException {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter andWith(ZLdapFilter filter, ZLdapFilter otherFilter) {

    return filter;
  }

  @Override
  public ZLdapFilter negate(ZLdapFilter filter) {

    return filter;
  }

  @Override
  public ZLdapFilter addrsExist(String[] addrs) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter allAccounts()
  {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter allAccountsOnly()
  {
    return new ZLdapFilter(FilterId.ALL_ACCOUNTS) {
      @Override
      public String toFilterString() {
        return "";
      }
    };
  }

  public ZLdapFilter allAccountsOnlyByCos(String s)
  {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allAdminAccounts() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter allNonSystemAccounts() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter allNonSystemArchivingAccounts() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter allNonSystemInternalAccounts() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountByForeignPrincipal(String foreignPrincipal) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountById(String id) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toString()).thenReturn(id);

    return filter;
  }

  @Override
  public ZLdapFilter accountByMemberOf(String dynGroupId) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountByName(String name) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter adminAccountByRDN(String namingRdnAttr, String name) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountsHomedOnServer(String serverServiceHostname) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountsHomedOnServerAccountsOnly(String serverServiceHostname) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter homedOnServer(String serverServiceHostname) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter accountsOnServerAndCosHasSubordinates(String serverServiceHostname, String cosId) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter externalAccountsHomedOnServer(String serverServiceHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);     when(filter.toFilterString()).thenReturn("");      return filter;
  }

  @Override
  public ZLdapFilter accountsByGrants(List<String> granteeIds, boolean includePublicShares, boolean includeAllAuthedShares) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);     when(filter.toFilterString()).thenReturn("");      return filter;
  }

  @Override
  public ZLdapFilter CMBSearchAccountsOnly() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter CMBSearchAccountsOnlyWithArchive() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter CMBSearchNonSystemResourceAccountsOnly() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allAliases() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn("");  return filter;
  }

  @Override
  public ZLdapFilter allCalendarResources() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter calendarResourceByForeignPrincipal(String foreignPrincipal) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter calendarResourceById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter calendarResourceByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter calendarResourcesHomedOnServer(String serverServiceHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allCoses() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter cosById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter cosesByMailHostPool(String serverId) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allDataSources() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dataSourceById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dataSourceByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allDistributionLists() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter distributionListById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter distributionListByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter distributionListsByMemberAddrs(String[] memberAddrs) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  public ZLdapFilter allDynamicGroups() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dynamicGroupById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  public ZLdapFilter dynamicGroupByIds(String[] strings) {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dynamicGroupByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dynamicGroupDynamicUnitByMailAddr(String mailAddr) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dynamicGroupsStaticUnitByMemberAddr(String memberAddr) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allGroups() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter groupById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter groupByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allDomains() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainAliases(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  public ZLdapFilter domainsByIds(Collection<String> collection)
  {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainByKrb5Realm(String krb5Realm) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainByVirtualHostame(String virtualHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainByForeignName(String foreignName) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainLabel() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter domainLockedForEagerAutoProvision() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter globalConfig() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allIdentities() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter identityByName(String name) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allMimeEntries() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter mimeEntryByMimeType(String mimeType) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allServers() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter serverById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter serverByService(String service) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter shareLocatorById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allSignatures() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter signatureById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allXMPPComponents() {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter imComponentById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter xmppComponentById(String id) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allZimlets() {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class);
    when(filter.toFilterString()).thenReturn("");

    return filter;
  }

  @Override
  public ZLdapFilter memberOf(String dnOfGroup) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllAccountsByDomain(String domainName) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllAccountsOnlyByDomain(String domainName) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllCalendarResourcesByDomain(String domainName) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllAccountsByDomainAndServer(String domainName, String serverServiceHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllAccountsOnlyByDomainAndServer(String domainName, String serverServiceHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter velodromeAllCalendarResourcesByDomainAndServer(String domainName, String serverServiceHostname) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter dnSubtreeMatch(String... dns) {
     ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  /* $if ZimbraVersion >= 8.8.10 || ZimbraX == 1 $ */
  @Override
  public ZLdapFilter habOrgUnitByName(String s)
  {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }

  @Override
  public ZLdapFilter allHabGroups()
  {
    ZLdapFilter filter = Mockito.mock(ZLdapFilter.class); when(filter.toFilterString()).thenReturn(""); return filter;
  }
  /* $endif $*/

}
