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

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.Filter;

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
/* $endif $ */

/* $if ZimbraVersion >= 8.0.0 && ZimbraVersion < 8.0.6 $
import com.zimbra.common.account.Key.GranteeBy;
   $endif$ */

/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $endif$ */

import com.zimbra.cs.mailbox.Contact;

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.provisioning.Group;

/* $if MajorZimbraVersion >= 8 $ */

/* $elseif MajorZimbraVersion >= 7 $
import com.zimbra.cs.account.Provisioning.GalSearchType;
$endif$ */

public class ProvisioningImp implements Provisioning
{

  /* $if ZimbraVersion >= 8.0.0 $ */
  public static String A_zimbraMailDomainQuota                                      = com.zimbra.cs.account.Provisioning.A_zimbraMailDomainQuota;
  public static String A_zimbraPrefAllowAddressForDelegatedSender                   = com.zimbra.cs.account.Provisioning.A_zimbraPrefAllowAddressForDelegatedSender;
  public static String DEFAULT_COS_NAME                                             = com.zimbra.cs.account.Provisioning.DEFAULT_COS_NAME;
  public static String DEFAULT_EXTERNAL_COS_NAME                                    = com.zimbra.cs.account.Provisioning.DEFAULT_EXTERNAL_COS_NAME;
  public static String A_zimbraMobilePolicyAllowBluetooth                           = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowBluetooth;
  public static String A_zimbraMobilePolicyAllowBrowser                             = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowBrowser;
  public static String A_zimbraMobilePolicyAllowCamera                              = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowCamera;
  public static String A_zimbraMobilePolicyAllowConsumerEmail                       = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowConsumerEmail;
  public static String A_zimbraMobilePolicyAllowDesktopSync                         = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowDesktopSync;
  public static String A_zimbraMobilePolicyAllowHTMLEmail                           = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowHTMLEmail;
  public static String A_zimbraMobilePolicyAllowInternetSharing                     = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowInternetSharing;
  public static String A_zimbraMobilePolicyAllowIrDA                                = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowIrDA;
  public static String A_zimbraMobilePolicyAllowPOPIMAPEmail                        = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowPOPIMAPEmail;
  public static String A_zimbraMobilePolicyAllowRemoteDesktop                       = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowRemoteDesktop;
  public static String A_zimbraMobilePolicyAllowSMIMEEncryptionAlgorithmNegotiation = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowSMIMEEncryptionAlgorithmNegotiation;
  public static String A_zimbraMobilePolicyAllowSMIMESoftCerts                      = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowSMIMESoftCerts;
  public static String A_zimbraMobilePolicyAllowStorageCard                         = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowStorageCard;
  public static String A_zimbraMobilePolicyAllowTextMessaging                       = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowTextMessaging;
  public static String A_zimbraMobilePolicyAllowUnsignedApplications                = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowUnsignedApplications;
  public static String A_zimbraMobilePolicyAllowUnsignedInstallationPackages        = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowUnsignedInstallationPackages;
  public static String A_zimbraMobilePolicyAllowWiFi                                = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowWiFi;
  public static String A_zimbraMobilePolicyMaxCalendarAgeFilter                     = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxCalendarAgeFilter;
  public static String A_zimbraMobilePolicyMaxEmailAgeFilter                        = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxEmailAgeFilter;
  public static String A_zimbraMobilePolicyMaxEmailBodyTruncationSize               = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxEmailBodyTruncationSize;
  public static String A_zimbraMobilePolicyMaxEmailHTMLBodyTruncationSize           = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxEmailHTMLBodyTruncationSize;
  public static String A_zimbraMobilePolicyRequireDeviceEncryption                  = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireDeviceEncryption;
  public static String A_zimbraMobilePolicyRequireEncryptedSMIMEMessages            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireEncryptedSMIMEMessages;
  public static String A_zimbraMobilePolicyRequireEncryptionSMIMEAlgorithm          = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireEncryptionSMIMEAlgorithm;
  public static String A_zimbraMobilePolicyRequireManualSyncWhenRoaming             = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireManualSyncWhenRoaming;
  public static String A_zimbraMobilePolicyRequireSignedSMIMEAlgorithm              = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireSignedSMIMEAlgorithm;
  public static String A_zimbraMobilePolicyRequireSignedSMIMEMessages               = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyRequireSignedSMIMEMessages;
  public static String A_zimbraMobilePolicySuppressDeviceEncryption                 = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicySuppressDeviceEncryption;
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
  public static String A_zimbraMailOutgoingSieveScript = com.zimbra.cs.account.Provisioning.A_zimbraMailOutgoingSieveScript;
  /* $else $
  public static       String A_zimbraMailOutgoingSieveScript            = "";
  /* $endif $ */

