/*
 * ZAL - An abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import java.util.*;

import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.Filter;
import org.openzal.zal.provisioning.ZEGroup;
import org.openzal.zal.provisioning.ZETargetType;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.accesscontrol.*;
import com.zimbra.cs.gal.GalSearchControl;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.cs.gal.GalSearchResultCallback;
import com.zimbra.common.soap.Element;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import com.zimbra.soap.type.GalSearchType;
import com.zimbra.soap.type.TargetBy;
/* $else $
import com.zimbra.cs.account.ldap.LdapProvisioning;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.io.IOException;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.account.ldap.LdapFilter;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Server;
/* $endif $ */

/* $if ZimbraVersion >= 8.0.0 && ZimbraVersion < 8.0.6 $
import com.zimbra.common.account.Key.GranteeBy;
   $endif$ */

/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $endif$ */

import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.OperationContext;

import org.jetbrains.annotations.Nullable;

/* $if MajorZimbraVersion >= 8 $ */

/* $elseif MajorZimbraVersion >= 7 $
import com.zimbra.cs.account.Provisioning.GalSearchType;
$endif$ */

public class ZEProvisioning
{
  public static final String ZIMBRA_USER_ID = "e0fafd89-1360-11d9-8661-000a95d98ef2";

  /* $if ZimbraVersion >= 8.0.0 $ */
  public static String A_zimbraMailDomainQuota                                      = Provisioning.A_zimbraMailDomainQuota;
  public static String A_zimbraPrefAllowAddressForDelegatedSender                   = Provisioning.A_zimbraPrefAllowAddressForDelegatedSender;
  public static String DEFAULT_COS_NAME                                             = Provisioning.DEFAULT_COS_NAME;
  public static String DEFAULT_EXTERNAL_COS_NAME                                    = Provisioning.DEFAULT_EXTERNAL_COS_NAME;
  public static String A_zimbraMobilePolicyAllowBluetooth                           = Provisioning.A_zimbraMobilePolicyAllowBluetooth;
  public static String A_zimbraMobilePolicyAllowBrowser                             = Provisioning.A_zimbraMobilePolicyAllowBrowser;
  public static String A_zimbraMobilePolicyAllowCamera                              = Provisioning.A_zimbraMobilePolicyAllowCamera;
  public static String A_zimbraMobilePolicyAllowConsumerEmail                       = Provisioning.A_zimbraMobilePolicyAllowConsumerEmail;
  public static String A_zimbraMobilePolicyAllowDesktopSync                         = Provisioning.A_zimbraMobilePolicyAllowDesktopSync;
  public static String A_zimbraMobilePolicyAllowHTMLEmail                           = Provisioning.A_zimbraMobilePolicyAllowHTMLEmail;
  public static String A_zimbraMobilePolicyAllowInternetSharing                     = Provisioning.A_zimbraMobilePolicyAllowInternetSharing;
  public static String A_zimbraMobilePolicyAllowIrDA                                = Provisioning.A_zimbraMobilePolicyAllowIrDA;
  public static String A_zimbraMobilePolicyAllowPOPIMAPEmail                        = Provisioning.A_zimbraMobilePolicyAllowPOPIMAPEmail;
  public static String A_zimbraMobilePolicyAllowRemoteDesktop                       = Provisioning.A_zimbraMobilePolicyAllowRemoteDesktop;
  public static String A_zimbraMobilePolicyAllowSMIMEEncryptionAlgorithmNegotiation = Provisioning.A_zimbraMobilePolicyAllowSMIMEEncryptionAlgorithmNegotiation;
  public static String A_zimbraMobilePolicyAllowSMIMESoftCerts                      = Provisioning.A_zimbraMobilePolicyAllowSMIMESoftCerts;
  public static String A_zimbraMobilePolicyAllowStorageCard                         = Provisioning.A_zimbraMobilePolicyAllowStorageCard;
  public static String A_zimbraMobilePolicyAllowTextMessaging                       = Provisioning.A_zimbraMobilePolicyAllowTextMessaging;
  public static String A_zimbraMobilePolicyAllowUnsignedApplications                = Provisioning.A_zimbraMobilePolicyAllowUnsignedApplications;
  public static String A_zimbraMobilePolicyAllowUnsignedInstallationPackages        = Provisioning.A_zimbraMobilePolicyAllowUnsignedInstallationPackages;
  public static String A_zimbraMobilePolicyAllowWiFi                                = Provisioning.A_zimbraMobilePolicyAllowWiFi;
  public static String A_zimbraMobilePolicyMaxCalendarAgeFilter                     = Provisioning.A_zimbraMobilePolicyMaxCalendarAgeFilter;
  public static String A_zimbraMobilePolicyMaxEmailAgeFilter                        = Provisioning.A_zimbraMobilePolicyMaxEmailAgeFilter;
  public static String A_zimbraMobilePolicyMaxEmailBodyTruncationSize               = Provisioning.A_zimbraMobilePolicyMaxEmailBodyTruncationSize;
  public static String A_zimbraMobilePolicyMaxEmailHTMLBodyTruncationSize           = Provisioning.A_zimbraMobilePolicyMaxEmailHTMLBodyTruncationSize;
  public static String A_zimbraMobilePolicyRequireDeviceEncryption                  = Provisioning.A_zimbraMobilePolicyRequireDeviceEncryption;
  public static String A_zimbraMobilePolicyRequireEncryptedSMIMEMessages            = Provisioning.A_zimbraMobilePolicyRequireEncryptedSMIMEMessages;
  public static String A_zimbraMobilePolicyRequireEncryptionSMIMEAlgorithm          = Provisioning.A_zimbraMobilePolicyRequireEncryptionSMIMEAlgorithm;
  public static String A_zimbraMobilePolicyRequireManualSyncWhenRoaming             = Provisioning.A_zimbraMobilePolicyRequireManualSyncWhenRoaming;
  public static String A_zimbraMobilePolicyRequireSignedSMIMEAlgorithm              = Provisioning.A_zimbraMobilePolicyRequireSignedSMIMEAlgorithm;
  public static String A_zimbraMobilePolicyRequireSignedSMIMEMessages               = Provisioning.A_zimbraMobilePolicyRequireSignedSMIMEMessages;
  public static String A_zimbraMobilePolicySuppressDeviceEncryption                 = Provisioning.A_zimbraMobilePolicySuppressDeviceEncryption;
  /* $else $
  public static       String A_zimbraMailDomainQuota                    = "";
  public static       String A_zimbraPrefAllowAddressForDelegatedSender = "";
  public static       String DEFAULT_COS_NAME                           = "";
  public static       String DEFAULT_EXTERNAL_COS_NAME                  = "";
  public static       String A_zimbraMobilePolicyAllowBluetooth = "";
  public static       String A_zimbraMobilePolicyAllowBrowser = "";
  public static       String A_zimbraMobilePolicyAllowCamera = "";
  public static       String A_zimbraMobilePolicyAllowConsumerEmail = "";
  public static       String A_zimbraMobilePolicyAllowDesktopSync = "";
  public static       String A_zimbraMobilePolicyAllowHTMLEmail = "";
  public static       String A_zimbraMobilePolicyAllowInternetSharing = "";
  public static       String A_zimbraMobilePolicyAllowIrDA = "";
  public static       String A_zimbraMobilePolicyAllowPOPIMAPEmail = "";
  public static       String A_zimbraMobilePolicyAllowRemoteDesktop = "";
  public static       String A_zimbraMobilePolicyAllowSMIMEEncryptionAlgorithmNegotiation = "";
  public static       String A_zimbraMobilePolicyAllowSMIMESoftCerts = "";
  public static       String A_zimbraMobilePolicyAllowStorageCard = "";
  public static       String A_zimbraMobilePolicyAllowTextMessaging = "";
  public static       String A_zimbraMobilePolicyAllowUnsignedApplications = "";
  public static       String A_zimbraMobilePolicyAllowUnsignedInstallationPackages = "";
  public static       String A_zimbraMobilePolicyAllowWiFi = "";
  public static       String A_zimbraMobilePolicyMaxCalendarAgeFilter = "";
  public static       String A_zimbraMobilePolicyMaxEmailAgeFilter = "";
  public static       String A_zimbraMobilePolicyMaxEmailBodyTruncationSize = "";
  public static       String A_zimbraMobilePolicyMaxEmailHTMLBodyTruncationSize = "";
  public static       String A_zimbraMobilePolicyRequireDeviceEncryption = "";
  public static       String A_zimbraMobilePolicyRequireEncryptedSMIMEMessages = "";
  public static       String A_zimbraMobilePolicyRequireEncryptionSMIMEAlgorithm = "";
  public static       String A_zimbraMobilePolicyRequireManualSyncWhenRoaming = "";
  public static       String A_zimbraMobilePolicyRequireSignedSMIMEAlgorithm = "";
  public static       String A_zimbraMobilePolicyRequireSignedSMIMEMessages = "";
  public static       String A_zimbraMobilePolicySuppressDeviceEncryption = "";
  /* $endif $ */

