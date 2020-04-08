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

import com.zimbra.cs.ldap.LdapConstants;
import com.zimbra.cs.mailbox.ACL;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.memcached.ZimbraMemcachedClient;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.ldap.LdapClient;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.LdapServerType;
import com.zimbra.cs.ldap.LdapUsage;
import com.zimbra.cs.ldap.LdapUtil;
import com.zimbra.cs.ldap.ZLdapContext;
import com.zimbra.cs.ldap.ZMutableEntry;
import com.zimbra.cs.ldap.ZSearchControls;
import com.zimbra.cs.ldap.ZSearchScope;
import com.zimbra.cs.ldap.unboundid.UBIDLdapContext;
import com.zimbra.cs.util.ProxyPurgeUtil;
import javax.annotation.Nonnull;

import com.zimbra.soap.ZimbraSoapContext;
import org.dom4j.QName;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lib.Filter;

import com.zimbra.cs.account.*;
import com.zimbra.cs.account.accesscontrol.*;
import com.zimbra.cs.gal.GalSearchControl;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.cs.gal.GalSearchResultCallback;
import com.zimbra.common.soap.Element;
import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import com.zimbra.soap.type.GalSearchType;
import com.zimbra.soap.type.TargetBy;
/* $if ZimbraVersion < 8.0.6 $
import com.zimbra.common.account.Key.GranteeBy;
/* $endif$ */

/* $if ZimbraVersion >= 8.0.6 $*/
import com.zimbra.soap.admin.type.GranteeSelector.GranteeBy;
/* $endif$ */

import com.zimbra.cs.mailbox.Contact;

import javax.annotation.Nullable;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.provisioning.DirectQueryFilterBuilder;

public class ProvisioningImp implements Provisioning
{
  /* $if ZimbraVersion >= 8.7.0 $ */
  public static String A_zimbraMaxAppSpecificPasswords                        = com.zimbra.cs.account.Provisioning.A_zimbraMaxAppSpecificPasswords;
  public static String A_zimbraZimletUserPropertiesMaxNumEntries              = com.zimbra.cs.account.Provisioning.A_zimbraZimletUserPropertiesMaxNumEntries;
  public static String A_zimbraTwoFactorAuthEnabled                           = com.zimbra.cs.account.Provisioning.A_zimbraTwoFactorAuthEnabled;
  public static String A_zimbraTwoFactorAuthScratchCodes                      = com.zimbra.cs.account.Provisioning.A_zimbraTwoFactorAuthScratchCodes;
  public static String A_zimbraTwoFactorAuthSecret                            = com.zimbra.cs.account.Provisioning.A_zimbraTwoFactorAuthSecret;
  public static String A_zimbraAppSpecificPassword                            = com.zimbra.cs.account.Provisioning.A_zimbraAppSpecificPassword;
  public static String A_zimbraRevokeAppSpecificPasswordsOnPasswordChange     = com.zimbra.cs.account.Provisioning.A_zimbraRevokeAppSpecificPasswordsOnPasswordChange;
  public static String A_zimbraAppSpecificPasswordDuration                    = com.zimbra.cs.account.Provisioning.A_zimbraAppSpecificPasswordDuration;
  /* $else $
  public static String A_zimbraMaxAppSpecificPasswords                        = "";
  public static String A_zimbraZimletUserPropertiesMaxNumEntries              = "";
  public static String A_zimbraTwoFactorAuthEnabled                           = "";
  public static String A_zimbraTwoFactorAuthScratchCodes                      = "";
  public static String A_zimbraTwoFactorAuthSecret                            = "";
  public static String A_zimbraAppSpecificPassword                            = "";
  public static String A_zimbraRevokeAppSpecificPasswordsOnPasswordChange     = "";
  public static String A_zimbraAppSpecificPasswordDuration                    = "";
  /* $endif $ */