  public static String A_zimbraACE                                            = com.zimbra.cs.account.Provisioning.A_zimbraACE;
  public static String A_zimbraDomainCOSMaxAccounts                           = com.zimbra.cs.account.Provisioning.A_zimbraDomainCOSMaxAccounts;
  public static String A_zimbraAdminConsoleUIComponents                       = com.zimbra.cs.account.Provisioning.A_zimbraAdminConsoleUIComponents;
  public static String A_zimbraDomainMaxAccounts                              = com.zimbra.cs.account.Provisioning.A_zimbraDomainMaxAccounts;
  public static String A_zimbraIsDelegatedAdminAccount                        = com.zimbra.cs.account.Provisioning.A_zimbraIsDelegatedAdminAccount;
  public static String A_zimbraDomainAdminMaxMailQuota                        = com.zimbra.cs.account.Provisioning.A_zimbraDomainAdminMaxMailQuota;
  public static String A_zimbraMailCanonicalAddress                           = com.zimbra.cs.account.Provisioning.A_zimbraMailCanonicalAddress;
  public static String A_zimbraMailHost                                       = com.zimbra.cs.account.Provisioning.A_zimbraMailHost;
  public static String A_zimbraId                                             = com.zimbra.cs.account.Provisioning.A_zimbraId;
  public static String A_userPassword                                         = com.zimbra.cs.account.Provisioning.A_userPassword;
  public static String A_zimbraPasswordModifiedTime                           = com.zimbra.cs.account.Provisioning.A_zimbraPasswordModifiedTime;
  public static String A_zimbraMailTransport                                  = com.zimbra.cs.account.Provisioning.A_zimbraMailTransport;
  public static String A_mail                                                 = com.zimbra.cs.account.Provisioning.A_mail;
  public static String A_zimbraMailDeliveryAddress                            = com.zimbra.cs.account.Provisioning.A_zimbraMailDeliveryAddress;
  public static String A_zimbraMailAlias                                      = com.zimbra.cs.account.Provisioning.A_zimbraMailAlias;
  public static String A_zimbraHideInGal                                      = com.zimbra.cs.account.Provisioning.A_zimbraHideInGal;
  public static String A_zimbraIsAdminAccount                                 = com.zimbra.cs.account.Provisioning.A_zimbraIsAdminAccount;
  public static String A_zimbraIsDomainAdminAccount                           = com.zimbra.cs.account.Provisioning.A_zimbraIsDomainAdminAccount;
  public static String A_zimbraLastLogonTimestamp                             = com.zimbra.cs.account.Provisioning.A_zimbraLastLogonTimestamp;
  public static String A_zimbraPrefIdentityName                               = com.zimbra.cs.account.Provisioning.A_zimbraPrefIdentityName;
  public static String A_zimbraPrefWhenInFolderIds                            = com.zimbra.cs.account.Provisioning.A_zimbraPrefWhenInFolderIds;
  public static String A_zimbraPrefIdentityId                                 = com.zimbra.cs.account.Provisioning.A_zimbraPrefIdentityId;
  public static String A_zimbraCreateTimestamp                                = com.zimbra.cs.account.Provisioning.A_zimbraCreateTimestamp;
  public static String A_zimbraDataSourceId                                   = com.zimbra.cs.account.Provisioning.A_zimbraDataSourceId;
  public static String A_zimbraDataSourceName                                 = com.zimbra.cs.account.Provisioning.A_zimbraDataSourceName;
  public static String A_zimbraDataSourceFolderId                             = com.zimbra.cs.account.Provisioning.A_zimbraDataSourceFolderId;
  public static String A_zimbraDataSourcePassword                             = com.zimbra.cs.account.Provisioning.A_zimbraDataSourcePassword;
  public static String A_zimbraDomainName                                     = com.zimbra.cs.account.Provisioning.A_zimbraDomainName;
  public static String A_zimbraGalAccountId                                   = com.zimbra.cs.account.Provisioning.A_zimbraGalAccountId;
  public static String A_zimbraDomainDefaultCOSId                             = com.zimbra.cs.account.Provisioning.A_zimbraDomainDefaultCOSId;
  public static String A_zimbraDomainAliasTargetId                            = com.zimbra.cs.account.Provisioning.A_zimbraDomainAliasTargetId;
  public static String A_zimbraDomainType                                     = com.zimbra.cs.account.Provisioning.A_zimbraDomainType;
  public static String A_cn                                                   = com.zimbra.cs.account.Provisioning.A_cn;
  public static String A_zimbraMailHostPool                                   = com.zimbra.cs.account.Provisioning.A_zimbraMailHostPool;
  public static String A_zimbraShareInfo                                      = com.zimbra.cs.account.Provisioning.A_zimbraShareInfo;
  public static String A_zimbraDataSourceType                                 = com.zimbra.cs.account.Provisioning.A_zimbraDataSourceType;
  public static String A_zimbraCOSId                                          = com.zimbra.cs.account.Provisioning.A_zimbraCOSId;
  public static String A_zimbraChildAccount                                   = com.zimbra.cs.account.Provisioning.A_zimbraChildAccount;
  public static String A_zimbraPrefChildVisibleAccount                        = com.zimbra.cs.account.Provisioning.A_zimbraPrefChildVisibleAccount;
  public static String A_zimbraChildVisibleAccount                            = com.zimbra.cs.account.Provisioning.A_zimbraChildVisibleAccount;
  public static String A_zimbraInterceptAddress                               = com.zimbra.cs.account.Provisioning.A_zimbraInterceptAddress;
  public static String A_zimbraMailQuota                                      = com.zimbra.cs.account.Provisioning.A_zimbraMailQuota;
  public static String A_zimbraPrefDefaultSignatureId                         = com.zimbra.cs.account.Provisioning.A_zimbraPrefDefaultSignatureId;
  public static String A_zimbraSignatureName                                  = com.zimbra.cs.account.Provisioning.A_zimbraSignatureName;
  public static String A_zimbraSignatureId                                    = com.zimbra.cs.account.Provisioning.A_zimbraSignatureId;
  public static String A_zimbraMailSieveScript                                = com.zimbra.cs.account.Provisioning.A_zimbraMailSieveScript;
  public static String A_zimbraAllowFromAddress                               = com.zimbra.cs.account.Provisioning.A_zimbraAllowFromAddress;
  public static String A_zimbraAccountStatus                                  = com.zimbra.cs.account.Provisioning.A_zimbraAccountStatus;
  public static String A_zimbraSpamIsSpamAccount                              = com.zimbra.cs.account.Provisioning.A_zimbraSpamIsSpamAccount;
  public static String A_zimbraServiceHostname                                = com.zimbra.cs.account.Provisioning.A_zimbraServiceHostname;
  public static String A_objectClass                                          = com.zimbra.cs.account.Provisioning.A_objectClass;
  public static String A_zimbraZimletPriority                                 = com.zimbra.cs.account.Provisioning.A_zimbraZimletPriority;
  public static String SERVICE_MAILBOX                                        = com.zimbra.cs.account.Provisioning.SERVICE_MAILBOX;
  public static String A_zimbraAdminPort                                      = com.zimbra.cs.account.Provisioning.A_zimbraAdminPort;
  public static String A_zimbraNotebookAccount                                = com.zimbra.cs.account.Provisioning.A_zimbraNotebookAccount;
  public static String A_zimbraNotes                                          = com.zimbra.cs.account.Provisioning.A_zimbraNotes;
  public static String A_zimbraFeatureMobileSyncEnabled                       = com.zimbra.cs.account.Provisioning.A_zimbraFeatureMobileSyncEnabled;
  public static String A_zimbraHttpProxyURL                                   = com.zimbra.cs.account.Provisioning.A_zimbraHttpProxyURL;
  public static String A_zimbraMobilePolicyPasswordRecoveryEnabled            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyPasswordRecoveryEnabled;
  public static String A_zimbraMobilePolicyMinDevicePasswordLength            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMinDevicePasswordLength;
  public static String A_zimbraMobilePolicyMinDevicePasswordComplexCharacters = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMinDevicePasswordComplexCharacters;
  public static String A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts    = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts;
  public static String A_zimbraMobilePolicyAllowSimpleDevicePassword          = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowSimpleDevicePassword;
  public static String A_zimbraMobilePolicyAlphanumericDevicePasswordRequired = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAlphanumericDevicePasswordRequired;
  public static String A_zimbraMobilePolicyDevicePasswordExpiration           = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyDevicePasswordExpiration;
  public static String A_zimbraMobilePolicyDevicePasswordHistory              = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyDevicePasswordHistory;
  public static String A_zimbraMobilePolicyMaxInactivityTimeDeviceLock        = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxInactivityTimeDeviceLock;
  public static String A_zimbraPrefMailDefaultCharset                         = com.zimbra.cs.account.Provisioning.A_zimbraPrefMailDefaultCharset;
  public static String A_zimbraHsmPolicy                                      = com.zimbra.cs.account.Provisioning.A_zimbraHsmPolicy;
  public static String A_zimbraDefaultDomainName                              = com.zimbra.cs.account.Provisioning.A_zimbraDefaultDomainName;
  public static String A_zimbraPublicServiceHostname                          = com.zimbra.cs.account.Provisioning.A_zimbraPublicServiceHostname;
  public static String A_zimbraMailForwardingAddress                          = com.zimbra.cs.account.Provisioning.A_zimbraMailForwardingAddress;
  public static String A_zimbraGalLastSuccessfulSyncTimestamp                 = com.zimbra.cs.account.Provisioning.A_zimbraGalLastSuccessfulSyncTimestamp;
  public static String A_zimbraPrefFromAddress                                = com.zimbra.cs.account.Provisioning.A_zimbraPrefFromAddress;
  public static String A_zimbraPrefTimeZoneId                                 = com.zimbra.cs.account.Provisioning.A_zimbraPrefTimeZoneId;
  public static String A_zimbraPrefFromDisplay                                = com.zimbra.cs.account.Provisioning.A_zimbraPrefFromDisplay;
  public static int    DATASOURCE_PASSWORD_MAX_LENGTH                         = 128;

