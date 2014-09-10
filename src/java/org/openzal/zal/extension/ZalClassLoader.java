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

package org.openzal.zal.extension;

import java.net.URL;
import java.net.URLClassLoader;

public class ZalClassLoader extends URLClassLoader {

  public ZalClassLoader(URL[] urls, ClassLoader parent)
  {
    super(urls, parent);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException
  {
    return loadClass(name, true);
  }

  @Override
  protected synchronized Class loadClass(String name, boolean resolve)
    throws ClassNotFoundException
  {
    if( name.startsWith("org.openzal.") )
    {
      if( super.getParent() != null)
      {
        Class cls = super.getParent().loadClass(name);
        if (resolve) {
          resolveClass(cls);
        }
        return cls;
      }
    }

    // First, check if the class has already been loaded
    Class cls = findLoadedClass(name);
    if (cls == null)
    {
      try
      {
        cls = findClass(name);
      }
      catch (ClassNotFoundException e)
      {
        if( super.getParent() != null)
        {
          cls = super.getParent().loadClass(name);
        }
      }
    }
    if (resolve) {
      resolveClass(cls);
    }
    return cls;
  }
}
