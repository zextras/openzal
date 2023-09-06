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
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.soap.admin.message.BackupQueryRequest;
import com.zimbra.soap.admin.message.BackupQueryResponse;
import com.zimbra.soap.admin.type.BackupQueryAccounts;
import com.zimbra.soap.admin.type.BackupQueryInfo;
import com.zimbra.soap.admin.type.BackupQuerySpec;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.soap.SoapTransport;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server extends Entry
{
  @Nonnull private final com.zimbra.cs.account.Server mServer;

  protected Server(@Nonnull Object server)
  {
    super(server);
    mServer = (com.zimbra.cs.account.Server) server;
  }

  public Server(
    String name,
    String id,
    Map<String, Object> attrs,
    Map<String, Object> defaults,
    @Nonnull Provisioning prov
  )
  {
    this(
      new com.zimbra.cs.account.Server(
        name,
        id,
        attrs,
        defaults,
        prov.toZimbra(com.zimbra.cs.account.Provisioning.class)
      )
    );
  }

  public String getServerHostname()
  {
    return mServer.getServiceHostname();
  }

  @Nonnull
  public Collection<String> getHsmPolicy()
  {
    return Arrays.asList(mServer.getHsmPolicy());
  }

  public boolean isLdapGentimeFractionalSecondsEnabled()
  {
    return mServer.isLdapGentimeFractionalSecondsEnabled();
  }

  public void setHsmPolicy(@Nonnull Collection<String> zimbraHsmPolicy)
  {
    try
    {
      mServer.setHsmPolicy(zimbraHsmPolicy.toArray(new String[zimbraHsmPolicy.size()]));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public void addHsmPolicy(String zimbraHsmPolicy)
  {
    try
    {
      mServer.addHsmPolicy(zimbraHsmPolicy);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nonnull
  public Collection<String> getServiceInstalled()
  {
    return Arrays.asList(mServer.getServiceInstalled());
  }

  public String getId()
  {
    return mServer.getId();
  }

  public String getName()
  {
    return mServer.getName();
  }

  @Nonnull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mServer.getAttrs(applyDefaults));
  }

  public String getAttr(String string1)
  {
    return mServer.getAttr(string1);
  }

  @Nonnull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mServer.getMultiAttrSet(name));
  }

  @Nonnull
  public Collection<String> getMultiAttr(String name)
  {
    return Arrays.asList(mServer.getMultiAttr(name));
  }

  public String getAttr(String name, String defaultValue)
  {
    return mServer.getAttr(name, defaultValue);
  }

  @Nonnull
  public Collection<String> getServiceEnabled()
  {
    return Arrays.asList(mServer.getServiceEnabled());
  }

  @VisibleForTesting
  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mServer);
  }

  public int getIntAttr(String name, int defaultValue)
  {
    return mServer.getIntAttr(name, defaultValue);
  }

  public boolean hasMailboxService()
  {
    return getMultiAttrSet(
      com.zimbra.cs.account.Provisioning.A_zimbraServiceEnabled
    ).contains(
      com.zimbra.cs.account.Provisioning.SERVICE_MAILBOX
    );
  }

  @Nonnull
  public String getAdminURL(String path)
  {
    return URLUtil.getAdminURL(mServer,path);
  }

  public String getServiceURL(String path)
  {
    try
    {
      return URLUtil.getServiceURL(mServer,path,false);
    }
    catch (ServiceException ex)
    {
      throw ExceptionWrapper.wrap(ex);
    }
  }

  public boolean isNetworkLegacyBackupActive(SoapTransport soapTransport)
  {
    BackupQueryRequest request = new BackupQueryRequest(new BackupQuerySpec());
    try
    {
      BackupQueryResponse response = soapTransport.invoke(request);
      List<BackupQueryInfo> backups = response.getBackups();
      for (BackupQueryInfo backup : backups)
      {
        BackupQueryAccounts accounts = backup.getAccounts();
        if (!accounts.getAccounts().isEmpty())
        {
          return true;
        }
      }
    }
    catch (IOException ignore)
    {}

    return false;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o)
    {
      return true;
    }
    if(o == null || getClass() != o.getClass())
    {
      return false;
    }
    Server server = (Server) o;
    return server.getServerHostname().equals(getServerHostname());
  }

  @Override
  public int hashCode()
  {
    return getServerHostname().hashCode();
  }

  /**
   * Return true if zimbraMailMode is https and false when both or http.
   * Both is treated as http to avoid issues with clients who have broken SSL
   * setup, most zimbra calls {@code URLUtil.getServiceURL()} with
   * {@code preferSSL} at {@code false}.
   *
   * @see com.zimbra.cs.httpclient.URLUtil#getServiceURL
   */
  public boolean isMailModeHttps()
  {
    return "https".equals(getAttr("zimbraMailMode", "http"));
  }
}

