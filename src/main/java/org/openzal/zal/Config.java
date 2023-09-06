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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.extension.ZimbraExtension;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.NoSuchDomainException;

public class Config extends Entry
{
  @Nonnull private final com.zimbra.cs.account.Config mConfig;

  public Config(@Nonnull Object config)
  {
    super(config);
    mConfig = (com.zimbra.cs.account.Config) config;
  }

  public Config(HashMap<String, Object> hashMap, @Nonnull Provisioning provisioning)
  {
    this(
      new com.zimbra.cs.account.Config(
        hashMap,
        provisioning.toZimbra(com.zimbra.cs.account.Provisioning.class)
      )
    );
  }

  public String getVersionCheckNotificationEmail()
  {
    return mConfig.getVersionCheckNotificationEmail();
  }

  @Nonnull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mConfig.getMultiAttrSet(name));
  }

  public String getAttr(String name)
  {
    return mConfig.getAttr(name);
  }

  @Nonnull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mConfig.getAttrs(applyDefaults));
  }

  public @Nonnull Domain getDefaultDomain()
  {
    String defaultDomainName = mConfig.getAttr("zimbraDefaultDomainName","");
    try
    {
      Object domain = mConfig.getProvisioning().getDomainByName(defaultDomainName);
      if( domain == null )
      {
        throw new NoSuchDomainException(defaultDomainName);
      }

      return new Domain(domain);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getAttr(String name, String defaultValue)
  {
    return mConfig.getAttr(name, defaultValue);
  }

  public String getAttr(String name, boolean applyDefaults)
  {
    return mConfig.getAttr(name, applyDefaults);
  }

  public void setMessageIdDedupeCacheSize(int messageIdDedupeCacheSize)
  {
    try
    {
      mConfig.setMessageIdDedupeCacheSize(messageIdDedupeCacheSize);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public <A> A readAttribute(LDAPAttributeReader<Config,A> attr) {
    return attr.read(this);
  }

  public static <A> LDAPAttributeReader<Config,A> createAttribute(LDAPAttributeReader<Entry, A> attr) {
    return attr.compose(config ->  config);
  }
}

