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

package org.openzal.zal.provisioning;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import com.zimbra.cs.ldap.unboundid.UBIDLdapFilter;
import org.openzal.zal.Utils;
import org.openzal.zal.exceptions.ExceptionWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class DirectQueryFilterBuilder
{
  private static final Constructor<?> mConstructor;

  static
  {
    mConstructor = UBIDLdapFilter.class.getDeclaredConstructors()[0];
    mConstructor.setAccessible(true);
  }

  public static UBIDLdapFilter create(String query) throws LDAPException
  {
    try
    {
      return (UBIDLdapFilter)mConstructor.newInstance(
        ZLdapFilterFactory.FilterId.TODO,
        Filter.create(query)
      );
    }
    catch (InstantiationException|IllegalAccessException|InvocationTargetException e)
    {
      if( e.getCause() instanceof Exception)
      {
        throw ExceptionWrapper.wrap((java.lang.Exception)e.getCause());
      }
      else
      {
        throw new RuntimeException(e);
      }
    }
  }
}