  public static String A_zimbraIsACLGroup                                           = com.zimbra.cs.account.Provisioning.A_zimbraIsACLGroup;
  public static String A_memberURL                                                  = com.zimbra.cs.account.Provisioning.A_memberURL;
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
  public static String A_zimbraMailOutgoingSieveScript                              = com.zimbra.cs.account.Provisioning.A_zimbraMailOutgoingSieveScript;
  public static String A_zimbraMailTrustedSenderListMaxNumEntries                   = com.zimbra.cs.account.Provisioning.A_zimbraMailTrustedSenderListMaxNumEntries;
  public static String A_zimbraIsExternalVirtualAccount                             = com.zimbra.cs.account.Provisioning.A_zimbraIsExternalVirtualAccount;
  public static String A_zimbraIsSystemAccount                                      = com.zimbra.cs.account.Provisioning.A_zimbraIsSystemAccount;
  public static String A_zimbraIsSystemResource                                     = com.zimbra.cs.account.Provisioning.A_zimbraIsSystemResource;
  public static String A_zimbraCalResType                                           = com.zimbra.cs.account.Provisioning.A_zimbraCalResType;
  /* $if ZimbraVersion >= 8.8.10 $ */
  public static String A_zimbraPrefDefaultCalendarId                                = com.zimbra.cs.account.Provisioning.A_zimbraPrefDefaultCalendarId;
/* $else$
  public static String A_zimbraPrefDefaultCalendarId                                = "";
/* $endif$ */


/* $if ZimbraVersion >= 8.5.0 $ */
  public static String A_zimbraAuthTokens                                     = com.zimbra.cs.account.Provisioning.A_zimbraAuthTokens;
/* $else$
  public static String A_zimbraAuthTokens                                     = "";
/* $endif$ */


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
  public static String A_zimbraLastLogonTimestampFrequency                    = com.zimbra.cs.account.Provisioning.A_zimbraLastLogonTimestampFrequency;
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
  public static String A_zimbraGivenName                                      = com.zimbra.cs.account.Provisioning.A_givenName;
  public static String A_zimbraSn                                             = com.zimbra.cs.account.Provisioning.A_sn;
  public static String A_zimbraDisplayName                                    = com.zimbra.cs.account.Provisioning.A_displayName;
  public static String A_zimbraCOSId                                          = com.zimbra.cs.account.Provisioning.A_zimbraCOSId;
  public static String A_zimbraPublicServiceProtocol                          = com.zimbra.cs.account.Provisioning.A_zimbraPublicServiceProtocol;
  public static String A_zimbraMyoneloginSamlSigningCert                      = com.zimbra.cs.account.Provisioning.A_zimbraMyoneloginSamlSigningCert;
  public static String A_zimbraChildAccount                                   = com.zimbra.cs.account.Provisioning.A_zimbraChildAccount;
  public static String A_zimbraPrefChildVisibleAccount                        = com.zimbra.cs.account.Provisioning.A_zimbraPrefChildVisibleAccount;
  public static String A_zimbraChildVisibleAccount                            = com.zimbra.cs.account.Provisioning.A_zimbraChildVisibleAccount;
  public static String A_zimbraInterceptAddress                               = com.zimbra.cs.account.Provisioning.A_zimbraInterceptAddress;
  public static String A_zimbraMailQuota                                      = com.zimbra.cs.account.Provisioning.A_zimbraMailQuota;
  public static String A_zimbraPrefDefaultSignatureId                         = com.zimbra.cs.account.Provisioning.A_zimbraPrefDefaultSignatureId;
  public static String A_zimbraPrefForwardReplySignatureId                    = com.zimbra.cs.account.Provisioning.A_zimbraPrefForwardReplySignatureId;
  public static String A_zimbraSignatureName                                  = com.zimbra.cs.account.Provisioning.A_zimbraSignatureName;
  public static String A_zimbraSignatureId                                    = com.zimbra.cs.account.Provisioning.A_zimbraSignatureId;
  public static String A_zimbraMailSieveScript                                = com.zimbra.cs.account.Provisioning.A_zimbraMailSieveScript;
  public static String A_zimbraAllowFromAddress                               = com.zimbra.cs.account.Provisioning.A_zimbraAllowFromAddress;
  public static String A_zimbraAccountStatus                                  = com.zimbra.cs.account.Provisioning.A_zimbraAccountStatus;
  public static String A_zimbraSpamIsSpamAccount                              = com.zimbra.cs.account.Provisioning.A_zimbraSpamIsSpamAccount;
  public static String A_zimbraSpamIsNotSpamAccount                           = com.zimbra.cs.account.Provisioning.A_zimbraSpamIsNotSpamAccount;
  public static String A_zimbraServiceHostname                                = com.zimbra.cs.account.Provisioning.A_zimbraServiceHostname;
  public static String A_objectClass                                          = com.zimbra.cs.account.Provisioning.A_objectClass;
  public static String A_zimbraZimletPriority                                 = com.zimbra.cs.account.Provisioning.A_zimbraZimletPriority;
  public static String A_zimbraZimletEnabled                                  = com.zimbra.cs.account.Provisioning.A_zimbraZimletEnabled;
  public static String SERVICE_MAILBOX                                        = com.zimbra.cs.account.Provisioning.SERVICE_MAILBOX;
  public static String A_zimbraAdminPort                                      = com.zimbra.cs.account.Provisioning.A_zimbraAdminPort;
  public static String A_zimbraNotebookAccount                                = com.zimbra.cs.account.Provisioning.A_zimbraNotebookAccount;
  public static String A_zimbraNotes                                          = com.zimbra.cs.account.Provisioning.A_zimbraNotes;
  public static String A_zimbraFeatureMobileSyncEnabled                       = com.zimbra.cs.account.Provisioning.A_zimbraFeatureMobileSyncEnabled;
  public static String A_zimbraHttpProxyURL                                   = com.zimbra.cs.account.Provisioning.A_zimbraHttpProxyURL;
  public static String A_zimbraAttachmentsBlocked                             = com.zimbra.cs.account.Provisioning.A_zimbraAttachmentsBlocked;
  public static String A_zimbraMobilePolicyPasswordRecoveryEnabled            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyPasswordRecoveryEnabled;
  public static String A_zimbraMobilePolicyMinDevicePasswordLength            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMinDevicePasswordLength;
  public static String A_zimbraMobilePolicyMinDevicePasswordComplexCharacters = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMinDevicePasswordComplexCharacters;
  public static String A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts    = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxDevicePasswordFailedAttempts;
  public static String A_zimbraMobilePolicyAllowSimpleDevicePassword          = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAllowSimpleDevicePassword;
  public static String A_zimbraMobilePolicyAlphanumericDevicePasswordRequired = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyAlphanumericDevicePasswordRequired;
  public static String A_zimbraMobilePolicyDevicePasswordExpiration           = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyDevicePasswordExpiration;
  public static String A_zimbraMobilePolicyDevicePasswordHistory              = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyDevicePasswordHistory;
  public static String A_zimbraMobilePolicyMaxInactivityTimeDeviceLock        = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyMaxInactivityTimeDeviceLock;
  public static String A_zimbraMobilePolicyUnapprovedInROMApplicationList     = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyUnapprovedInROMApplication;
  public static String A_zimbraMobilePolicyApprovedApplicationList            = com.zimbra.cs.account.Provisioning.A_zimbraMobilePolicyApprovedApplicationList;
  public static String A_zimbraPrefMailDefaultCharset                         = com.zimbra.cs.account.Provisioning.A_zimbraPrefMailDefaultCharset;
  public static String A_zimbraHsmPolicy                                      = com.zimbra.cs.account.Provisioning.A_zimbraHsmPolicy;
  public static String A_zimbraDefaultDomainName                              = com.zimbra.cs.account.Provisioning.A_zimbraDefaultDomainName;
  public static String A_zimbraPublicServiceHostname                          = com.zimbra.cs.account.Provisioning.A_zimbraPublicServiceHostname;
  public static String A_zimbraMailForwardingAddress                          = com.zimbra.cs.account.Provisioning.A_zimbraMailForwardingAddress;
  public static String A_zimbraGalLastSuccessfulSyncTimestamp                 = com.zimbra.cs.account.Provisioning.A_zimbraGalLastSuccessfulSyncTimestamp;
  public static String A_zimbraFeatureGalAutoCompleteEnabled                  = com.zimbra.cs.account.Provisioning.A_zimbraFeatureGalAutoCompleteEnabled;
  public static String A_zimbraFeatureGalEnabled                              = com.zimbra.cs.account.Provisioning.A_zimbraFeatureGalEnabled;
  public static String A_zimbraPrefFromAddress                                = com.zimbra.cs.account.Provisioning.A_zimbraPrefFromAddress;
  public static String A_zimbraPrefTimeZoneId                                 = com.zimbra.cs.account.Provisioning.A_zimbraPrefTimeZoneId;
  public static String A_zimbraPrefFromDisplay                                = com.zimbra.cs.account.Provisioning.A_zimbraPrefFromDisplay;
  public static String A_zimbraContactMaxNumEntries                           = com.zimbra.cs.account.Provisioning.A_zimbraContactMaxNumEntries;
  public static String A_zimbraMailSignatureMaxLength                         = com.zimbra.cs.account.Provisioning.A_zimbraMailSignatureMaxLength;
  public static String A_zimbraMailForwardingAddressMaxLength                 = com.zimbra.cs.account.Provisioning.A_zimbraMailForwardingAddressMaxLength;
  public static String A_zimbraMailForwardingAddressMaxNumAddrs               = com.zimbra.cs.account.Provisioning.A_zimbraMailForwardingAddressMaxNumAddrs;
  public static String A_zimbraRedoLogDeleteOnRollover                        = com.zimbra.cs.account.Provisioning.A_zimbraRedoLogDeleteOnRollover;

