/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2015 ZeXtras S.r.l.
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

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
/* $if ZimbraVersion >= 8.0.0 $ */
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.account.Key.CacheEntryBy;
import com.zimbra.common.account.Key.CalendarResourceBy;
import com.zimbra.common.account.Key.CosBy;
import com.zimbra.common.account.Key.DataSourceBy;
import com.zimbra.common.account.Key.DistributionListBy;
import com.zimbra.common.account.Key.DomainBy;
import com.zimbra.common.account.Key.IdentityBy;
import com.zimbra.common.account.Key.ServerBy;
import com.zimbra.common.account.Key.ShareLocatorBy;
import com.zimbra.common.account.Key.SignatureBy;
import com.zimbra.common.account.Key.UCServiceBy;
import com.zimbra.common.account.Key.XMPPComponentBy;
import com.zimbra.common.account.Key.ZimletBy;
/* $else$
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.CacheEntryBy;
import com.zimbra.cs.account.Provisioning.CalendarResourceBy;
import com.zimbra.cs.account.Provisioning.CosBy;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.DistributionListBy;
import com.zimbra.cs.account.Provisioning.DomainBy;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.Provisioning.SignatureBy;
import com.zimbra.cs.account.Provisioning.XMPPComponentBy;
import com.zimbra.cs.account.Provisioning.ZimletBy;
/* $endif $ */

public class ProvisioningKey
{
  public static class ByAccount
  {
    private final AccountBy mAccountBy;

    @NotNull public static ByAccount adminName        = new ByAccount(AccountBy.adminName);
    @NotNull public static ByAccount appAdminName     = new ByAccount(AccountBy.appAdminName);
    @NotNull public static ByAccount id               = new ByAccount(AccountBy.id);
    @NotNull public static ByAccount foreignPrincipal = new ByAccount(AccountBy.foreignPrincipal);
    @NotNull public static ByAccount name             = new ByAccount(AccountBy.name);
    @NotNull public static ByAccount krb5Principal    = new ByAccount(AccountBy.krb5Principal);

    ByAccount(AccountBy accountBy)
    {
      mAccountBy = accountBy;
    }

    @NotNull
    public static ByAccount fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByAccount(AccountBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }

    AccountBy toZimbra()
    {
      return mAccountBy;
    }
  }

  // data sources
  public static class ByDataSource
  {

    private final DataSourceBy mDataSourceBy;

    @NotNull public static ByDataSource id   = new ByDataSource(DataSourceBy.id);
    @NotNull public static ByDataSource name = new ByDataSource(DataSourceBy.name);

    ByDataSource(DataSourceBy dataSourceBy)
    {
      mDataSourceBy = dataSourceBy;
    }

    DataSourceBy toZimbra()
    {
      return mDataSourceBy;
    }

