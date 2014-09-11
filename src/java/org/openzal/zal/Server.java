/*
 * ZAL - The abstraction layer for Zimbra.
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
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server extends Entry
{
  @NotNull private final com.zimbra.cs.account.Server mServer;

  protected Server(@NotNull Object server)
  {
    super(server);
    mServer = (com.zimbra.cs.account.Server) server;
  }

  public Server(
    String name,
    String id,
    Map<String, Object> attrs,
    Map<String, Object> defaults,
    @NotNull Provisioning prov
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

  @NotNull
  public Collection<String> getHsmPolicy()
  {
    return Arrays.asList(mServer.getHsmPolicy());
  }

  public void setHsmPolicy(@NotNull Collection<String> zimbraHsmPolicy)
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

  @NotNull
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

  @NotNull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mServer.getAttrs(applyDefaults));
  }

  public String getAttr(String string1)
  {
    return mServer.getAttr(string1);
  }

  public boolean isXMPPEnabled()
  {
    return mServer.isXMPPEnabled();
  }

  @NotNull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mServer.getMultiAttrSet(name));
  }

  @NotNull
  public Collection<String> getMultiAttr(String name)
  {
    return Arrays.asList(mServer.getMultiAttr(name));
  }

  public String getAttr(String name, String defaultValue)
  {
    return mServer.getAttr(name, defaultValue);
  }

  @NotNull
  public Collection<String> getServiceEnabled()
  {
    return Arrays.asList(mServer.getServiceEnabled());
  }

  protected <T> T toZimbra(@NotNull Class<T> cls)
  {
    return cls.cast(mServer);
  }

  public int getIntAttr(String name, int defaultValue)
  {
    return mServer.getIntAttr(name, defaultValue);
  }

  @NotNull
  public String getAdminURL(String path) {
    String hostname = getAttr(Provisioning.A_zimbraServiceHostname);
    int port = getIntAttr(Provisioning.A_zimbraAdminPort, 0);
    StringBuffer sb = new StringBuffer(128);
    sb.append(LC.zimbra_admin_service_scheme.value()).append(hostname).append(":").append(port).append(path);
    return sb.toString();
  }
}

