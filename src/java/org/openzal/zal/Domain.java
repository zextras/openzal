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

import com.google.common.annotations.VisibleForTesting;
import com.zimbra.common.account.ZAttrProvisioning;
import java.util.Set;
import javax.annotation.Nullable;

import java.util.Objects;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Domain extends Entry
{
  public static final String HTTPS = "https";
  public static final String HTTP = "http";
  @Nonnull private final com.zimbra.cs.account.Domain mDomain;

  public Domain(@Nonnull Object domain)
  {
    super(domain);
    mDomain = (com.zimbra.cs.account.Domain) domain;
  }

  public Domain(
    String name,
    String id,
    Map<String, Object> attrs,
    Map<String, Object> defaults,
    @Nonnull Provisioning prov
  )
  {
    this(
      new com.zimbra.cs.account.Domain(
        name,
        id,
        attrs,
        defaults,
        prov.toZimbra(com.zimbra.cs.account.Provisioning.class)
      )
    );
  }

  public void unsetPasswordChangeListener()
  {
    try
    {
      mDomain.unsetPasswordChangeListener();
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getDomainDefaultCOSId()
  {
    return mDomain.getDomainDefaultCOSId();
  }

  public String getId()
  {
    return mDomain.getId();
  }

  public String getName()
  {
    return mDomain.getName();
  }

  @Nonnull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mDomain.getAttrs(applyDefaults));
  }

  public long getMailDomainQuota()
  {
    return mDomain.getMailDomainQuota();
  }

  public void setDomainCOSMaxAccounts(@Nonnull Collection<String> zimbraDomainCOSMaxAccounts)
  {
    try
    {
      mDomain.setDomainCOSMaxAccounts(zimbraDomainCOSMaxAccounts.toArray(new String[zimbraDomainCOSMaxAccounts.size()]));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public String getDomainAliasTargetId()
  {
    return mDomain.getDomainAliasTargetId();
  }

  public boolean isAliasDomain()
  {
    return mDomain.getDomainAliasTargetId() != null;
  }

  public int getDomainMaxAccounts()
  {
    return mDomain.getDomainMaxAccounts();
  }

  public void setDomainTypeAsString(String zimbraDomainType)
  {
    try
    {
      mDomain.setDomainTypeAsString(zimbraDomainType);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Collection<String> getDomainCOSMaxAccounts()
  {
    return Arrays.asList(mDomain.getDomainCOSMaxAccounts());
  }

  public String getPasswordChangeListener()
  {
    return mDomain.getPasswordChangeListener();
  }

  @Nonnull
  @Deprecated //instant-kill big infrastructures
  public List<Account> getAllAccounts()
  {
    try
    {
      return ZimbraListWrapper.wrapAccounts(mDomain.getAllAccounts());
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @VisibleForTesting
  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mDomain);
  }

  public long getLongAttr(String name, int defaultValue)
  {
    return mDomain.getLongAttr(name, defaultValue);
  }

  @Nullable
  public String getPublicHostname()
  {
    return mDomain.getPublicServiceHostname();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    Domain domain = (Domain) o;

    return mDomain.getId().equals(domain.mDomain.getId());

  }

  @Override
  public int hashCode()
  {
    return mDomain.getId().hashCode();
  }

  @Nullable
  public String getPublicProtocol() {
    return Optional.ofNullable(mDomain.getPublicServiceProtocol())
        .orElse(HTTPS);
  }

  private String defaultPortForProtocol(String protocol)
  {
    if( Objects.isNull(protocol))
    {
      return null;
    }
    switch( protocol.toLowerCase() )
    {
      case HTTPS:
        return "443";
      case HTTP:
      default:
        return "80";
    }
  }

  @Nullable
  public String getPublicPort()
  {
    int publicServicePort = mDomain.getPublicServicePort();
    if (publicServicePort > 0) {
      return String.valueOf(publicServicePort);
    }
    else {
      return String.valueOf(defaultPortForProtocol(getPublicProtocol()));
    }
  }

  public List<String> getGalAccountIds()
  {
    return Arrays.asList(mDomain.getGalAccountId());
  }

  @Nullable
  public String getSkinLogoAppBanner()
  {
    return mDomain.getSkinLogoAppBanner();
  }

  @Nullable
  public String getSkinLogoURL()
  {
    return mDomain.getSkinLogoURL();
  }

  public String getAuthMech() {
    return mDomain.getAuthMech();
  }

  public String getAuthMechAdmin() {
    return mDomain.getAuthMechAdmin();
  }

  @Nullable
  public String getWebClientLoginURL() {
    return mDomain.getWebClientLoginURL();
  }

  public boolean isExternalLdapAuthAvailable() {
    Set<String> url = mDomain.getMultiAttrSet(com.zimbra.cs.account.Provisioning.A_zimbraAuthLdapURL);
    return url != null && url.size() > 0;
  }

  @Nullable
  public DomainStatus getStatus() {
    ZAttrProvisioning.DomainStatus domainStatus = mDomain.getDomainStatus();
    if (domainStatus == null) {
      return null;
    }
    return new DomainStatus(domainStatus);
  }

  public boolean isAuthFallbackToLocal() {
    return mDomain.isAuthFallbackToLocal();
  }

  public <A> A readAttribute(LDAPAttributeReader<Domain,A> attr) {
    return attr.read(this);
  }

  public static <A> LDAPAttributeReader<Domain,A> createAttribute(LDAPAttributeReader<Entry, A> attr) {
    return attr.compose(domain ->  domain);
  }

  public String getNotificationFrom() {
    return mDomain.getCarbonioNotificationFrom();
  }

  public String[] getNotificationRecipients() {
    return mDomain.getCarbonioNotificationRecipients();
  }
}

