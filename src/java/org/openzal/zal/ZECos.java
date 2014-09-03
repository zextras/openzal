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
import com.zimbra.cs.account.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZECos extends ZEEntry
{
  private final com.zimbra.cs.account.Cos mCos;

  protected ZECos(@NotNull Object cos)
  {
    super(cos);
    mCos = (com.zimbra.cs.account.Cos)cos;
  }

  public ZECos(String name,
               String id,
               Map<String,Object> attrs,
               ZEProvisioning prov)
  {
    this(
      new Cos(
        name,
        id,
        attrs,
        prov.toZimbra(Provisioning.class)
      )
    );
  }

  public Collection<String> getACE()
  {
    return Arrays.asList(mCos.getACE());
  }

  public long getMailQuota()
  {
    return mCos.getMailQuota();
  }

  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mCos.getMultiAttrSet(name));
  }

  public void modify(Map<String, Object> attrs)
  {
    try
    {
      mCos.modify(attrs);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getId()
  {
    return mCos.getId();
  }

  public void setACE(Collection<String> strings)
  {
    try
    {
      mCos.setACE(strings.toArray(new String[strings.size()]));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getName()
  {
    return mCos.getName();
  }

  public void setNotes(String zimbraNotes)
  {
    try
    {
      mCos.setNotes(zimbraNotes);
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mCos.getAttrs(applyDefaults));
  }

  public String getNotes()
  {
    return mCos.getNotes();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }

    ZECos zeCos = (ZECos) o;

    if (mCos != null ? !mCos.equals(zeCos.mCos) : zeCos.mCos != null)
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = super.hashCode();
    result = 31 * result + (mCos != null ? mCos.hashCode() : 0);
    return result;
  }

  com.zimbra.cs.account.Cos toZimbra()
  {
    return mCos;
  }
}

