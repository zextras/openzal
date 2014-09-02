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

import com.zimbra.cs.account.Provisioning;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZEServer extends ZEEntry
{
  private final com.zimbra.cs.account.Server mServer;

  protected ZEServer(@NotNull Object server)
  {
    super(server);
    mServer = (com.zimbra.cs.account.Server)server;
  }

  public ZEServer(
    String name,
    String id,
    Map<String,Object> attrs,
    Map<String,Object> defaults,
    ZEProvisioning prov
  ) {
    this(
      new Server(
        name,
        id,
        attrs,
        defaults,
        prov.toZimbra(Provisioning.class)
      )
    );
  }

  public Collection<String> getHsmPolicy()
  {
    return Arrays.asList(mServer.getHsmPolicy());
  }

  public void setHsmPolicy(Collection<String> zimbraHsmPolicy)
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

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mServer.getMultiAttrSet(name));
  }

  public Collection<String> getMultiAttr(String name)
  {
    return Arrays.asList(mServer.getMultiAttr(name));
  }

  public String getAttr(String name, String defaultValue)
  {
    return mServer.getAttr(name, defaultValue);
  }

  public Collection<String> getServiceEnabled()
  {
    return Arrays.asList(mServer.getServiceEnabled());
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mServer);
  }

  public int getIntAttr(String name, int defaultValue)
  {
    return mServer.getIntAttr(name, defaultValue);
  }

  public String getAdminURL(String path) {
    String hostname = getAttr(ZEProvisioning.A_zimbraServiceHostname);
    int port = getIntAttr(ZEProvisioning.A_zimbraAdminPort, 0);
    StringBuffer sb = new StringBuffer(128);
    sb.append(LC.zimbra_admin_service_scheme.value()).append(hostname).append(":").append(port).append(path);
    return sb.toString();
  }
}

