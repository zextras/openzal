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

public class ZEKey
{
  public static class ZEAccountBy
  {
    private final AccountBy mAccountBy;

    public static ZEAccountBy adminName        = new ZEAccountBy(AccountBy.adminName);
    public static ZEAccountBy appAdminName     = new ZEAccountBy(AccountBy.appAdminName);
    public static ZEAccountBy id               = new ZEAccountBy(AccountBy.id);
    public static ZEAccountBy foreignPrincipal = new ZEAccountBy(AccountBy.foreignPrincipal);
    public static ZEAccountBy name             = new ZEAccountBy(AccountBy.name);
    public static ZEAccountBy krb5Principal    = new ZEAccountBy(AccountBy.krb5Principal);

    ZEAccountBy(AccountBy accountBy)
    {
      mAccountBy = accountBy;
    }

    public static ZEAccountBy fromString(String s) throws ServiceException
    {
      try {
        return new ZEAccountBy(AccountBy.valueOf(s));
      } catch (IllegalArgumentException e) {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }

    AccountBy toZimbra()
    {
      return mAccountBy;
    }
  }

  // data sources
  public static class ZEDataSourceBy
  {

    private final DataSourceBy mDataSourceBy;

    public static ZEDataSourceBy id   = new ZEDataSourceBy(DataSourceBy.id);
    public static ZEDataSourceBy name = new ZEDataSourceBy(DataSourceBy.name);

    ZEDataSourceBy(DataSourceBy dataSourceBy)
    {
      mDataSourceBy = dataSourceBy;
    }

    DataSourceBy toZimbra()
    {
      return mDataSourceBy;
    }