  /* $if ZimbraVersion >= 7.0.0 $ */
  public static String A_zimbraMailOutgoingSieveScript = Provisioning.A_zimbraMailOutgoingSieveScript;
  /* $else $
  public static       String A_zimbraMailOutgoingSieveScript            = "";
  /* $endif $ */

  public static String A_zimbraACE                                            = Provisioning.A_zimbraACE;
  public static String A_zimbraDomainCOSMaxAccounts                           = Provisioning.A_zimbraDomainCOSMaxAccounts;
  public static String A_zimbraAdminConsoleUIComponents                       = Provisioning.A_zimbraAdminConsoleUIComponents;
  public static String A_zimbraDomainMaxAccounts                              = Provisioning.A_zimbraDomainMaxAccounts;
  public static String A_zimbraIsDelegatedAdminAccount                        = Provisioning.A_zimbraIsDelegatedAdminAccount;
  public static String A_zimbraDomainAdminMaxMailQuota                        = Provisioning.A_zimbraDomainAdminMaxMailQuota;
  public static String A_zimbraMailCanonicalAddress                           = Provisioning.A_zimbraMailCanonicalAddress;
  public static String A_zimbraMailHost                                       = Provisioning.A_zimbraMailHost;
  public static String A_zimbraId                                             = Provisioning.A_zimbraId;
  public static String A_userPassword                                         = Provisioning.A_userPassword;
  public static String A_zimbraPasswordModifiedTime                           = Provisioning.A_zimbraPasswordModifiedTime;
  public static String A_zimbraMailTransport                                  = Provisioning.A_zimbraMailTransport;
  public static String A_mail                                                 = Provisioning.A_mail;
  public static String A_zimbraMailDeliveryAddress                            = Provisioning.A_zimbraMailDeliveryAddress;
  public static String A_zimbraMailAlias                                      = Provisioning.A_zimbraMailAlias;
  public static String A_zimbraHideInGal                                      = Provisioning.A_zimbraHideInGal;
  public static String A_zimbraIsAdminAccount                                 = Provisioning.A_zimbraIsAdminAccount;
  public static String A_zimbraIsDomainAdminAccount                           = Provisioning.A_zimbraIsDomainAdminAccount;
  public static String A_zimbraLastLogonTimestamp                             = Provisioning.A_zimbraLastLogonTimestamp;
  public static String A_zimbraPrefIdentityName                               = Provisioning.A_zimbraPrefIdentityName;
  public static String A_zimbraPrefWhenInFolderIds                            = Provisioning.A_zimbraPrefWhenInFolderIds;
  public static String A_zimbraPrefIdentityId                                 = Provisioning.A_zimbraPrefIdentityId;
  public static String A_zimbraCreateTimestamp                                = Provisioning.A_zimbraCreateTimestamp;
  public static String A_zimbraDataSourceId                                   = Provisioning.A_zimbraDataSourceId;
  public static String A_zimbraDataSourceName                                 = Provisioning.A_zimbraDataSourceName;
  public static String A_zimbraDataSourceFolderId                             = Provisioning.A_zimbraDataSourceFolderId;
  public static String A_zimbraDataSourcePassword                             = Provisioning.A_zimbraDataSourcePassword;
  public static String A_zimbraDomainName                                     = Provisioning.A_zimbraDomainName;
  public static String A_zimbraGalAccountId                                   = Provisioning.A_zimbraGalAccountId;
  public static String A_zimbraDomainDefaultCOSId                             = Provisioning.A_zimbraDomainDefaultCOSId;
  public static String A_zimbraDomainAliasTargetId                            = Provisioning.A_zimbraDomainAliasTargetId;
  public static String A_zimbraDomainType                                     = Provisioning.A_zimbraDomainType;
  public static String A_cn                                                   = Provisioning.A_cn;
  public static String A_zimbraMailHostPool                                   = Provisioning.A_zimbraMailHostPool;
  public static String A_zimbraShareInfo                                      = Provisioning.A_zimbraShareInfo;
  public static String A_zimbraDataSourceType                                 = Provisioning.A_zimbraDataSourceType;
  public static String A_zimbraCOSId                                          = Provisioning.A_zimbraCOSId;
  public static String A_zimbraChildAccount                                   = Provisioning.A_zimbraChildAccount;
  public static String A_zimbraPrefChildVisibleAccount                        = Provisioning.A_zimbraPrefChildVisibleAccount;
  public static String A_zimbraChildVisibleAccount                            = Provisioning.A_zimbraChildVisibleAccount;
  public static String A_zimbraInterceptAddress                               = Provisioning.A_zimbraInterceptAddress;
  public static String A_zimbraMailQuota                                      = Provisioning.A_zimbraMailQuota;
  public static String A_zimbraPrefDefaultSignatureId                         = Provisioning.A_zimbraPrefDefaultSignatureId;
  public static String A_zimbraSignatureName                                  = Provisioning.A_zimbraSignatureName;
  public static String A_zimbraSignatureId                                    = Provisioning.A_zimbraSignatureId;
  public static String A_zimbraMailSieveScript                                = Provisioning.A_zimbraMailSieveScript;
  public static String A_zimbraAllowFromAddress                               = Provisioning.A_zimbraAllowFromAddress;
  public static String A_zimbraAccountStatus                                  = Provisioning.A_zimbraAccountStatus;
  public static String A_zimbraSpamIsSpamAccount                              = Provisioning.A_zimbraSpamIsSpamAccount;
  public static String A_zimbraServiceHostname                                = Provisioning.A_zimbraServiceHostname;
  public static String A_objectClass                                          = Provisioning.A_objectClass;
  public static String A_zimbraZimletPriority                                 = Provisioning.A_zimbraZimletPriority;
  public static String SERVICE_MAILBOX                                        = Provisioning.SERVICE_MAILBOX;
  public static String A_zimbraAdminPort                                      = Provisioning.A_zimbraAdminPort;
  public static String A_zimbraNotebookAccount                                = Provisioning.A_zimbraNotebookAccount;
  public static String A_zimbraNotes                                          = Provisioning.A_zimbraNotes;
  public static String A_zimbraFeatureMobileSyncEnabled                       = Provisioning.A_zimbraFeatureMobileSyncEnabled;
  public static String A_zimbraHttpProxyURL                                   = Provisioning.A_zimbraHttpProxyURL;
  public static String A_zimbraMobilePolicyPasswordRecoveryEnabled            = Provisioning.A_zimbraMobilePolicyPasswordRecoveryEnabled;
  public static String A_zimbraMobilePolicyMinDevicePasswordLength            = Provisioning.A_zimbraMobilePolicyMinDevicePasswordLength;
  public static String A_zimbraMobilePolicyMinDevicePasswordComplexCharacters = Provisioning.A_zimbraMobilePolicyMinDevicePasswordComplexCharacters;
  public static String A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts    = Provisioning.A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts;
  public static String A_zimbraMobilePolicyAllowSimpleDevicePassword          = Provisioning.A_zimbraMobilePolicyAllowSimpleDevicePassword;
  public static String A_zimbraMobilePolicyAlphanumericDevicePasswordRequired = Provisioning.A_zimbraMobilePolicyAlphanumericDevicePasswordRequired;
  public static String A_zimbraMobilePolicyDevicePasswordExpiration           = Provisioning.A_zimbraMobilePolicyDevicePasswordExpiration;
  public static String A_zimbraMobilePolicyDevicePasswordHistory              = Provisioning.A_zimbraMobilePolicyDevicePasswordHistory;
  public static String A_zimbraMobilePolicyMaxInactivityTimeDeviceLock        = Provisioning.A_zimbraMobilePolicyMaxInactivityTimeDeviceLock;
  public static String A_zimbraPrefMailDefaultCharset                         = Provisioning.A_zimbraPrefMailDefaultCharset;
  public static String A_zimbraHsmPolicy                                      = Provisioning.A_zimbraHsmPolicy;
  public static String A_zimbraDefaultDomainName                              = Provisioning.A_zimbraDefaultDomainName;

