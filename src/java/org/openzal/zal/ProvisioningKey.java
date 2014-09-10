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

    public static ByAccount adminName        = new ByAccount(AccountBy.adminName);
    public static ByAccount appAdminName     = new ByAccount(AccountBy.appAdminName);
    public static ByAccount id               = new ByAccount(AccountBy.id);
    public static ByAccount foreignPrincipal = new ByAccount(AccountBy.foreignPrincipal);
    public static ByAccount name             = new ByAccount(AccountBy.name);
    public static ByAccount krb5Principal    = new ByAccount(AccountBy.krb5Principal);

    ByAccount(AccountBy accountBy)
    {
      mAccountBy = accountBy;
    }

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

    public static ByDataSource id   = new ByDataSource(DataSourceBy.id);
    public static ByDataSource name = new ByDataSource(DataSourceBy.name);

    ByDataSource(DataSourceBy dataSourceBy)
    {
      mDataSourceBy = dataSourceBy;
    }

    DataSourceBy toZimbra()
    {
      return mDataSourceBy;
    }

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

    public static ByIdentity id   = new ByIdentity(IdentityBy.id);
    public static ByIdentity name = new ByIdentity(IdentityBy.name);

    ByIdentity(IdentityBy identityBy)
    {
      mIdentityBy = identityBy;
    }

    IdentityBy toZimbra()
    {
      return mIdentityBy;
    }

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

    public static ByDomain id              = new ByDomain(DomainBy.id);
    public static ByDomain name            = new ByDomain(DomainBy.name);
    public static ByDomain virtualHostname = new ByDomain(DomainBy.virtualHostname);
    public static ByDomain krb5Realm       = new ByDomain(DomainBy.krb5Realm);
    /* $if ZimbraVersion >= 8.0.0 $ */
    public static ByDomain foreignName     = new ByDomain(DomainBy.foreignName);
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

    public static ByServer id              = new ByServer(ServerBy.id);
    public static ByServer name            = new ByServer(ServerBy.name);
    public static ByServer serviceHostname = new ByServer(ServerBy.serviceHostname);

    ByServer(ServerBy serverBy)
    {
      mServerBy = serverBy;
    }

    ServerBy toZimbra()
    {
      return mServerBy;
    }

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

    public static ByZimlet id   = new ByZimlet(ZimletBy.id);
    public static ByZimlet name = new ByZimlet(ZimletBy.name);

    ByZimlet(ZimletBy identityBy)
    {
      mZimletBy = identityBy;
    }

    ZimletBy toZimbra()
    {
      return mZimletBy;
    }

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

    public static BySignature id   = new BySignature(SignatureBy.id);
    public static BySignature name = new BySignature(SignatureBy.name);

    BySignature(SignatureBy identityBy)
    {
      mSignatureBy = identityBy;
    }

    SignatureBy toZimbra()
    {
      return mSignatureBy;
    }

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

    public static ByCacheEntry id   = new ByCacheEntry(CacheEntryBy.id);
    public static ByCacheEntry name = new ByCacheEntry(CacheEntryBy.name);

    ByCacheEntry(CacheEntryBy identityBy)
    {
      mCacheEntryBy = identityBy;
    }

    CacheEntryBy toZimbra()
    {
      return mCacheEntryBy;
    }

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

    public static ByCos id   = new ByCos(CosBy.id);
    public static ByCos name = new ByCos(CosBy.name);

    ByCos(CosBy identityBy)
    {
      mCosBy = identityBy;
    }

    CosBy toZimbra()
    {
      return mCosBy;
    }

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

    public static ByCalendarResource id               = new ByCalendarResource(CalendarResourceBy.id);
    public static ByCalendarResource foreignPrincipal = new ByCalendarResource(CalendarResourceBy.foreignPrincipal);
    public static ByCalendarResource name             = new ByCalendarResource(CalendarResourceBy.name);

    ByCalendarResource(CalendarResourceBy identityBy)
    {
      mCalendarResourceBy = identityBy;
    }

    CalendarResourceBy toZimbra()
    {
      return mCalendarResourceBy;
    }

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

    public static ByXMPPComponent id              = new ByXMPPComponent(XMPPComponentBy.id);
    public static ByXMPPComponent serviceHostname = new ByXMPPComponent(XMPPComponentBy.serviceHostname);
    public static ByXMPPComponent name            = new ByXMPPComponent(XMPPComponentBy.name);

    ByXMPPComponent(XMPPComponentBy identityBy)
    {
      mXMPPComponentBy = identityBy;
    }

    XMPPComponentBy toZimbra()
    {
      return mXMPPComponentBy;
    }

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

    public static ByDistributionList id   = new ByDistributionList(DistributionListBy.id);
    public static ByDistributionList name = new ByDistributionList(DistributionListBy.name);

    ByDistributionList(DistributionListBy identityBy)
    {
      mDistributionListBy = identityBy;
    }

    DistributionListBy toZimbra()
    {
      return mDistributionListBy;
    }

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

    public static ByUCService id   = new ByUCService(UCServiceBy.id);
    public static ByUCService name = new ByUCService(UCServiceBy.name);

    ByUCService(UCServiceBy identityBy)
    {
      mUCServiceBy = identityBy;
    }

    UCServiceBy toZimbra()
    {
      return mUCServiceBy;
    }

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

    public static ByShareLocator id = new ByShareLocator(ShareLocatorBy.id);

    ByShareLocator(ShareLocatorBy identityBy)
    {
      mShareLocatorBy = identityBy;
    }

    ShareLocatorBy toZimbra()
    {
      return mShareLocatorBy;
    }

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