  @NotNull
  public final com.zimbra.cs.account.Provisioning mProvisioning;

  @NotNull
  private final NamedEntryWrapper<Account> mNamedEntryAccountWrapper;
  @NotNull
  private final NamedEntryWrapper<Domain>  mNamedEntryDomainWrapper;
  private final static String[] mAccountAttrs = {
    com.zimbra.cs.account.Provisioning.A_c,
    com.zimbra.cs.account.Provisioning.A_cn,
    com.zimbra.cs.account.Provisioning.A_co,

  };

  public ProvisioningImp()
  {
    this(com.zimbra.cs.account.Provisioning.getInstance());
  }

  public ProvisioningImp(Object provisioning)
  {
    mProvisioning = (com.zimbra.cs.account.Provisioning) provisioning;

    mNamedEntryAccountWrapper = new NamedEntryWrapper<Account>()
    {
      @NotNull
      @Override
      public Account wrap(NamedEntry entry)
      {
        return new Account((com.zimbra.cs.account.Account) entry);
      }
    };

    mNamedEntryDomainWrapper = new NamedEntryWrapper<Domain>()
    {
      @NotNull
      @Override
      public Domain wrap(NamedEntry entry)
      {
        return new Domain((com.zimbra.cs.account.Domain) entry);
      }
    };
  }