  public final Provisioning mProvisioning;

  private final NamedEntryWrapper<ZEAccount> mNamedEntryAccountWrapper;
  private final NamedEntryWrapper<ZEDomain>  mNamedEntryDomainWrapper;
  private final static String[] mAccountAttrs = {
    Provisioning.A_c,
    Provisioning.A_cn,
    Provisioning.A_co,

  };

  public ZEProvisioning()
  {
    this(Provisioning.getInstance());
  }

  public ZEProvisioning(Object provisioning)
  {
    mProvisioning = (Provisioning)provisioning;

    mNamedEntryAccountWrapper = new NamedEntryWrapper<ZEAccount>()
    {
      @Override
      public ZEAccount wrap(NamedEntry entry)
      {
        return new ZEAccount((com.zimbra.cs.account.Account) entry);
      }
    };

    mNamedEntryDomainWrapper = new NamedEntryWrapper<ZEDomain>()
    {
      @Override
      public ZEDomain wrap(NamedEntry entry)
      {
        return new ZEDomain((com.zimbra.cs.account.Domain) entry);
      }
    };
  }

  public boolean isValidUid(String uid)
  {
    return uid.length() == 36 &&
      (uid.charAt(8) == '-' &&
        uid.charAt(13) == '-' &&
        uid.charAt(18) == '-' &&
        uid.charAt(23) == '-');
  }

