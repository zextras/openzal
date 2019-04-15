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

import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cos extends Entry
{
  @Nonnull private final com.zimbra.cs.account.Cos mCos;

  protected Cos(@Nonnull Object cos)
  {
    super(cos);
    mCos = (com.zimbra.cs.account.Cos) cos;
  }

  public Cos(
    String name,
    String id,
    Map<String, Object> attrs,
    @Nonnull Provisioning prov
  )
  {
    this(
      new com.zimbra.cs.account.Cos(
        name,
        id,
        attrs,
        prov.toZimbra(com.zimbra.cs.account.Provisioning.class)
      )
    );
  }

  @Nonnull
  public Collection<String> getACE()
  {
    return Arrays.asList(mCos.getACE());
  }

  public long getMailQuota()
  {
    return mCos.getMailQuota();
  }

  @Nonnull
  public Set<String> getMultiAttrSet(String name)
  {
    return new HashSet<String>(mCos.getMultiAttrSet(name));
  }

  public String getId()
  {
    return mCos.getId();
  }

  public void setACE(@Nonnull Collection<String> strings)
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

  @Nonnull
  public Map<String, Object> getAttrs(boolean applyDefaults)
  {
    return new HashMap<String, Object>(mCos.getAttrs(applyDefaults));
  }

  public String getNotes()
  {
    return mCos.getNotes();
  }

  @Override
  public boolean equals(@Nullable Object o)
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

    Cos zeCos = (Cos) o;

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

  @Nonnull
  com.zimbra.cs.account.Cos toZimbra()
  {
    return mCos;
  }
}