  @Override
  public boolean isValidUid(@NotNull String uid)
  {
    return uid.length() == 36 &&
      (uid.charAt(8) == '-' &&
        uid.charAt(13) == '-' &&
        uid.charAt(18) == '-' &&
        uid.charAt(23) == '-');
  }

  @Override
  @NotNull
  public Account getZimbraUser()
    throws ZimbraException
  {
    return assertAccountById(ZIMBRA_USER_ID);
  }

  @Override
  public OperationContext createZContext()
  {
    return new OperationContext(
      new com.zimbra.cs.mailbox.OperationContext(
        getZimbraUser().toZimbra(com.zimbra.cs.account.Account.class),
        true
      )
    );
  }

  @Override
  @Nullable
  public DistributionList getDistributionListById(String id)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.DistributionList distributionList = null;
/* $if MajorZimbraVersion >= 8 $ */
      distributionList = mProvisioning.get(ProvisioningKey.ByDistributionList.id.toZimbra(), id);
/* $else$
      distributionList = mProvisioning.getDistributionListById(id);
   $endif$ */
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new DistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public DistributionList getDistributionListByName(String name)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.DistributionList distributionList = null;
/* $if MajorZimbraVersion >= 8 $ */
      distributionList = mProvisioning.get(ProvisioningKey.ByDistributionList.name.toZimbra(), name);
/* $else$
      distributionList = mProvisioning.getDistributionListByName(name);
   $endif$ */
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new DistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllAccounts(@NotNull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
    /* $if MajorZimbraVersion >= 8 $ */
      com.zimbra.cs.account.SearchDirectoryOptions searchOptions = new com.zimbra.cs.account.SearchDirectoryOptions();
      ZLdapFilterFactory zldapFilterFactory = ZLdapFilterFactory.getInstance();
      searchOptions.setTypes(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.accounts);
      searchOptions.setFilter(zldapFilterFactory.allAccountsOnly());
      searchOptions.setMakeObjectOpt(
        com.zimbra.cs.account.SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS
      );
      mProvisioning.searchDirectory(searchOptions, namedEntryVisitor);
    /* $else$
      com.zimbra.cs.account.Provisioning.SearchOptions searchOptions = new com.zimbra.cs.account.Provisioning.SearchOptions();
      searchOptions.setFlags(
        com.zimbra.cs.account.Provisioning.SO_NO_ACCOUNT_DEFAULTS |
        com.zimbra.cs.account.Provisioning.SA_CALENDAR_RESOURCE_FLAG |
        com.zimbra.cs.account.Provisioning.SA_ACCOUNT_FLAG
      );
      for(com.zimbra.cs.account.Server server : mProvisioning.getAllServers())
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

  @Override
  public void visitAllLocalAccountsNoDefaults(@NotNull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getLocalServer();

      /* $if MajorZimbraVersion >= 8 $ */
      com.zimbra.cs.account.SearchAccountsOptions searchOptions = new com.zimbra.cs.account.SearchAccountsOptions();
      searchOptions.setIncludeType(com.zimbra.cs.account.SearchAccountsOptions.IncludeType.ACCOUNTS_AND_CALENDAR_RESOURCES);
      searchOptions.setMakeObjectOpt(com.zimbra.cs.account.SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);
      /* $else$
      com.zimbra.cs.account.Provisioning.SearchOptions searchOptions = new com.zimbra.cs.account.Provisioning.SearchOptions();
      searchOptions.setFlags(
        com.zimbra.cs.account.Provisioning.SO_NO_ACCOUNT_DEFAULTS |
        com.zimbra.cs.account.Provisioning.SA_CALENDAR_RESOURCE_FLAG |
        com.zimbra.cs.account.Provisioning.SA_ACCOUNT_FLAG
      );
      $endif$ */

      mProvisioning.searchAccountsOnServer(server, searchOptions, namedEntryVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllAccounts(@NotNull SimpleVisitor<Account> visitor, @NotNull Filter<Account> filterAccounts)
    throws ZimbraException
  {
    ProvisioningVisitor<Account> accountProvisioningVisitor = new ProvisioningVisitor<Account>(
      visitor,
      filterAccounts
    );

    visitAllAccounts(accountProvisioningVisitor);
  }

  @Override
  public void visitAllLocalAccountsSlow(
    @NotNull SimpleVisitor<Account> visitor,
    @NotNull Filter<Account> filterAccounts
  )
    throws ZimbraException
  {
    final List<Account> allAccounts = new ArrayList<Account>();
    SimpleVisitor<Account> accountListBuilder = new AbstractVisitor<Account>()
    {
      @Override
      public void visit(Account entry)
      {
        allAccounts.add(entry);
      }
    };
    ProvisioningVisitor<Account> accountListBuilderVisitor = new ProvisioningVisitor<Account>(
      accountListBuilder,
      filterAccounts
    );
    visitAllLocalAccountsNoDefaults(accountListBuilderVisitor);

    visitor.init();
    try {
      for (Account account : allAccounts) {
        visitor.visit(account);
      }
    } finally {
      visitor.finish();
    }
  }

  @Override
  public void visitAccountByIdNoDefaults(SimpleVisitor<Account> visitor, ZimbraId accountId)
  {
    try
    {
      ZimbraVisitorWrapper<Account> zimbraVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);

/* $if ZimbraVersion >= 8.0.0 $ */
      SearchDirectoryOptions searchOptions = new SearchDirectoryOptions();
      searchOptions.setMakeObjectOpt(SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);
      searchOptions.setTypes(
        SearchDirectoryOptions.ObjectType.accounts,
        SearchDirectoryOptions.ObjectType.resources
      );
      searchOptions.setFilter(ZLdapFilterFactory.getInstance().accountById(accountId.getId()));

      mProvisioning.searchDirectory(searchOptions, zimbraVisitor);
/* $else $
      String query = "(" + com.zimbra.cs.account.Provisioning.A_zimbraId + "=" + accountId.getId() + ")";
      com.zimbra.cs.account.Provisioning.SearchOptions searchOptions = new com.zimbra.cs.account.Provisioning.SearchOptions();
      searchOptions.setFlags(
          com.zimbra.cs.account.Provisioning.SO_NO_ACCOUNT_DEFAULTS |
          com.zimbra.cs.account.Provisioning.SA_CALENDAR_RESOURCE_FLAG |
          com.zimbra.cs.account.Provisioning.SA_ACCOUNT_FLAG
      );
      searchOptions.setQuery(query);
      List<NamedEntry> accounts = mProvisioning.searchDirectory(searchOptions);

      if (accounts.size() != 1)
      {
        return;
      }

      zimbraVisitor.visit(accounts.get(0));
/* $endif $ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllDomains(@NotNull SimpleVisitor<Domain> visitor) throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Domain>(visitor, mNamedEntryDomainWrapper);
    try
    {
      mProvisioning.getAllDomains(namedEntryVisitor, null);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitDomain(@NotNull SimpleVisitor<Account> visitor, @NotNull Domain domain) throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
      mProvisioning.getAllAccounts(
        domain.toZimbra(com.zimbra.cs.account.Domain.class),
        namedEntryVisitor
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Collection<String> getGroupMembers(String list) throws UnableToFindDistributionListException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      com.zimbra.cs.account.Group distributionList =
        mProvisioning.getGroup(ProvisioningKey.ByDistributionList.name.toZimbra(), list);
      if (distributionList == null)
      {
        throw ExceptionWrapper.createUnableToFindDistributionList(list);
      }
      return Arrays.asList(distributionList.getAllMembers());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.createUnableToFindDistributionList(list, e);
    }
    /* $else $
    return getDistributionListByName(list).getAllMembersSet();
    /* $endif $ */
  }

  @Override
  public void authAccount(@NotNull Account account, String password, @NotNull Protocol protocol, Map<String, Object> context)
    throws ZimbraException
  {
    try
    {
      mProvisioning.authAccount(
        account.toZimbra(com.zimbra.cs.account.Account.class),
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

  @Override
  public Account getAccountByAccountIdOrItemId(String id)
  {
    int index = id.indexOf("/");
    if (index > 0)
    {
      return getAccountById(id.substring(0, index));
    }
    else
    {
      return getAccountById(id);
    }
  }

  @Override
  @Nullable
  public Account getAccountById(String accountId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Account account = mProvisioning.getAccountById(accountId);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new Account(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @NotNull
  public Server getLocalServer()
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getLocalServer();
      if (server == null)
      {
        throw new RuntimeException();
      }
      else
      {
        return new Server(server);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Domain getDomainByName(String domainName)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Domain domain = mProvisioning.getDomainByName(domainName);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new Domain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<Domain> getAllDomains()
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

  @Override
  @NotNull
  public Zimlet getZimlet(String zimletName)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Zimlet zimlet = mProvisioning.getZimlet(zimletName);
      if (zimlet == null)
      {
        throw ExceptionWrapper.createNoSuchZimletException("Zimlet " + zimletName + " not found.");
      }
      else
      {
        return new Zimlet(zimlet);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.createNoSuchZimletException(e);
    }
  }

  @Override
  public void modifyAttrs(@NotNull Entry entry, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      mProvisioning.modifyAttrs(entry.toZimbra(), attrs);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Domain getDomainById(String domainId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Domain domain = mProvisioning.getDomainById(domainId);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new Domain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<DistributionList> getAllDistributionLists(@NotNull Domain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapDistributionLists(
        mProvisioning.getAllDistributionLists(domain.toZimbra(com.zimbra.cs.account.Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Cos getCosById(String cosId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Cos cos = mProvisioning.getCosById(cosId);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new Cos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<Cos> getAllCos()
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


  @Override
  @Nullable
  public Cos getCosByName(String cosStr)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Cos cos = mProvisioning.getCosByName(cosStr);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new Cos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public DistributionList get(@NotNull ProvisioningKey.ByDistributionList id, String dlStr)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.DistributionList distributionList = mProvisioning.get(id.toZimbra(), dlStr);
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new DistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Account get(@NotNull ProvisioningKey.ByAccount by, String target)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Account account = mProvisioning.get(by.toZimbra(), target);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new Account(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @NotNull
  public Account assertAccountByName(String accountStr)
    throws NoSuchAccountException
  {
    Account account = getAccountByName(accountStr);
    if (account == null)
    {
      throw new NoSuchAccountException(accountStr);
    }
    else
    {
      return account;
    }
  }

  @Override
  @NotNull
  public Account assertAccountById(String accountStr)
    throws NoSuchAccountException
  {
    Account account = getAccountById(accountStr);
    if (account == null)
    {
      throw new NoSuchAccountException(accountStr);
    }
    else
    {
      return account;
    }
  }

  @Override
  @Nullable
  public Account getAccountByName(String accountStr)
    throws NoSuchAccountException
  {
    try
    {
      com.zimbra.cs.account.Account account = mProvisioning.getAccountByName(accountStr);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new Account(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<Account> getAllAdminAccounts()
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

  @Override
  public List<Account> getAllAccounts(@NotNull Domain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapAccounts(
        mProvisioning.getAllAccounts(domain.toZimbra(com.zimbra.cs.account.Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<Server> getAllServers()
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

  @Override
  public List<Server> getAllServers(String service)
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

  @Override
  public List<CalendarResource> getAllCalendarResources(@NotNull Domain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapCalendarResources(
        mProvisioning.getAllCalendarResources(domain.toZimbra(com.zimbra.cs.account.Domain.class))
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<Zimlet> listAllZimlets()
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

  @Override
  public List<XMPPComponent> getAllXMPPComponents()
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

  @Override
  @Nullable
  public GlobalGrant getGlobalGrant()
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.GlobalGrant globalGrant = mProvisioning.getGlobalGrant();
      if (globalGrant == null)
      {
        return null;
      }
      else
      {
        return new GlobalGrant(globalGrant);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @NotNull
  public Config getConfig()
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Config config = mProvisioning.getConfig();
      if (config == null)
      {
        throw new RuntimeException("Unable to retrieve global config");
      }
      else
      {
        return new Config(config);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public List<UCService> getAllUCServices()
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

  @Override
  @Nullable
  public CalendarResource getCalendarResourceByName(String resourceName)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.CalendarResource calendarResource = mProvisioning.getCalendarResourceByName(resourceName);
      if (calendarResource == null)
      {
        return null;
      }
      else
      {
        return new CalendarResource(calendarResource);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public CalendarResource getCalendarResourceById(String resourceId)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.CalendarResource calendarResource = mProvisioning.getCalendarResourceById(resourceId);
      if (calendarResource == null)
      {
        return null;
      }
      else
      {
        return new CalendarResource(calendarResource);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Domain createDomain(String currentDomainName, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Domain domain = mProvisioning.createDomain(currentDomainName, stringObjectHashMap);
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new Domain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Cos createCos(String cosname, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Cos cos = mProvisioning.createCos(cosname, stringObjectHashMap);
      if (cos == null)
      {
        return null;
      }
      else
      {
        return new Cos(cos);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public DistributionList createDistributionList(String dlistName, HashMap<String, Object> stringObjectHashMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.DistributionList distributionList = mProvisioning.createDistributionList(dlistName, stringObjectHashMap);
      if (distributionList == null)
      {
        return null;
      }
      else
      {
        return new DistributionList(distributionList);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Account createCalendarResource(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Account calendar = mProvisioning.createCalendarResource(dstAccount, newPassword, attrs);
      if (calendar == null)
      {
        return null;
      }
      else
      {
        return new Account(calendar);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Account createAccount(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Account account = mProvisioning.createAccount(dstAccount, newPassword, attrs);
      if (account == null)
      {
        return null;
      }
      else
      {
        return new Account(account);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void modifyIdentity(@NotNull Account newAccount, String identityName, Map<String, Object> newAttrs)
    throws ZimbraException
  {
    try
    {
      mProvisioning.modifyIdentity(
        newAccount.toZimbra(com.zimbra.cs.account.Account.class),
        identityName,
        newAttrs
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void grantRight(
    String targetType, @NotNull TargetBy targetBy, String target,
    String granteeType, @NotNull GrantedBy granteeBy, String grantee,
    String right
  ) throws ZimbraException
  {
    /* $if MajorZimbraVersion == 8 $ */
    try
    {
      mProvisioning.grantRight(
        targetType,
        targetBy.toZimbra(com.zimbra.soap.type.TargetBy.class),
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

  @Override
  public void revokeRight(
    String targetType, @NotNull TargetBy targetBy, String target,
    String granteeType, @NotNull GrantedBy granteeBy, String grantee,
    String right
  ) throws NoSuchGrantException
  {
    /* $if MajorZimbraVersion == 8 $ */
    try
    {
      mProvisioning.revokeRight(
        targetType,
        targetBy.toZimbra(com.zimbra.soap.type.TargetBy.class),
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

  @Override
  public <T> T toZimbra(@NotNull Class<T> cls)
  {
    return cls.cast(mProvisioning);
  }

  @Override
  @Nullable
  public Domain getDomain(@NotNull Account account)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Domain domain = mProvisioning.getDomain(account.toZimbra(com.zimbra.cs.account.Account.class));
      if (domain == null)
      {
        return null;
      }
      else
      {
        return new Domain(domain);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void flushCache(@NotNull CacheEntryType cacheEntryType, @Nullable Collection<CacheEntry> cacheEntries)
    throws ZimbraException
  {
    com.zimbra.cs.account.Provisioning.CacheEntry[] cacheEntriesArray = null;
    if (cacheEntries != null)
    {
      cacheEntriesArray = new com.zimbra.cs.account.Provisioning.CacheEntry[cacheEntries.size()];
      int i = 0;
      for (CacheEntry cacheEntry : cacheEntries)
      {
        cacheEntriesArray[i] = cacheEntry.toZimbra(com.zimbra.cs.account.Provisioning.CacheEntry.class);
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

  @Override
  public CountAccountResult countAccount(@NotNull Domain domain)
    throws ZimbraException
  {
    try
    {
      return new CountAccountResult(
        mProvisioning.countAccount(domain.toZimbra(com.zimbra.cs.account.Domain.class))
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public long getAccountsOnCos(@NotNull Domain domain, @NotNull Cos cos)
  {
    CountAccountResult accountResult = countAccount(domain);
    for (CountAccountByCos accountByCos : accountResult.getCountAccountByCos())
    {
      if (accountByCos.getCosId().equals(cos.getId()))
      {
        return accountByCos.getCount();
      }
    }
    return -1;
  }

  @Override
  public long getMaxAccountsOnCos(@NotNull Domain domain, @NotNull Cos cos)
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

  @Override
  @Nullable
  public Server getServer(@NotNull Account acct)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getServer(acct.toZimbra(com.zimbra.cs.account.Account.class));
      if (server == null)
      {
        return null;
      }
      else
      {
        return new Server(server);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  @Override
  public Server getServerById(String id)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getServerById(id);
      if(server == null)
      {
        return null;
      }
      return new Server(server);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  @Override
  public Server getServerByName(String name)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getServerByName(name);
      if(server == null)
      {
        return null;
      }
      return new Server(server);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public boolean onLocalServer(@NotNull Account userAccount)
    throws ZimbraException
  {
    try
    {
      return mProvisioning.onLocalServer(userAccount.toZimbra(com.zimbra.cs.account.Account.class));
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Zimlet createZimlet(String name, Map<String, Object> attrs) throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Zimlet zimlet = mProvisioning.createZimlet(name, attrs);
      if (zimlet == null)
      {
        return null;
      }
      else
      {
        return new Zimlet(zimlet);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public long getEffectiveQuota(@NotNull Account account)
  {
    long acctQuota = account.getLongAttr(A_zimbraMailQuota, 0);
    Domain domain = getDomain(account);
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

  @Override
  public void setZimletPriority(String zimletName, int priority)
  {
    Zimlet zimlet = getZimlet(zimletName);
    Map<String, Object> attrs = zimlet.getAttrs(false);
    attrs.put(A_zimbraZimletPriority, String.valueOf(priority));
    modifyAttrs(zimlet, attrs);
  }

  @Override
  public List<Account> getAllDelegatedAdminAccounts() throws ZimbraException
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
        "(" + com.zimbra.cs.account.Provisioning.A_zimbraIsDelegatedAdminAccount + "=TRUE)",
        new String[] { com.zimbra.cs.account.Provisioning.A_zimbraId },
        null,
        false,
        com.zimbra.cs.account.Provisioning.SA_ACCOUNT_FLAG
      );
      $endif$ */
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }

    return ZimbraListWrapper.wrapAccounts(entryList);
  }

  @Override
  @Nullable
  public Group getGroupById(String dlStr)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      com.zimbra.cs.account.Group group = mProvisioning.getGroup(Key.DistributionListBy.id, dlStr);
      if (group == null)
      {
        return null;
      }
      else
      {
        return new Group(group);
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

  @Override
  @Nullable
  public Group getGroupByName(String dlStr)
    throws ZimbraException
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      com.zimbra.cs.account.Group group = mProvisioning.getGroup(Key.DistributionListBy.name, dlStr);
      if (group == null)
      {
        return null;
      }
      else
      {
        return new Group(group);
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

  @Override
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
      com.zimbra.cs.account.Entry targetEntry = com.zimbra.cs.account.accesscontrol.TargetType.lookupTarget(
        mProvisioning,
        com.zimbra.cs.account.accesscontrol.TargetType.dl,
        com.zimbra.soap.type.TargetBy.name,
        target
      );

      Right r = RightManager.getInstance().getRight(right);

      Set<ZimbraACE> aces = new HashSet<ZimbraACE>();
      ZimbraACE ace = new ZimbraACE(
        grantee_id,
        GranteeType.fromCode(granteeType).toZimbra(com.zimbra.cs.account.accesscontrol.GranteeType.class),
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

  @Override
  @Nullable
  public Grants getGrants(
    @NotNull org.openzal.zal.provisioning.TargetType targetType,
    @NotNull TargetBy name,
    String targetName,
    boolean granteeIncludeGroupsGranteeBelongs
  )
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    try
    {
      RightCommand.Grants grants = mProvisioning.getGrants(
        targetType.getCode(),
        name.toZimbra(com.zimbra.soap.type.TargetBy.class),
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
        return new Grants(grants);
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

  @Override
  public String getGranteeName(
    String grantee_id,
    @NotNull String grantee_type
  ) throws ZimbraException
  {
    if( grantee_type.equals(GranteeType.GT_GROUP.getCode()) )
    {
      DistributionList distributionList = getDistributionListById(grantee_id);
      return distributionList.getName();
    }
    else if ( grantee_type.equals(GranteeType.GT_USER.getCode()) )
    {
      Account granteeAccount = getAccountById(grantee_id);
      if ( granteeAccount == null )
      {
        throw new NoSuchAccountException(grantee_id);
      }
      return granteeAccount.getName();
    }

    throw new RuntimeException("Unknown grantee type: "+grantee_type);
  }

  @Override
  @NotNull
  public GalSearchResult galSearch(@NotNull Account account, String query, int skip, int limit)
  {
    GalSearchParams searchParams = new GalSearchParams(account.toZimbra(com.zimbra.cs.account.Account.class));

    searchParams.createSearchParams(query);
    searchParams.setQuery(query);
    searchParams.setLimit(limit);
    searchParams.setIdOnly(false);

/* $if MajorZimbraVersion >= 7 $ */
    searchParams.setType(GalSearchType.all);
/* $else$
    searchParams.setType(com.zimbra.cs.account.Provisioning.GAL_SEARCH_TYPE.ALL);
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

  @NotNull
  public DistributionList assertDistributionListById(String targetId)
  {
    DistributionList distributionList = getDistributionListById(targetId);
    if (distributionList == null)
    {
      throw new NoSuchDistributionListException(targetId);
    }
    else
    {
      return distributionList;
    }
  }

  @Override
  public void deleteAccountByName(String name)
  {
    try
    {
      Account account = getAccountByName(name);
      if( account != null)
      {
        mProvisioning.deleteAccount(account.getId());
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void deleteAccountById(String id)
  {
    try
    {
      mProvisioning.deleteAccount(id);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
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

    public void handleContact(GalContact galContact)
      throws ZimbraException
    {
      if (mCounter >= mSkip)
      {
        mSearchResult.addContact(new GalSearchResult.GalContact(galContact));
      }

      mCounter += 1;
      mSearchResult.setTotal(mCounter);
    }

    public void setHasMoreResult(boolean hasMore)
    {
      mSearchResult.setHasMore(hasMore);
    }

    @Nullable
    public Element handleContact(@NotNull Contact contact)
      throws ZimbraException
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
        mSearchResult.addContact(new GalSearchResult.GalContact(galContact));
      }
      mCounter += 1;
      mSearchResult.setTotal(mCounter);
      return null;
    }

    public void handleElement(@NotNull Element node)
    {
      if (mCounter >= mSkip)
      {
        Map<String, Object> galAttrs = new HashMap<String, Object>();
        String tag_id;
        try
        {
          tag_id = node.getAttribute("id");
        }
        catch (ServiceException e)
        {
          throw ExceptionWrapper.wrap(e);
        }
        List<Element> tagList = node.listElements("a");

        for (Element tag : tagList)
        {
          String tag_n;
          try
          {
            tag_n = tag.getAttribute("n");
          }
          catch (ServiceException e)
          {
            throw ExceptionWrapper.wrap(e);
          }
          galAttrs.put(tag_n, tag.getText());
        }

        GalContact galContact = new GalContact(tag_id, galAttrs);
        mSearchResult.addContact(new GalSearchResult.GalContact(galContact));
      }
      mCounter += 1;
      mSearchResult.setTotal(mCounter);
    }
  }

  public Collection<Domain> getDomainAliases(Domain domain)
  {
    if (domain.isAliasDomain())
    {
      return Collections.emptyList();
    }

    DomainAliasesVisitor visitor = new DomainAliasesVisitor(domain);
    visitAllDomains(visitor);
    return visitor.getAliases();
  }
}
