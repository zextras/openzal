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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Domain extends Entry
{
  private final com.zimbra.cs.account.Domain mDomain;

  public Domain(@NotNull Object domain)
  {
    super(domain);
    mDomain = (com.zimbra.cs.account.Domain)domain;
  }

  public Domain(
    String name,
    String id,
    Map<String, Object> attrs,
    Map<String, Object> defaults,
    Provisioning prov
  ) {
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

  public void modify(Map<String, Object> attrs)
  {
    try
    {
      mDomain.modify(attrs);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getName()
  {
    return mDomain.getName();
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mDomain.getAttrs(applyDefaults));
  }

  public long getMailDomainQuota()
  {
    /* $if ZimbraVersion >= 8.0.0 $ */
    return mDomain.getMailDomainQuota();
    /* $else $
    return -1L;
    /* $endif$ */
  }

  public void setDomainCOSMaxAccounts(Collection<String> zimbraDomainCOSMaxAccounts)
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

  public String getDomainAliasTargetId()
  {
    return mDomain.getDomainAliasTargetId();
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

  public Collection<String> getDomainCOSMaxAccounts()
  {
    return Arrays.asList(mDomain.getDomainCOSMaxAccounts());
  }

  public String getPasswordChangeListener()
  {
    return mDomain.getPasswordChangeListener();
  }

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mDomain.getMultiAttrSet(name));
  }

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

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mDomain);
  }

  public long getLongAttr(String name, int defaultValue)
  {
    return mDomain.getLongAttr(name, defaultValue);
  }
}