  /* $if ZimbraVersion >= 8.8.0 $ */
  public static String A_zimbraNetworkModulesNGEnabled                        = com.zimbra.cs.account.Provisioning.A_zimbraNetworkModulesNGEnabled;
  public static String A_zimbraNetworkMobileNGEnabled                         = com.zimbra.cs.account.Provisioning.A_zimbraNetworkMobileNGEnabled;
  public static String A_zimbraNetworkAdminEnabled                            = "zimbraNetworkAdminEnabled";//com.zimbra.cs.account.Provisioning.A_zimbraNetworkAdminEnabled;
  public static String A_zimbraNetworkAdminNGEnabled                          = "zimbraNetworkAdminNGEnabled";//com.zimbra.cs.account.Provisioning.A_zimbraNetworkAdminNGEnabled;
  /* $else$
  public static String A_zimbraNetworkModulesNGEnabled                        = "";
  public static String A_zimbraNetworkMobileNGEnabled                         = "";
  public static String A_zimbraNetworkAdminEnabled                            = "";
  public static String A_zimbraNetworkAdminNGEnabled                          = "";
  /* $endif$ */
  public static int    DATASOURCE_PASSWORD_MAX_LENGTH                         = 128;
  /* $if ZimbraVersion >= 8.6.0 $ */
  public static String A_zimbraMailboxdSSLProtocols                           = com.zimbra.cs.account.Provisioning.A_zimbraMailboxdSSLProtocols;
  /* $else$
  public static String A_zimbraMailboxdSSLProtocols                           = "";
  /* $endif$ */
  public static String A_zimbraSSLExcludeCipherSuites                         = com.zimbra.cs.account.Provisioning.A_zimbraSSLExcludeCipherSuites;
  /* $if ZimbraVersion >= 8.5.0 $ */
  public static String A_zimbraSSLIncludeCipherSuites                         = com.zimbra.cs.account.Provisioning.A_zimbraSSLIncludeCipherSuites;
  /* $else$
  public static String A_zimbraSSLIncludeCipherSuites                         = "";
  /* $endif$ */
  public static String A_zimbraGalType                                        = com.zimbra.cs.account.Provisioning.A_zimbraGalType;
  public static String A_zimbraDataSourceEnabled                              = com.zimbra.cs.account.Provisioning.A_zimbraDataSourceEnabled;
  public static String A_zimbraGalStatus                                      = com.zimbra.cs.account.Provisioning.A_zimbraGalStatus;

  public static String A_zimbraPrefLocale                                     = com.zimbra.cs.account.Provisioning.A_zimbraPrefLocale;
  public static String A_zimbraLocale                                         = com.zimbra.cs.account.Provisioning.A_zimbraLocale;

  @Nonnull
  public final com.zimbra.cs.account.Provisioning mProvisioning;

  @Nonnull
  private final NamedEntryWrapper<Account> mNamedEntryAccountWrapper;
  @Nonnull
  private final NamedEntryWrapper<Domain>  mNamedEntryDomainWrapper;
  private final static String[] mAccountAttrs = {
    com.zimbra.cs.account.Provisioning.A_c,
    com.zimbra.cs.account.Provisioning.A_cn,
    com.zimbra.cs.account.Provisioning.A_co,
  };

  private final AuthProvider mAuthProvider = new AuthProvider();

  public ProvisioningImp()
  {
    this(com.zimbra.cs.account.Provisioning.getInstance());
  }

  public ProvisioningImp(Object provisioning)
  {
    mProvisioning = (com.zimbra.cs.account.Provisioning) provisioning;

    mNamedEntryAccountWrapper = new NamedEntryWrapper<Account>()
    {
      @Nonnull
      @Override
      public Account wrap(NamedEntry entry)
      {
        return new Account((com.zimbra.cs.account.Account) entry);
      }
    };

    mNamedEntryDomainWrapper = new NamedEntryWrapper<Domain>()
    {
      @Nonnull
      @Override
      public Domain wrap(NamedEntry entry)
      {
        return new Domain((com.zimbra.cs.account.Domain) entry);
      }
    };
  }

  @Override
  public boolean isValidUid(@Nonnull String uid)
  {
    return uid.length() == 36 &&
      (uid.charAt(8) == '-' &&
        uid.charAt(13) == '-' &&
        uid.charAt(18) == '-' &&
        uid.charAt(23) == '-');
  }