    public static ZEDataSourceBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEDataSourceBy(DataSourceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  // identities
  public static class ZEIdentityBy
  {
    private final IdentityBy mIdentityBy;

    public static ZEIdentityBy id   = new ZEIdentityBy(IdentityBy.id);
    public static ZEIdentityBy name = new ZEIdentityBy(IdentityBy.name);

    ZEIdentityBy(IdentityBy identityBy)
    {
      mIdentityBy = identityBy;
    }

    IdentityBy toZimbra()
    {
      return mIdentityBy;
    }

    public static ZEIdentityBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEIdentityBy(IdentityBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZEDomainBy
  {
    private DomainBy mDomainBy;

    public static ZEDomainBy id              = new ZEDomainBy(DomainBy.id);
    public static ZEDomainBy name            = new ZEDomainBy(DomainBy.name);
    public static ZEDomainBy virtualHostname = new ZEDomainBy(DomainBy.virtualHostname);
    public static ZEDomainBy krb5Realm       = new ZEDomainBy(DomainBy.krb5Realm);
    /* $if ZimbraVersion >= 8.0.0 $ */
    public static ZEDomainBy foreignName     = new ZEDomainBy(DomainBy.foreignName);
    /* $else$
    public static ZEDomainBy foreignName     = new ZEDomainBy(DomainBy.name);
    /* $endif $ */

    ZEDomainBy(DomainBy domainBy)
    {
      mDomainBy = domainBy;
    }

    DomainBy toZimbra()
    {
      return mDomainBy;
    }

    public static ZEDomainBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEDomainBy(DomainBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZEServerBy
  {
    private ServerBy mServerBy;

    public static ZEServerBy id              = new ZEServerBy(ServerBy.id);
    public static ZEServerBy name            = new ZEServerBy(ServerBy.name);
    public static ZEServerBy serviceHostname = new ZEServerBy(ServerBy.serviceHostname);

    ZEServerBy(ServerBy serverBy)
    {
      mServerBy = serverBy;
    }

    ServerBy toZimbra()
    {
      return mServerBy;
    }

    public static ZEServerBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEServerBy(ServerBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZEZimletBy
  {
    private final ZimletBy mZimletBy;

    public static ZEZimletBy id   = new ZEZimletBy(ZimletBy.id);
    public static ZEZimletBy name = new ZEZimletBy(ZimletBy.name);

    ZEZimletBy(ZimletBy identityBy)
    {
      mZimletBy = identityBy;
    }

    ZimletBy toZimbra()
    {
      return mZimletBy;
    }

    public static ZEZimletBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEZimletBy(ZimletBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  // signatures
  public static class ZESignatureBy
  {
    private final SignatureBy mSignatureBy;

    public static ZESignatureBy id   = new ZESignatureBy(SignatureBy.id);
    public static ZESignatureBy name = new ZESignatureBy(SignatureBy.name);

    ZESignatureBy(SignatureBy identityBy)
    {
      mSignatureBy = identityBy;
    }

    SignatureBy toZimbra()
    {
      return mSignatureBy;
    }

    public static ZESignatureBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZESignatureBy(SignatureBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZECacheEntryBy
  {
    private final CacheEntryBy mCacheEntryBy;

    public static ZECacheEntryBy id   = new ZECacheEntryBy(CacheEntryBy.id);
    public static ZECacheEntryBy name = new ZECacheEntryBy(CacheEntryBy.name);

    ZECacheEntryBy(CacheEntryBy identityBy)
    {
      mCacheEntryBy = identityBy;
    }

    CacheEntryBy toZimbra()
    {
      return mCacheEntryBy;
    }

    public static ZECacheEntryBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZECacheEntryBy(CacheEntryBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZECosBy
  {
    private final CosBy mCosBy;

    public static ZECosBy id   = new ZECosBy(CosBy.id);
    public static ZECosBy name = new ZECosBy(CosBy.name);

    ZECosBy(CosBy identityBy)
    {
      mCosBy = identityBy;
    }

    CosBy toZimbra()
    {
      return mCosBy;
    }

    public static ZECosBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZECosBy(CosBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZECalendarResourceBy
  {
    private final CalendarResourceBy mCalendarResourceBy;

    public static ZECalendarResourceBy id               = new ZECalendarResourceBy(CalendarResourceBy.id);
    public static ZECalendarResourceBy foreignPrincipal = new ZECalendarResourceBy(CalendarResourceBy.foreignPrincipal);
    public static ZECalendarResourceBy name             = new ZECalendarResourceBy(CalendarResourceBy.name);

    ZECalendarResourceBy(CalendarResourceBy identityBy)
    {
      mCalendarResourceBy = identityBy;
    }

    CalendarResourceBy toZimbra()
    {
      return mCalendarResourceBy;
    }

    public static ZECalendarResourceBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZECalendarResourceBy(CalendarResourceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZEXMPPComponentBy
  {
    private final XMPPComponentBy mXMPPComponentBy;

    public static ZEXMPPComponentBy id = new ZEXMPPComponentBy(XMPPComponentBy.id);
    public static ZEXMPPComponentBy serviceHostname = new ZEXMPPComponentBy(XMPPComponentBy.serviceHostname);
    public static ZEXMPPComponentBy name            = new ZEXMPPComponentBy(XMPPComponentBy.name);

    ZEXMPPComponentBy(XMPPComponentBy identityBy)
    {
      mXMPPComponentBy = identityBy;
    }

    XMPPComponentBy toZimbra()
    {
      return mXMPPComponentBy;
    }

    public static ZEXMPPComponentBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEXMPPComponentBy(XMPPComponentBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  public static class ZEDistributionListBy
  {
    private final DistributionListBy mDistributionListBy;

    public static ZEDistributionListBy id   = new ZEDistributionListBy(DistributionListBy.id);
    public static ZEDistributionListBy name = new ZEDistributionListBy(DistributionListBy.name);

    ZEDistributionListBy(DistributionListBy identityBy)
    {
      mDistributionListBy = identityBy;
    }

    DistributionListBy toZimbra()
    {
      return mDistributionListBy;
    }

    public static ZEDistributionListBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEDistributionListBy(DistributionListBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  /* $if ZimbraVersion >= 8.0.0 $ */
  static class ZEUCServiceBy
  {
    private final UCServiceBy mUCServiceBy;

    public static ZEUCServiceBy id   = new ZEUCServiceBy(UCServiceBy.id);
    public static ZEUCServiceBy name = new ZEUCServiceBy(UCServiceBy.name);

    ZEUCServiceBy(UCServiceBy identityBy)
    {
      mUCServiceBy = identityBy;
    }

    UCServiceBy toZimbra()
    {
      return mUCServiceBy;
    }

    public static ZEUCServiceBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEUCServiceBy(UCServiceBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }

  static class ZEShareLocatorBy
  {
    private final ShareLocatorBy mShareLocatorBy;

    public static ZEShareLocatorBy id = new ZEShareLocatorBy(ShareLocatorBy.id);

    ZEShareLocatorBy(ShareLocatorBy identityBy)
    {
      mShareLocatorBy = identityBy;
    }

    ShareLocatorBy toZimbra()
    {
      return mShareLocatorBy;
    }

    public static ZEShareLocatorBy fromString(String s) throws ServiceException
    {
      try
      {
        return new ZEShareLocatorBy(ShareLocatorBy.valueOf(s));
      }
      catch (IllegalArgumentException e)
      {
        throw ExceptionWrapper.wrap(ServiceException.INVALID_REQUEST("unknown key: " + s, e));
      }
    }
  }
  /* $endif $ */
}