  public ZEAccount getZimbraUser() throws ZimbraException
  {
    return getAccountById(ZIMBRA_USER_ID);
  }

  public ZEOperationContext createZContext()
  {
    return new ZEOperationContext(
      new OperationContext(
        getZimbraUser().toZimbra(Account.class),
        true
      )
    );
  }

  public ZEDistributionList getDistributionListById(String id)
    throws ZimbraException
  {
    try
    {
      DistributionList distributionList = null;
/* $if MajorZimbraVersion >= 8 $ */
      distributionList = mProvisioning.get(ZEKey.ZEDistributionListBy.id.toZimbra(), id);
/* $else$
      distributionList = mProvisioning.getDistributionListById(id);
   $endif$ */
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new ZEDistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEDistributionList getDistributionListByName(String name)
    throws ZimbraException
  {
    try
    {
      DistributionList distributionList = null;
/* $if MajorZimbraVersion >= 8 $ */
      distributionList = mProvisioning.get(ZEKey.ZEDistributionListBy.name.toZimbra(), name);
/* $else$
      distributionList = mProvisioning.getDistributionListByName(name);
   $endif$ */
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new ZEDistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void visitAllAccounts(SimpleVisitor<ZEAccount> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<ZEAccount>(visitor, mNamedEntryAccountWrapper);
    try
    {
    /* $if MajorZimbraVersion >= 8 $ */
      SearchDirectoryOptions searchOptions = new SearchDirectoryOptions();
      ZLdapFilterFactory zldapFilterFactory = ZLdapFilterFactory.getInstance();
      searchOptions.setTypes(SearchDirectoryOptions.ObjectType.accounts);
      searchOptions.setFilter(zldapFilterFactory.allAccountsOnly());
      searchOptions.setMakeObjectOpt(SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);
      mProvisioning.searchDirectory(searchOptions, namedEntryVisitor);
    /* $else$
      Provisioning.SearchOptions searchOptions = new Provisioning.SearchOptions();
      searchOptions.setFlags(Provisioning.SO_NO_ACCOUNT_DEFAULTS);
      for(Server server : mProvisioning.getAllServers())
      {
        mProvisioning.searchAccountsOnServer(server, searchOptions, namedEntryVisitor);
      }
    $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void visitAllLocalAccounts(SimpleVisitor<ZEAccount> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<ZEAccount>(visitor, mNamedEntryAccountWrapper);
    try
    {
      Server server = mProvisioning.getLocalServer();

      /* $if MajorZimbraVersion >= 8 $ */
      SearchAccountsOptions searchOptions = new SearchAccountsOptions();
      searchOptions.setIncludeType(SearchAccountsOptions.IncludeType.ACCOUNTS_AND_CALENDAR_RESOURCES);
      searchOptions.setMakeObjectOpt(SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);
      /* $else$
      Provisioning.SearchOptions searchOptions = new Provisioning.SearchOptions();
      searchOptions.setFlags(Provisioning.SO_NO_ACCOUNT_DEFAULTS);
      $endif$ */

      mProvisioning.searchAccountsOnServer(server, searchOptions, namedEntryVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void visitAllAccounts(SimpleVisitor<ZEAccount> visitor, Filter<ZEAccount> filterAccounts)
    throws ZimbraException
  {
    ProvisioningVisitor<ZEAccount> accountProvisioningVisitor = new ProvisioningVisitor<ZEAccount>(
      visitor,
      filterAccounts
    );

    visitAllAccounts(accountProvisioningVisitor);
  }

  public void visitAllLocalAccountsSlow(
    SimpleVisitor<ZEAccount> visitor,
    Filter<ZEAccount> filterAccounts
  )
    throws ZimbraException
  {
    final List<ZEAccount> allAccounts = new ArrayList<ZEAccount>();
    SimpleVisitor<ZEAccount> accountListBuilder = new SimpleVisitor<ZEAccount>()
    {
      @Override
      public void visit(ZEAccount entry)
      {
        allAccounts.add(entry);
      }
    };
    ProvisioningVisitor<ZEAccount> accountListBuilderVisitor = new ProvisioningVisitor<ZEAccount>(
      accountListBuilder,
      filterAccounts
    );
    visitAllLocalAccounts(accountListBuilderVisitor);

    for (ZEAccount account : allAccounts)
    {
      visitor.visit(account);
    }
  }

  public void visitAllDomains(SimpleVisitor<ZEDomain> visitor) throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<ZEDomain>(visitor, mNamedEntryDomainWrapper);
    try
    {
      mProvisioning.getAllDomains(namedEntryVisitor, null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void visitDomain(SimpleVisitor<ZEAccount> visitor, ZEDomain domain) throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<ZEAccount>(visitor, mNamedEntryAccountWrapper);
    try
    {
      mProvisioning.getAllAccounts(
        domain.toZimbra(Domain.class),
        namedEntryVisitor
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Collection<String> getGroupMembers(String list) throws UnableToFindDistributionListException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      Group distributionList = mProvisioning.getGroup(ZEKey.ZEDistributionListBy.name.toZimbra(), list);
      if (distributionList == null)
      {
        throw ExceptionWrapper.createUnableToFindDistributionList(list);
      }
      return distributionList.getAllMembersSet();
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.createUnableToFindDistributionList(list, e);
    }
    /* $else $
    return getDistributionListByName(list).getAllMembersSet();
    /* $endif $ */
  }

  public void authAccount(ZEAccount account, String password, Protocol protocol, Map<String, Object> context)
    throws ZimbraException
  {
    try
    {
      mProvisioning.authAccount(
        account.toZimbra(Account.class),
        password,
        protocol.toZimbra(),
        context
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEAccount getAccountById(String accountId)
    throws ZimbraException
  {
    try
    {
      Account account = mProvisioning.getAccountById(accountId);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new ZEAccount(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEServer getLocalServer()
    throws ZimbraException
  {
    try
    {
      Server server = mProvisioning.getLocalServer();
      if (server == null)
      {
        return null;
      }
      else
      {
        return new ZEServer(server);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEDomain getDomainByName(String domainName)
    throws ZimbraException
  {
    try
    {
      Domain domain = mProvisioning.getDomainByName(domainName);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new ZEDomain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEDomain> getAllDomains()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapDomain(mProvisioning.getAllDomains());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEZimlet getZimlet(String zimletName)
    throws ZimbraException
  {
    try
    {
      Zimlet zimlet = mProvisioning.getZimlet(zimletName);
      if (zimlet == null)
      {
        return null;
      }
      else
      {
        return new ZEZimlet(zimlet);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyAttrs(ZEEntry entry, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      mProvisioning.modifyAttrs(entry.getProxiedEntry(), attrs);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEDomain getDomainById(String domainId)
    throws ZimbraException
  {
    try
    {
      Domain domain = mProvisioning.getDomainById(domainId);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new ZEDomain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEDistributionList> getAllDistributionLists(ZEDomain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapDistributionLists(
        mProvisioning.getAllDistributionLists(domain.toZimbra(Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZECos getCosById(String cosId)
    throws ZimbraException
  {
    try
    {
      Cos cos = mProvisioning.getCosById(cosId);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new ZECos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZECos> getAllCos()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapCoses(mProvisioning.getAllCos());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }


  @Nullable
  public ZECos getCosByName(String cosStr)
    throws ZimbraException
  {
    try
    {
      Cos cos = mProvisioning.getCosByName(cosStr);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new ZECos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEDistributionList get(ZEKey.ZEDistributionListBy id, String dlStr)
    throws ZimbraException
  {
    try
    {
      DistributionList distributionList = mProvisioning.get(id.toZimbra(), dlStr);
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new ZEDistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEAccount get(ZEKey.ZEAccountBy by, String target)
    throws ZimbraException
  {
    try
    {
      Account account = mProvisioning.get(by.toZimbra(), target);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new ZEAccount(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZEAccount getAccountByName(String accountStr)
    throws NoSuchAccountException
  {
    try
    {
      Account account = mProvisioning.getAccountByName(accountStr);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new ZEAccount(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEAccount> getAllAdminAccounts()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapAccounts(mProvisioning.getAllAdminAccounts());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEAccount> getAllAccounts(ZEDomain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapAccounts(
        mProvisioning.getAllAccounts(domain.toZimbra(Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEServer> getAllServers()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapServers(mProvisioning.getAllServers());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEServer> getAllServers(String service)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapServers(mProvisioning.getAllServers(service));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZECalendarResource> getAllCalendarResources(ZEDomain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapCalendarResources(
        mProvisioning.getAllCalendarResources(domain.toZimbra(Domain.class))
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEZimlet> listAllZimlets()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapZimlets(mProvisioning.listAllZimlets());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEXMPPComponent> getAllXMPPComponents()
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapXmppComponents(mProvisioning.getAllXMPPComponents());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEGlobalGrant getGlobalGrant()
    throws ZimbraException
  {
    try
    {
      GlobalGrant globalGrant = mProvisioning.getGlobalGrant();
      if (globalGrant == null)
      {
        return null;
      }
      else
      {
        return new ZEGlobalGrant(globalGrant);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEConfig getConfig()
    throws ZimbraException
  {
    try
    {
      Config config = mProvisioning.getConfig();
      if (config == null)
      {
        return null;
      }
      else
      {
        return new ZEConfig(config);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public List<ZEUCService> getAllUCServices()
    throws ZimbraException
  {
    /* $if MajorZimbraVersion >= 8 $ */
    try
    {
      return ZimbraListWrapper.wrapUCServices(mProvisioning.getAllUCServices());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public ZECalendarResource getCalendarResourceByName(String resourceName)
    throws ZimbraException
  {
    try
    {
      CalendarResource calendarResource = mProvisioning.getCalendarResourceByName(resourceName);
      if (calendarResource == null)
      {
        return null;
      }
      else
      {
        return new ZECalendarResource(calendarResource);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public ZECalendarResource getCalendarResourceById(String resourceId)
    throws ZimbraException
  {
    try
    {
      CalendarResource calendarResource = mProvisioning.getCalendarResourceById(resourceId);
      if (calendarResource == null)
      {
        return null;
      }
      else
      {
        return new ZECalendarResource(calendarResource);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEDomain createDomain(String currentDomainName, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      Domain domain = mProvisioning.createDomain(currentDomainName, stringObjectHashMap);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new ZEDomain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZECos createCos(String cosname, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      Cos cos = mProvisioning.createCos(cosname, stringObjectHashMap);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new ZECos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEDistributionList createDistributionList(String dlistName, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      DistributionList distributionList = mProvisioning.createDistributionList(dlistName, stringObjectHashMap);
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new ZEDistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEAccount createCalendarResource(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      Account calendar = mProvisioning.createCalendarResource(dstAccount, newPassword, attrs);
      if (calendar == null)
      {
        return null;
      }
      else
      {
        return new ZEAccount(calendar);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEAccount createAccount(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      Account account = mProvisioning.createAccount(dstAccount, newPassword, attrs);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new ZEAccount(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void modifyIdentity(ZEAccount newAccount, String identityName, Map<String, Object> newAttrs)
    throws ZimbraException
  {
    try
    {
      mProvisioning.modifyIdentity(
        newAccount.toZimbra(Account.class),
        identityName,
        newAttrs
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void grantRight(
    String targetType, ZETargetBy targetBy, String target,
    String granteeType, ZEGranteeBy granteeBy, String grantee,
    String right
  ) throws ZimbraException
  {
    /* $if MajorZimbraVersion == 8 $ */
    try
    {
      mProvisioning.grantRight(
        targetType,
        targetBy.toZimbra(TargetBy.class),
        target,
        granteeType,
        granteeBy.toZimbra(GranteeBy.class),
        grantee,
        null,
        right,
        null
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void revokeRight(
    String targetType, ZETargetBy targetBy, String target,
    String granteeType, ZEGranteeBy granteeBy, String grantee,
    String right
  ) throws NoSuchGrantException
  {
    /* $if MajorZimbraVersion == 8 $ */
    try
    {
      mProvisioning.revokeRight(
        targetType,
        targetBy.toZimbra(TargetBy.class),
        target,
        granteeType,
        granteeBy.toZimbra(GranteeBy.class),
        grantee,
        right,
        null
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mProvisioning);
  }

  @Nullable
  public ZEDomain getDomain(ZEAccount account)
    throws ZimbraException
  {
    try
    {
      Domain domain = mProvisioning.getDomain(account.toZimbra(Account.class));
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new ZEDomain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void flushCache(ZECacheEntryType cacheEntryType, Collection<ZECacheEntry> cacheEntries)
    throws ZimbraException
  {
    Provisioning.CacheEntry[] cacheEntriesArray = null;
    if (cacheEntries != null)
    {
      cacheEntriesArray = new Provisioning.CacheEntry[cacheEntries.size()];
      int i = 0;
      for (ZECacheEntry cacheEntry : cacheEntries)
      {
        cacheEntriesArray[i] = cacheEntry.toZimbra(Provisioning.CacheEntry.class);
        i++;
      }
    }

    try
    {
      mProvisioning.flushCache(cacheEntryType.getType(), cacheEntriesArray);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZECountAccountResult countAccount(ZEDomain domain)
    throws ZimbraException
  {
    try
    {
      return new ZECountAccountResult(
        mProvisioning.countAccount(domain.toZimbra(Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getAccountsOnCos(ZEDomain domain, ZECos cos)
  {
    ZECountAccountResult accountResult = countAccount(domain);
    for (ZECountAccountByCos accountByCos : accountResult.getCountAccountByCos())
    {
      if (accountByCos.getCosId().equals(cos.getId()))
      {
        return accountByCos.getCount();
      }
    }
    return -1;
  }

  public long getMaxAccountsOnCos(ZEDomain domain, ZECos cos)
  {
    final Collection<String> cosLimits = domain.getDomainCOSMaxAccounts();

    final String mCosId = cos.getId();

    for(final String cosLimit : cosLimits)
    {
      final String [] parts = cosLimit.split(":");

      if(parts.length != 2)
      {
        continue;
      }

      if(parts[0].equals(mCosId))
      {
        try
        {
          return Long.parseLong(parts[1]);
        }
        catch(NumberFormatException e)
        {
          return -1;
        }
      }
    }

    return -1;
  }

  @Nullable
  public ZEServer getServer(ZEAccount acct)
    throws ZimbraException
  {
    try
    {
      Server server = mProvisioning.getServer(acct.toZimbra(Account.class));
      if (server == null)
      {
        return null;
      }
      else
      {
        return new ZEServer(server);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public boolean onLocalServer(ZEAccount userAccount)
    throws ZimbraException
  {
    try
    {
      return mProvisioning.onLocalServer(userAccount.toZimbra(Account.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZEZimlet createZimlet(String name, Map<String, Object> attrs) throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      Zimlet zimlet = mProvisioning.createZimlet(name, attrs);
      if (zimlet == null)
      {
        return null;
      }
      else
      {
        return new ZEZimlet(zimlet);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public long getEffectiveQuota(ZEAccount account)
  {
    long acctQuota = account.getLongAttr(A_zimbraMailQuota, 0);
    ZEDomain domain = getDomain(account);
    long domainQuota = 0;
    if (domain != null)
    {
      domainQuota = domain.getLongAttr(A_zimbraMailDomainQuota, 0);
    }
    if (acctQuota == 0)
    {
      return domainQuota;
    }
    else if (domainQuota == 0)
    {
      return acctQuota;
    }
    else
    {
      return Math.min(acctQuota, domainQuota);
    }
  }

  public void setZimletPriority(String zimletName, int priority)
  {
    ZEZimlet zimlet = getZimlet(zimletName);
    Map<String, Object> attrs = zimlet.getAttrs(false);
    attrs.put(A_zimbraZimletPriority, String.valueOf(priority));
    modifyAttrs(zimlet, attrs);
  }

  public List<ZEAccount> getAllDelegatedAdminAccounts() throws ZimbraException
  {
    List<NamedEntry> entryList;
    /* $if MajorZimbraVersion >= 8 $ */
    SearchDirectoryOptions opts = new SearchDirectoryOptions();
    ZLdapFilterFactory zLdapFilterFactory = ZLdapFilterFactory.getInstance();
    /* $endif $ */
    try
    {
      /* $if MajorZimbraVersion >= 8 $ */
      ZLdapFilter filter = ZLdapFilterFactory.getInstance().fromFilterString(
        ZLdapFilterFactory.FilterId.ALL_ACCOUNTS_ONLY,
        zLdapFilterFactory.equalityFilter(A_zimbraIsDelegatedAdminAccount, "TRUE", true)
      );
      opts.setFilter(filter);
      opts.setTypes(SearchDirectoryOptions.ObjectType.accounts);
      entryList = mProvisioning.searchDirectory(opts);
      /* $else$
      entryList = mProvisioning.searchAccounts(
        "("+ZEProvisioning.A_zimbraIsDelegatedAdminAccount+"=TRUE)",
        new String[] { Provisioning.A_zimbraId },
        null,
        false,
        Provisioning.SA_ACCOUNT_FLAG
      );
      $endif$ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return ZimbraListWrapper.wrapAccounts(entryList);
  }

  @Nullable
  public ZEGroup getGroupById(String dlStr)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      Group group = mProvisioning.getGroup(Key.DistributionListBy.id, dlStr);
      if (group == null)
      {
        return null;
      }
      else
      {
        return new ZEGroup(group);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Nullable
  public ZEGroup getGroupByName(String dlStr)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      Group group = mProvisioning.getGroup(Key.DistributionListBy.name, dlStr);
      if (group == null)
      {
        return null;
      }
      else
      {
        return new ZEGroup(group);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void removeGranteeId(
    String target,
    String grantee_id,
    String granteeType,
    String right
  ) throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      // target
      Entry targetEntry = TargetType.lookupTarget(
        mProvisioning,
        TargetType.dl,
        TargetBy.name,
        target
      );

      Right r = RightManager.getInstance().getRight(right);

      Set<ZimbraACE> aces = new HashSet<ZimbraACE>();
      ZimbraACE ace = new ZimbraACE(
        grantee_id,
        GranteeType.fromCode(granteeType),
        r,
        null,
        null
      );
      aces.add(ace);

      List<ZimbraACE> revoked = ACLUtil.revokeRight(
        mProvisioning,
        targetEntry,
        aces
      );
      if (revoked.isEmpty())
      {
        throw AccountServiceException.NO_SUCH_GRANT(ace.dump(true));
      }
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public ZEGrants getGrants(
    ZETargetType targetType,
    ZETargetBy name,
    String targetName,
    boolean granteeIncludeGroupsGranteeBelongs
  )
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      RightCommand.Grants grants = mProvisioning.getGrants(
        targetType.getCode(),
        name.toZimbra(TargetBy.class),
        targetName,
        null,
        null,
        null,
        granteeIncludeGroupsGranteeBelongs
      );
      if (grants == null)
      {
        return null;
      }
      else
      {
        return new ZEGrants(grants);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public String getGranteeName(
    String grantee_id,
    String grantee_type
  ) throws ZimbraException
  {
    if( grantee_type.equals(GranteeType.GT_GROUP.getCode()) )
    {
      ZEDistributionList distributionList = getDistributionListById(grantee_id);
      return distributionList.getName();
    }
    else if ( grantee_type.equals(GranteeType.GT_USER.getCode()) )
    {
      ZEAccount granteeAccount = getAccountById(grantee_id);
      if ( granteeAccount == null )
      {
        throw new NoSuchAccountException(grantee_id);
      }
      return granteeAccount.getName();
    }

    throw new RuntimeException("Unknown grantee type: "+grantee_type);
  }

  public class ZECountAccountResult
  {
    private final Provisioning.CountAccountResult mCountAccountResult;

    protected ZECountAccountResult(Provisioning.CountAccountResult countAccountResult)
    {
      mCountAccountResult = countAccountResult;
    }

    public List<ZECountAccountByCos> getCountAccountByCos()
    {
      return ZimbraListWrapper.wrapCountAccountByCosList(mCountAccountResult.getCountAccountByCos());
    }
  }

  public static class ZECountAccountByCos
  {
    private final Provisioning.CountAccountResult.CountAccountByCos mCountAccountByCos;

    public ZECountAccountByCos(Provisioning.CountAccountResult.CountAccountByCos countAccountByCos)
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



  public static class GalSearchResult
  {
    private final LinkedList<ZEGalContact> mContactList;
    private       int                      mTotal;
    private boolean mHasMore;

    void setTotal(int total)
    {
      mTotal = total;
    }

    void setHasMore(boolean hasMore)
    {
      mHasMore = hasMore;
    }

    public static class ZEGalContact
    {
      private final GalContact mGalContact;

      ZEGalContact(GalContact galContact)
      {
        mGalContact = galContact;
      }

      public String getSingleAttr(String key)
      {
        return mGalContact.getSingleAttr(key);
      }

      public String getId()
      {
        return mGalContact.getId();
      }
    }

    GalSearchResult()
    {
      mContactList = new LinkedList<ZEGalContact>();
    }

    public List<ZEGalContact> getContactList()
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

    void addContact(ZEGalContact galContact)
    {
      mContactList.add(galContact);
    }
  }

  public GalSearchResult galSearch(ZEAccount account, String query, int skip, int limit)
  {
    GalSearchParams searchParams = new GalSearchParams(account.toZimbra(Account.class));

    searchParams.createSearchParams(query);
    searchParams.setQuery(query);
    searchParams.setLimit(limit);
    searchParams.setIdOnly(false);

/* $if MajorZimbraVersion >= 7 $ */
    searchParams.setType(GalSearchType.all);
/* $else$
    searchParams.setType(Provisioning.GAL_SEARCH_TYPE.ALL);
   $endif$ */

    GalSearchResult result = new GalSearchResult();
    GalSearchCallback callback = new GalSearchCallback(skip, searchParams, result);
    searchParams.setResultCallback(callback);

    //writes mResultList ans hasMore
    GalSearchControl searchControl = new GalSearchControl(searchParams);

    try
    {
      searchControl.autocomplete();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    if (result.hasMore())
    {
      result.setTotal(result.getTotal() + 1);
    }

    return result;
  }

  private static class GalSearchCallback extends GalSearchResultCallback
  {
    private final int             mSkip;
    private final GalSearchResult mSearchResult;
    private int mCounter = 0;


    GalSearchCallback(int skip, GalSearchParams params, GalSearchResult result)
    {
      super(params);
      mSkip = skip;
      mSearchResult = result;
    }

    public void handleContact(GalContact galContact) throws ZimbraException
    {
      if (mCounter >= mSkip)
      {
        mSearchResult.addContact(new GalSearchResult.ZEGalContact(galContact));
      }

      mCounter += 1;
      mSearchResult.setTotal(mCounter);
    }

    public void setHasMoreResult(boolean hasMore)
    {
      mSearchResult.setHasMore(hasMore);
    }

    public Element handleContact(Contact contact) throws ZimbraException
    {
      if (mCounter >= mSkip)
      {
        Map<String, Object> galAttrs = new HashMap<String, Object>();
        String tag_id = String.valueOf(contact.getId());

        Map<String, String> fields = contact.getFields();
        for (String key : fields.keySet())
        {
          galAttrs.put(key, fields.get(key));
        }

        GalContact galContact = new GalContact(tag_id, galAttrs);
        mSearchResult.addContact(new GalSearchResult.ZEGalContact(galContact));
      }
      mCounter += 1;
      mSearchResult.setTotal(mCounter);
      return null;
    }

    public void handleElement(Element node) throws ServiceException
    {
      if (mCounter >= mSkip)
      {
        Map<String, Object> galAttrs = new HashMap<String, Object>();
        String tag_id = node.getAttribute("id");
        List<Element> tagList = node.listElements("a");

        for (Element tag : tagList)
        {
          String tag_n = tag.getAttribute("n");
          galAttrs.put(tag_n, tag.getText());
        }

        GalContact galContact = new GalContact(tag_id, galAttrs);
        mSearchResult.addContact(new GalSearchResult.ZEGalContact(galContact));
      }
      mCounter += 1;
      mSearchResult.setTotal(mCounter);
    }
  }
}