  @Override
  @Nonnull
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
      com.zimbra.cs.account.DistributionList distributionList = mProvisioning.get(ProvisioningKey.ByDistributionList.id.toZimbra(), id);
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
      com.zimbra.cs.account.DistributionList distributionList = mProvisioning.get(ProvisioningKey.ByDistributionList.name.toZimbra(), name);
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
      try
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch(InvalidRequestException e1)
      {
        return null;
      }
    }
  }

  @Override
  public void visitAllAccounts(@Nonnull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
      com.zimbra.cs.account.SearchDirectoryOptions searchOptions = new com.zimbra.cs.account.SearchDirectoryOptions();
      ZLdapFilterFactory zldapFilterFactory = ZLdapFilterFactory.getInstance();
      searchOptions.setTypes(com.zimbra.cs.account.SearchDirectoryOptions.ObjectType.accounts);
      searchOptions.setFilter(zldapFilterFactory.allAccountsOnly());
      searchOptions.setMakeObjectOpt(
        com.zimbra.cs.account.SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS
      );
      mProvisioning.searchDirectory(searchOptions, namedEntryVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllLocalAccountsNoDefaults(@Nonnull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getLocalServer();

      com.zimbra.cs.account.SearchAccountsOptions searchOptions = new com.zimbra.cs.account.SearchAccountsOptions();
      searchOptions.setIncludeType(com.zimbra.cs.account.SearchAccountsOptions.IncludeType.ACCOUNTS_AND_CALENDAR_RESOURCES);
      searchOptions.setMakeObjectOpt(com.zimbra.cs.account.SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);

      mProvisioning.searchAccountsOnServer(server, searchOptions, namedEntryVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllLocalAccounts(@Nonnull SimpleVisitor<Account> visitor)
    throws ZimbraException
  {
    NamedEntry.Visitor namedEntryVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.getLocalServer();

      com.zimbra.cs.account.SearchAccountsOptions searchOptions = new com.zimbra.cs.account.SearchAccountsOptions();
      searchOptions.setIncludeType(com.zimbra.cs.account.SearchAccountsOptions.IncludeType.ACCOUNTS_AND_CALENDAR_RESOURCES);

      mProvisioning.searchAccountsOnServer(server, searchOptions, namedEntryVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllAccounts(@Nonnull SimpleVisitor<Account> visitor, @Nonnull Filter<Account> filterAccounts)
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
    @Nonnull SimpleVisitor<Account> visitor,
    @Nonnull Filter<Account> filterAccounts
  )
    throws ZimbraException
  {
    final List<Account> allAccounts = new ArrayList<Account>();
    SimpleVisitor<Account> accountListBuilder = new SimpleVisitor<Account>()
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

    for (Account account : allAccounts)
    {
      visitor.visit(account);
    }
  }

  @Override
  public void visitAccountByIdNoDefaults(SimpleVisitor<Account> visitor, ZimbraId accountId)
  {
    try
    {
      ZimbraVisitorWrapper<Account> zimbraVisitor = new ZimbraVisitorWrapper<Account>(visitor, mNamedEntryAccountWrapper);

      SearchDirectoryOptions searchOptions = new SearchDirectoryOptions();
      searchOptions.setMakeObjectOpt(SearchDirectoryOptions.MakeObjectOpt.NO_DEFAULTS);
      searchOptions.setTypes(
        SearchDirectoryOptions.ObjectType.accounts,
        SearchDirectoryOptions.ObjectType.resources
      );
      searchOptions.setFilter(ZLdapFilterFactory.getInstance().accountById(accountId.getId()));

      mProvisioning.searchDirectory(searchOptions, zimbraVisitor);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void visitAllDomains(@Nonnull SimpleVisitor<Domain> visitor) throws ZimbraException
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
  public void visitDomain(@Nonnull SimpleVisitor<Account> visitor, @Nonnull Domain domain) throws ZimbraException
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
  }

  @Override
  public void authAccount(@Nonnull Account account, String password, @Nonnull Protocol protocol, Map<String, Object> context)
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
  @Nonnull
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

  @Nonnull
  @Override
  public Domain assertDomainById(String domainId)
  {
    Domain domain = getDomainById(domainId);
    if( domain == null )
    {
      throw new NoSuchDomainException(domainId);
    }
    else
    {
      return domain;
    }
  }

  @Nonnull
  @Override
  public Domain assertDomainByName(String domainName)
  {
    Domain domain = getDomainByName(domainName);
    if( domain == null )
    {
      throw new NoSuchDomainException(domainName);
    }
    else
    {
      return domain;
    }
  }

  @Override
  public Zimlet assertZimlet(String zimletName)
  {
    Zimlet zimlet = getZimlet(zimletName);
    if( zimlet == null )
    {
      throw new NoSuchZimletException(zimletName);
    }
    else
    {
      return zimlet;
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
  @Nonnull
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
  public void modifyAttrs(@Nonnull Entry entry, Map<String, Object> attrs)
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
  public List<DistributionList> getAllDistributionLists(@Nonnull Domain domain)
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
  public List<Group> getAllGroups(Domain domain)
    throws ZimbraException
  {
    try
    {
      return ZimbraListWrapper.wrapGroups(
        mProvisioning.getAllGroups(domain.toZimbra(com.zimbra.cs.account.Domain.class))
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
  public DistributionList get(@Nonnull ProvisioningKey.ByDistributionList id, String dlStr)
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
  public Account get(@Nonnull ProvisioningKey.ByAccount by, String target)
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
  @Nonnull
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
  @Nonnull
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
  public List<Account> getAllAccounts(@Nonnull Domain domain)
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
  public List<CalendarResource> getAllCalendarResources(@Nonnull Domain domain)
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
  @Nonnull
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
    try
    {
      return ZimbraListWrapper.wrapUCServices(mProvisioning.getAllUCServices());
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
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
  public Domain createDomain(String currentDomainName, Map<String, Object> stringObjectMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Domain domain = mProvisioning.createDomain(currentDomainName,
        stringObjectMap);
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
  public Cos createCos(String cosname, Map<String, Object> stringObjectMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Cos cos = mProvisioning.createCos(cosname,
        stringObjectMap);
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
  public DistributionList createDistributionList(String dlistName)
    throws ZimbraException
  {
    return createDistributionList(dlistName, new HashMap<String, Object>());
  }

  @Override
  @Nullable
  public DistributionList createDistributionList(String dlistName, Map<String, Object> stringObjectMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.DistributionList distributionList = mProvisioning.createDistributionList(
        dlistName,
        stringObjectMap
      );
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
  public Group createDynamicGroup(String groupName)
    throws ZimbraException
  {
    return createDynamicGroup(groupName, Collections.<String, Object>emptyMap());
  }

  @Override
  @Nullable
  public Group createDynamicGroup(String groupName, Map<String, Object> stringObjectMap)
    throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Group group = mProvisioning.createDynamicGroup(groupName,
        stringObjectMap);
      if (group == null)
      {
        return null;
      }
      else
      {
        return new Group(group);
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
  public Account createGalAccount(String dstAccount, String newPassword, Map<String, Object> attrs)
    throws ZimbraException
  {
    final HashMap<String, Object> galProp = new HashMap<>();
    galProp.put("zimbraContactMaxNumEntries", "0");
    galProp.put("zimbraHideInGal", "TRUE");
    galProp.put("zimbraIsSystemAccount", "TRUE");
    galProp.put("zimbraIsSystemResource", "TRUE");
    galProp.putAll(attrs);

    final Account account = createAccount(dstAccount, newPassword, galProp);
    final Domain domain = getDomainByName(account.getDomainName());
    if (domain == null)
    {
      throw new ZimbraException("No such domain " + account.getDomainName());
    }
    try
    {
      String acctName = account.getName();
      String acctId = account.getId();
      com.zimbra.cs.account.Domain zimbraDomain = domain.toZimbra(com.zimbra.cs.account.Domain.class);
      HashSet<String> galAcctIds = new HashSet<String>(Arrays.asList(zimbraDomain.getGalAccountId()));
      if (!galAcctIds.contains(acctId)) {
        galAcctIds.add(acctId);
        zimbraDomain.setGalAccountId(galAcctIds.toArray(new String[0]));
      }

      final String folder = "/_zimbra";
      final HashMap<String, Object> dataSourceProperties = new HashMap<>();
      dataSourceProperties.put("zimbraGalType", "zimbra");
      dataSourceProperties.put("zimbraDataSourceFolderId", folder);
      dataSourceProperties.put("zimbraDataSourceEnabled", "TRUE");
      dataSourceProperties.put("zimbraGalStatus", "enabled");

      final org.openzal.zal.Mailbox mailboxByAccount = new MailboxManagerImp().getMailboxByAccount(account);
      final Mailbox zimbraMBox = mailboxByAccount.toZimbra(Mailbox.class);

      Folder contactFolder;
      try {
         contactFolder = zimbraMBox.getFolderByPath(
          null,
          folder
        );
      }
      catch (MailServiceException.NoSuchItemException e) {
        contactFolder = zimbraMBox.createFolder(
          null,
          folder,
          new Folder.FolderOptions().setDefaultView(MailItem.Type.CONTACT)
        );
      }

      int folderId = contactFolder.getId();
      for (DataSource ds : account.getAllDataSources()) {
        if (ds.getFolderId() == folderId) {
          throw MailServiceException.ALREADY_EXISTS("data source " + ds.toZimbra().getName() + " already contains folder " + folder);
        }
      }

      zimbraMBox.grantAccess(
        null,
        folderId,
        domain.getId(),
        ACL.GRANTEE_DOMAIN,
        ACL.stringToRights("r"),
        null
      );

      // create datasource
      Map<String, Object> dataSourceAttrs = new HashMap<>();
      dataSourceAttrs.put("zimbraDataSourcePollingInterval", "1d");
      dataSourceAttrs.put(A_zimbraGalType, "zimbra");
      dataSourceAttrs.put(A_zimbraDataSourceFolderId, String.valueOf(folderId));
      if( !dataSourceAttrs.containsKey(A_zimbraDataSourceEnabled) )
      {
        dataSourceAttrs.put(A_zimbraDataSourceEnabled, LdapConstants.LDAP_TRUE);
      }
      if( !dataSourceAttrs.containsKey(A_zimbraGalStatus) )
      {
        dataSourceAttrs.put(A_zimbraGalStatus, "enabled");
      }
      mProvisioning.createDataSource(
        account.toZimbra(com.zimbra.cs.account.Account.class),
        com.zimbra.soap.admin.type.DataSourceType.gal,
        "zimbra",
        dataSourceAttrs
      );
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }

    return account;
  }

  @Override
  public Account createFakeAccount(Map<String, Object> attrs)
    throws ZimbraException
  {
    return new Account("dummy_account", "", attrs, new HashMap(), this);
  }

  @Override
  public void restoreAccount(String emailAddress, Map<String, Object> attrs)
  {
    ZLdapContext zlc = null;
    ZMutableEntry entry = null;
    String dn = null;
    try
    {
      zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.CREATE_ACCOUNT);
      entry = LdapClient.createMutableEntry();
      entry.mapToAttrs(attrs);
      //dn = "cn="+LdapUtil.escapeRDNValue(attributes.get("cn").toString())+",cn=cos,cn=zimbra";
      LdapProvisioning provisioning = (LdapProvisioning)mProvisioning;

      String[] parts = emailAddress.split("@");
      String localPart = parts[0];
      String domain = parts[1];

      dn = provisioning.getDIT().accountDNCreate(
        null,
        entry.getAttributes(),
        localPart,
        domain
      );

      entry.setDN(dn);
      ZimbraLog.mailbox.info("Restoring account "+dn);
      zlc.createEntry(entry);
    }
    catch (LdapException.LdapEntryAlreadyExistException ex)
    {
      try
      {
        if( zlc != null )
        {
          zlc.replaceAttributes(dn, entry.getAttributes());
        }
      }
      catch (LdapException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    finally
    {
      LdapClient.closeContext(zlc);
    }
  }

  @Override
  public DataSource restoreDataSource(Account account, DataSourceType dsType, String dsName, Map<String, Object> dataSourceAttrs)
  {
    try
    {
      return new DataSource(
        mProvisioning.restoreDataSource(
          account.toZimbra(com.zimbra.cs.account.Account.class),
          com.zimbra.soap.admin.type.DataSourceType.fromString(dsType.name()),
          dsName,
          dataSourceAttrs
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Identity restoreIdentity(Account account, String identityName, Map<String, Object> identityAttrs)
  {
    try
    {
      return new Identity(
        mProvisioning.restoreIdentity(
          account.toZimbra(com.zimbra.cs.account.Account.class),
          identityName,
          identityAttrs
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public Signature restoreSignature(Account account, String signatureName, Map<String, Object> signatureAttrs)
  {
    try
    {
      return new Signature(
        mProvisioning.restoreSignature(
          account.toZimbra(com.zimbra.cs.account.Account.class),
          signatureName,
          signatureAttrs
        )
      );
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void restoreCos(Map<String, Object> attributes)
  {
    ZLdapContext zlc = null;
    ZMutableEntry entry = null;
    String dn = null;
    try
    {
      zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.CREATE_COS);
      entry = LdapClient.createMutableEntry();
      entry.mapToAttrs(attributes);
      dn = "cn="+LdapUtil.escapeRDNValue(attributes.get("cn").toString())+",cn=cos,cn=zimbra";
      entry.setDN(dn);
      ZimbraLog.mailbox.info("Restoring cos "+dn);
      zlc.createEntry(entry);
    }
    catch (LdapException.LdapEntryAlreadyExistException ex)
    {
      try
      {
        if( zlc != null )
        {
          zlc.replaceAttributes(dn, entry.getAttributes());
        }
      }
      catch (LdapException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    finally
    {
      LdapClient.closeContext(zlc);
    }
  }

  private final static Method sCreateParentDomains;
  static
  {
    try
    {
      sCreateParentDomains = com.zimbra.cs.account.ldap.LdapProvisioning.class.getDeclaredMethod(
        "createParentDomains",
        ZLdapContext.class,
        String[].class,
        String[].class
      );
      sCreateParentDomains.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }


  private void createParentDomains(ZLdapContext zlc, String[] parts, String[] dns) throws ServiceException
  {
    try
    {
      sCreateParentDomains.invoke(mProvisioning,zlc,parts,dns);
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch (InvocationTargetException e)
    {
      try
      {
        throw e.getCause();
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (ServiceException ex)
      {
        throw ex;
      }
      catch (Throwable throwable)
      {
        throw new RuntimeException(throwable);
      }
    }
  }

  @Override
  public void restoreDomain(Map<String, Object> attributes)
  {
    ZLdapContext zlc = null;
    String dn = null;
    ZMutableEntry entry = null;
    try
    {
      String name = (String) attributes.get("zimbraDomainName");
      String[] parts = name.split(Pattern.quote("."));

      StringBuffer stringBuffer = new StringBuffer();
      for( String part : parts )
      {
        stringBuffer
          .append("dc=")
          .append(LdapUtil.escapeRDNValue(part))
          .append(",");
      }
      dn = stringBuffer.substring(0, stringBuffer.length()-1);

      zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.CREATE_DOMAIN);

      LdapProvisioning provisioning = (LdapProvisioning)mProvisioning;

      if( parts.length > 1 )
      {
        String[] dns = provisioning.getDIT().domainToDNs(parts);
        createParentDomains(zlc, parts, dns);
      }

      entry = LdapClient.createMutableEntry();
      entry.mapToAttrs(attributes);
      entry.setDN(dn);
      ZimbraLog.mailbox.info("Restoring domain "+dn);

      zlc.createEntry(entry);

      String acctBaseDn = provisioning.getDIT().domainDNToAccountBaseDN(dn);
      if (!acctBaseDn.equals(dn)) {
        zlc.createEntry(provisioning.getDIT().domainDNToAccountBaseDN(dn), "organizationalRole", new String[]{"ou", "people", "cn", "people"});
        zlc.createEntry(provisioning.getDIT().domainDNToDynamicGroupsBaseDN(dn), "organizationalRole", new String[]{"cn", "groups", "description", "dynamic groups base"});
      }
    }
    catch (LdapException.LdapEntryAlreadyExistException ex)
    {
      try
      {
        if( zlc != null && entry != null ) // Just tell me why? 
        {
          zlc.replaceAttributes(dn, entry.getAttributes());

          String acctBaseDn = ((LdapProvisioning)mProvisioning).getDIT().domainDNToAccountBaseDN(dn);
          if (!acctBaseDn.equals(dn)) {
            zlc.createEntry(((LdapProvisioning)mProvisioning).getDIT().domainDNToAccountBaseDN(dn), "organizationalRole", new String[]{"ou", "people", "cn", "people"});
            zlc.createEntry(((LdapProvisioning)mProvisioning).getDIT().domainDNToDynamicGroupsBaseDN(dn), "organizationalRole", new String[]{"cn", "groups", "description", "dynamic groups base"});
          }
        }
      }
      catch( ServiceException e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    finally
    {
      LdapClient.closeContext(zlc);
    }
  }

  @Override
  public void restoreDistributionList(String address, Map<String, Object> attributes)
  {
    ZLdapContext zlc = null;
    ZMutableEntry entry = null;
    String dn = null;
    try
    {
      String[] parts = address.split(Pattern.quote("@"));
      String local = parts[0];
      String domain = parts[1];

      StringBuffer dc = new StringBuffer();
      for( String token : domain.split(Pattern.quote(".")) ) {
        dc.append(",dc=").append(LdapUtil.escapeRDNValue(token));
      }

      dn = "uid="+LdapUtil.escapeRDNValue(local)+",ou=people"+dc;
      zlc = LdapClient.getContext(LdapServerType.MASTER, LdapUsage.CREATE_DISTRIBUTIONLIST);
      entry = LdapClient.createMutableEntry();
      entry.mapToAttrs(attributes);
      entry.setDN(dn);
      ZimbraLog.mailbox.info("Restoring distribution list "+dn);
      zlc.createEntry(entry);
    }
    catch (LdapException.LdapEntryAlreadyExistException ex)
    {
      try
      {
        if( zlc != null )
        {
          zlc.replaceAttributes(dn, entry.getAttributes());
        }
      }
      catch (LdapException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    finally
    {
      LdapClient.closeContext(zlc);
    }
  }

  @Override
  @Nullable
  public Server createServer(String name, Map<String, Object> attrs)
          throws ZimbraException
  {
    try
    {
      com.zimbra.cs.account.Server server = mProvisioning.createServer(name, attrs);
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

  @Override
  public void modifyIdentity(@Nonnull Account newAccount, String identityName, Map<String, Object> newAttrs)
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
    String targetType, @Nonnull Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right
  ) throws ZimbraException
  {
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
  }

  @Override
  public void revokeRight(
    String targetType, Targetby targetBy, String target,
    String granteeType, @Nonnull GrantedBy granteeBy, String grantee,
    String right
  ) throws NoSuchGrantException
  {
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
  }

  @Override
  public void revokeRight(String targetType,
                          Targetby targetBy,
                          String target,
                          String granteeType,
                          @Nonnull GrantedBy granteeBy,
                          String grantee,
                          String right,
                          RightModifier rightModifier) throws NoSuchGrantException
  {
    try
    {
      mProvisioning.revokeRight(
        targetType,
        targetBy!=null?targetBy.toZimbra(TargetBy.class):null,
        target!=null?target:null,
        granteeType,
        granteeBy.toZimbra(GranteeBy.class),
        grantee,
        right,
        rightModifier!=null?rightModifier.toZimbra():null
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  //only it works if the specified target is compatible with the target of the right
  //It does not work with combo rights
  //you can see the tests in provisioningTest(AT)
  @Override
  public boolean checkRight(
    String targetType,
    Targetby targetBy,
    String target,
    GrantedBy granteeBy,
    String granteeVal,
    String right
                            )
  {
    try
    {
      return mProvisioning.checkRight(
        targetType,
        targetBy.toZimbra(TargetBy.class),
        target,
        granteeBy.toZimbra(GranteeBy.class),
        granteeVal,
        right,
        null,
        null
      );
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Grants getGrants(
    String targetType,
    Targetby targetBy,
    String target,
    String granteeType,
    GrantedBy granteeBy,
    String grantee,
    boolean granteeIncludeGroupsGranteeBelongs
  )
  {
    try
    {
      TargetBy targetBy1 = targetBy==null?null:targetBy.toZimbra(TargetBy.class);
      GranteeBy granteeBy1 = granteeBy==null?null:granteeBy.toZimbra(GranteeBy.class);
      RightCommand.Grants grants = mProvisioning.getGrants(
        targetType,
        targetBy1,
        target,
        granteeType,
        granteeBy1,
        grantee,
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
  }

  @Override
  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mProvisioning);
  }

  @Override
  @Nullable
  public Domain getDomain(@Nonnull Account account)
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
  public void flushCache(@Nonnull CacheEntryType cacheEntryType, @Nullable Collection<CacheEntry> cacheEntries)
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
  public CountAccountResult countAccount(@Nonnull Domain domain)
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
  public long getAccountsOnCos(@Nonnull Domain domain, @Nonnull Cos cos)
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
  public long getMaxAccountsOnCos(@Nonnull Domain domain, @Nonnull Cos cos)
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
  public Server getServer(@Nonnull Account acct)
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
  public boolean onLocalServer(@Nonnull Account userAccount)
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
  public long getEffectiveQuota(@Nonnull Account account)
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
    SearchDirectoryOptions opts = new SearchDirectoryOptions();
    ZLdapFilterFactory zLdapFilterFactory = ZLdapFilterFactory.getInstance();
    try
    {
      ZLdapFilter filter = ZLdapFilterFactory.getInstance().fromFilterString(
        ZLdapFilterFactory.FilterId.ALL_ACCOUNTS_ONLY,
        zLdapFilterFactory.equalityFilter(A_zimbraIsDelegatedAdminAccount, "TRUE", true)
      );
      opts.setFilter(filter);
      opts.setTypes(SearchDirectoryOptions.ObjectType.accounts);
      entryList = mProvisioning.searchDirectory(opts);
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
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nullable
  public Group getGroupByName(String dlStr)
    throws NoSuchGroupException
  {
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
    catch (com.zimbra.common.service.ServiceException e)
    {
      try
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch(InvalidRequestException e1)
      {
        return null;
      }
    }
  }

  @Override
  public void removeGranteeId(
    String target,
    String grantee_id,
    String granteeType,
    String right
  ) throws ZimbraException
  {
    try
    {
      // target
      com.zimbra.cs.account.Entry targetEntry = null;
      try
      {
        targetEntry = com.zimbra.cs.account.accesscontrol.TargetType.lookupTarget(
          mProvisioning,
          TargetType.account,
          com.zimbra.soap.type.TargetBy.name,
          target
        );
      }
      catch (Exception ignore) {}

      if( targetEntry == null )
      {
        targetEntry = com.zimbra.cs.account.accesscontrol.TargetType.lookupTarget(
          mProvisioning,
          TargetType.dl,
          com.zimbra.soap.type.TargetBy.name,
          target
        );
      }

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
  }

  @Override
  @Nullable
  public Grants getGrants(
    @Nonnull org.openzal.zal.provisioning.TargetType targetType,
    Targetby name,
    String targetName,
    boolean granteeIncludeGroupsGranteeBelongs
  )
  {
    try
    {
      RightCommand.Grants grants = mProvisioning.getGrants(
        targetType.getCode(),
        name!=null?name.toZimbra(TargetBy.class):null,
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
  }

  @Override
  public String getGranteeName(
    String grantee_id,
    @Nonnull String grantee_type
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
  @Nonnull
  public GalSearchResult galSearch(@Nonnull Account account, String query, int skip, int limit)
  {
    GalSearchParams searchParams = new GalSearchParams(account.toZimbra(com.zimbra.cs.account.Account.class));

    searchParams.createSearchParams(query);
    searchParams.setQuery(query);
    searchParams.setLimit(limit);
    searchParams.setIdOnly(false);

    searchParams.setType(GalSearchType.all);

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

  @Nonnull
  public GalSearchResult galSearch(@Nonnull Account account, Domain domain, String query, int skip, int limit)
  {
    AuthToken authToken = mAuthProvider.createAuthTokenForAccount(account).toZimbra(AuthToken.class);
    try
    {
      ZimbraSoapContext zimbraSoapContext = new ZimbraSoapContext(authToken, account.getId(), SoapProtocol.Soap12, SoapProtocol.Soap12);
      GalSearchParams searchParams = new GalSearchParams(domain.toZimbra(com.zimbra.cs.account.Domain.class), zimbraSoapContext);

      searchParams.createSearchParams(query);
      searchParams.setQuery(query);
      searchParams.setLimit(limit);
      searchParams.setIdOnly(false);

      searchParams.setType(GalSearchType.all);

      GalSearchResult result = new GalSearchResult();
      searchParams.setResponseName(new QName("result"));
      GalSearchCallback callback = new GalSearchCallback(skip, searchParams, result);
      searchParams.setResultCallback(callback);

      //writes mResultList ans hasMore
      GalSearchControl searchControl = new GalSearchControl(searchParams);

      searchControl.autocomplete();
      if (result.hasMore())
      {
        result.setTotal(result.getTotal() + 1);
      }

      return result;
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
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

  @Override
  public void deleteDomainById(String id)
  {
    try
    {
      mProvisioning.deleteDomain(id);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void deleteCosById(String id)
  {
    try
    {
      mProvisioning.deleteCos(id);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Collection<String> getWithDomainAliasesExpansion(String address)
  {
    return getWithDomainAliasesExpansion(address, new HashMap<String, Collection<Domain>>());
  }

  public Collection<String> getWithDomainAliasesExpansion(String address, Map<String,Collection<Domain>> domainCache)
  {
    Set<String> addresses = new HashSet<String>();
    if (address.contains("@"))
    {
      String[] parts = address.split("@");
      if (parts.length == 2)
      {
        String aliasName = parts[0];
        String domainName = parts[1];

        Collection<Domain> domainAliases = domainCache.get(domainName);
        if( domainAliases == null )
        {
          Domain domain = getDomainByName(domainName);
          if (domain != null)
          {
            domainAliases = getDomainAliases(domain);
            domainCache.put(domainName, domainAliases);
          }
          else
          {
            domainAliases = Collections.emptyList();
          }
        }

        for (Domain domainAlias : domainAliases)
        {
          String alias = aliasName + '@' + domainAlias.getName();
          addresses.add(alias);
        }
      }
    }
    return addresses;
  }

  public static class GalSearchCallback extends GalSearchResultCallback
  {
    private final int             mSkip;
    private final GalSearchResult mSearchResult;
    private int mCounter = 0;


    public GalSearchCallback(int skip, GalSearchParams params, GalSearchResult result)
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
    public Element handleContact(@Nonnull Contact contact)
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

    public void handleElement(@Nonnull Element node)
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

  public void invalidateAllCache()
  {
    com.zimbra.cs.account.accesscontrol.PermissionCache.invalidateAllCache();
  }

  public void purgeMemcachedAccounts(List<String> accounts)
  {
    try
    {
      ProxyPurgeUtil.purgeAccounts((List)null,accounts,true,(String)null);
      List<com.zimbra.cs.account.Server> memcachedServers = mProvisioning.getAllServers("memcached");
      List<String> routes = new ArrayList<>(accounts.size());
      for (String account : accounts)
      {
        Account accountObj = getAccountByName(account);
        if (accountObj != null)
        {
          routes.add("route:proto=http;zx=1;id=" + accountObj.getId());
          routes.add("route:proto=https;zx=1;id=" + accountObj.getId());
        }
      }
      ZimbraMemcachedClient zmc = new ZimbraMemcachedClient();
      try
      {
        String[] addresses = new String[memcachedServers.size()];
        int i = 0;
        for( com.zimbra.cs.account.Server memcachedServer : memcachedServers )
        {
          addresses[i++] = memcachedServer.getAttr(ProvisioningImp.A_zimbraServiceHostname, "localhost") +
            ":" + memcachedServer.getAttr("zimbraMemcachedBindPort", "11211");
        }
        zmc.connect(addresses, false, (String) null, 0, 5000L);
        for( String route : routes )
        {
          zmc.remove(route, false);
        }
      }
      finally
      {
        zmc.disconnect(-1);
      }
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public long getLastLogonTimestampFrequency()
  {
    try
    {
      return mProvisioning.getConfig().getLastLogonTimestampFrequency();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  @Nonnull
  public Group assertGroupById(String groupId)
    throws NoSuchGroupException
  {
    Group group = getGroupById(groupId);
    if (group == null)
    {
      throw new NoSuchGroupException(groupId);
    }

    return group;
  }

  @Override
  @Nonnull
  public Group assertGroupByName(String groupName)
    throws NoSuchGroupException
  {
    Group group = getGroupByName(groupName);
    if (group == null)
    {
      throw new NoSuchGroupException(groupName);
    }

    return group;
  }

  @Override
  public void rawQuery(String base, final String query, LdapVisitor visitor)
  {
    rawQuery(base, query, visitor, null);
  }

  @Override
  public void rawQuery(String base, final String query, LdapVisitor visitor, String[] fields)
  {
    UBIDLdapContext zlc = null;
    try {
      zlc = ((UBIDLdapContext) LdapClient.getContext(LdapServerType.REPLICA, LdapUsage.GENERIC));
      LDAPConnection connection = zlc.getNative();


      {
        SearchRequest searchRequest = new SearchRequest(
          base,
          SearchScope.SUBORDINATE_SUBTREE,
          query
        );
        searchRequest.setAttributes(fields);

        HashMap<String,String> map = new HashMap<>(
          fields == null ? 100 : fields.length, 1.0f
        );

        ASN1OctetString resumeCookie = null;
        while (true)
        {
          searchRequest.setControls(
            new SimplePagedResultsControl(
              1000 * 25,
              resumeCookie
            )
          );
          SearchResult searchResult = connection.search(searchRequest);

          for (SearchResultEntry current : searchResult.getSearchEntries())
          {
            String dn = current.getDN();
            String address = Utils.dnToName(dn);

            map.clear();
            for( Attribute attribute : current.getAttributes() ) {
              map.put(attribute.getName(), attribute.getValue());
            }
            visitor.visit(
              dn,
              address,
              map
            );
          }

          SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(
            searchResult
          );
          if (responseControl.moreResultsToReturn())
          {
            resumeCookie = responseControl.getCookie();
          }
          else
          {
            break;
          }
        }
      }
    } catch (ServiceException ex) {
      throw ExceptionWrapper.wrap(ex);
    }
    catch (LDAPException ex) {
      throw ExceptionWrapper.wrap(ex);
    }
    finally {
      LdapClient.closeContext(zlc);
    }
  }

  @Override
  public int rawCountQuery(String base, final String query)
  {
    ZLdapContext zlc = null;
    try
    {
      ZSearchControls searchControls = ZSearchControls.createSearchControls(
        ZSearchScope.SEARCH_SCOPE_SUBTREE,
        0,
        (String[]) null
      );

      zlc = LdapClient.getContext(LdapServerType.REPLICA, LdapUsage.GENERIC);
      return (int)zlc.countEntries(
        base,
        DirectQueryFilterBuilder.create(query),
        searchControls
      );
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
    catch (LDAPException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
    finally
    {
      LdapClient.closeContext(zlc);
    }
  }

  @Override
  public void registerChangePasswordListener(ChangePasswordListener listener)
  {
    com.zimbra.cs.account.ldap.ChangePasswordListener.registerInternal(com.zimbra.cs.account.ldap.ChangePasswordListener.InternalChangePasswordListenerId.CPL_SYNC, new ChangePasswordListenerWrapper(listener));
  }

  @Override
  public void registerTwoFactorChangeListener(String name, TwoFactorAuthChangeListener listener)
  {
    TwoFactorAuthChangeListenerWrapper.wrap(listener).register(name);
  }

}