    @NotNull
    public static ByDataSource fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByDataSource(DataSourceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  // identities
  public static class ByIdentity
  {
    private final IdentityBy mIdentityBy;

    @NotNull public static ByIdentity id   = new ByIdentity(IdentityBy.id);
    @NotNull public static ByIdentity name = new ByIdentity(IdentityBy.name);

    ByIdentity(IdentityBy identityBy)
    {
      mIdentityBy = identityBy;
    }

    IdentityBy toZimbra()
    {
      return mIdentityBy;
    }

    @NotNull
    public static ByIdentity fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByIdentity(IdentityBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByDomain
  {
    private DomainBy mDomainBy;

    @NotNull public static ByDomain id              = new ByDomain(DomainBy.id);
    @NotNull public static ByDomain name            = new ByDomain(DomainBy.name);
    @NotNull public static ByDomain virtualHostname = new ByDomain(DomainBy.virtualHostname);
    @NotNull public static ByDomain krb5Realm       = new ByDomain(DomainBy.krb5Realm);
    /* $if ZimbraVersion >= 8.0.0 $ */
    @NotNull public static ByDomain foreignName     = new ByDomain(DomainBy.foreignName);
    /* $else$
    public static ByDomain foreignName     = new ByDomain(DomainBy.name);
    /* $endif $ */

    ByDomain(DomainBy domainBy)
    {
      mDomainBy = domainBy;
    }

    DomainBy toZimbra()
    {
      return mDomainBy;
    }

    @NotNull
    public static ByDomain fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByDomain(DomainBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByServer
  {
    private ServerBy mServerBy;

    @NotNull public static ByServer id              = new ByServer(ServerBy.id);
    @NotNull public static ByServer name            = new ByServer(ServerBy.name);
    @NotNull public static ByServer serviceHostname = new ByServer(ServerBy.serviceHostname);

    ByServer(ServerBy serverBy)
    {
      mServerBy = serverBy;
    }

    ServerBy toZimbra()
    {
      return mServerBy;
    }

    @NotNull
    public static ByServer fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByServer(ServerBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByZimlet
  {
    private final ZimletBy mZimletBy;

    @NotNull public static ByZimlet id   = new ByZimlet(ZimletBy.id);
    @NotNull public static ByZimlet name = new ByZimlet(ZimletBy.name);

    ByZimlet(ZimletBy identityBy)
    {
      mZimletBy = identityBy;
    }

    ZimletBy toZimbra()
    {
      return mZimletBy;
    }

    @NotNull
    public static ByZimlet fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByZimlet(ZimletBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  // signatures
  public static class BySignature
  {
    private final SignatureBy mSignatureBy;

    @NotNull public static BySignature id   = new BySignature(SignatureBy.id);
    @NotNull public static BySignature name = new BySignature(SignatureBy.name);

    BySignature(SignatureBy identityBy)
    {
      mSignatureBy = identityBy;
    }

    SignatureBy toZimbra()
    {
      return mSignatureBy;
    }

    @NotNull
    public static BySignature fromString(String s)
      throws ServiceException
    {
      try
      {
        return new BySignature(SignatureBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByCacheEntry
  {
    private final CacheEntryBy mCacheEntryBy;

    @NotNull public static ByCacheEntry id   = new ByCacheEntry(CacheEntryBy.id);
    @NotNull public static ByCacheEntry name = new ByCacheEntry(CacheEntryBy.name);

    ByCacheEntry(CacheEntryBy identityBy)
    {
      mCacheEntryBy = identityBy;
    }

    CacheEntryBy toZimbra()
    {
      return mCacheEntryBy;
    }

    @NotNull
    public static ByCacheEntry fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByCacheEntry(CacheEntryBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByCos
  {
    private final CosBy mCosBy;

    @NotNull public static ByCos id   = new ByCos(CosBy.id);
    @NotNull public static ByCos name = new ByCos(CosBy.name);

    ByCos(CosBy identityBy)
    {
      mCosBy = identityBy;
    }

    CosBy toZimbra()
    {
      return mCosBy;
    }

    @NotNull
    public static ByCos fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByCos(CosBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByCalendarResource
  {
    private final CalendarResourceBy mCalendarResourceBy;

    @NotNull public static ByCalendarResource id               = new ByCalendarResource(CalendarResourceBy.id);
    @NotNull
    public static          ByCalendarResource foreignPrincipal = new ByCalendarResource(CalendarResourceBy.foreignPrincipal);
    @NotNull public static ByCalendarResource name             = new ByCalendarResource(CalendarResourceBy.name);

    ByCalendarResource(CalendarResourceBy identityBy)
    {
      mCalendarResourceBy = identityBy;
    }

    CalendarResourceBy toZimbra()
    {
      return mCalendarResourceBy;
    }

    @NotNull
    public static ByCalendarResource fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByCalendarResource(CalendarResourceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByXMPPComponent
  {
    private final XMPPComponentBy mXMPPComponentBy;

    @NotNull public static ByXMPPComponent id              = new ByXMPPComponent(XMPPComponentBy.id);
    @NotNull public static ByXMPPComponent serviceHostname = new ByXMPPComponent(XMPPComponentBy.serviceHostname);
    @NotNull public static ByXMPPComponent name            = new ByXMPPComponent(XMPPComponentBy.name);

    ByXMPPComponent(XMPPComponentBy identityBy)
    {
      mXMPPComponentBy = identityBy;
    }

    XMPPComponentBy toZimbra()
    {
      return mXMPPComponentBy;
    }

    @NotNull
    public static ByXMPPComponent fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByXMPPComponent(XMPPComponentBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ByDistributionList
  {
    private final DistributionListBy mDistributionListBy;

    @NotNull public static ByDistributionList id   = new ByDistributionList(DistributionListBy.id);
    @NotNull public static ByDistributionList name = new ByDistributionList(DistributionListBy.name);

    ByDistributionList(DistributionListBy identityBy)
    {
      mDistributionListBy = identityBy;
    }

    DistributionListBy toZimbra()
    {
      return mDistributionListBy;
    }

    @NotNull
    public static ByDistributionList fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByDistributionList(DistributionListBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  static class ByUCService
  {
    private final UCServiceBy mUCServiceBy;

    @NotNull public static ByUCService id   = new ByUCService(UCServiceBy.id);
    @NotNull public static ByUCService name = new ByUCService(UCServiceBy.name);

    ByUCService(UCServiceBy identityBy)
    {
      mUCServiceBy = identityBy;
    }

    UCServiceBy toZimbra()
    {
      return mUCServiceBy;
    }

    @NotNull
    public static ByUCService fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByUCService(UCServiceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  static class ByShareLocator
  {
    private final ShareLocatorBy mShareLocatorBy;

    @NotNull public static ByShareLocator id = new ByShareLocator(ShareLocatorBy.id);

    ByShareLocator(ShareLocatorBy identityBy)
    {
      mShareLocatorBy = identityBy;
    }

    ShareLocatorBy toZimbra()
    {
      return mShareLocatorBy;
    }

    @NotNull
    public static ByShareLocator fromString(String s)
      throws ServiceException
    {
      try
      {
        return new ByShareLocator(ShareLocatorBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }
  /* $endif $ */
}
