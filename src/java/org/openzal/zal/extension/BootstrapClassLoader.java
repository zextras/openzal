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

import org.jetbrains.annotations.NotNull;
import sun.misc.Resource;
import sun.misc.URLClassPath;

import java.io.IOException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@SuppressWarnings({"SynchronizedMethod", "rawtypes", "CustomClassloader"})
public class BootstrapClassLoader extends ClassLoader
{
  private static final CodeSigner[] sEmptyCodeSigner = new CodeSigner[0];
  private static final Permissions  sAllPermission   = new Permissions();
  static {
    sAllPermission.add(new AllPermission());
  }

  private final boolean              mDelegateZalLoading;
  private final URLClassPath         mUrlClassPath;
  private final AccessControlContext mAccessControl;

  public BootstrapClassLoader(URL[] urls, ClassLoader parent, boolean delegateZalLoading)
  {
    super(parent);
    mUrlClassPath = new URLClassPath(urls);
    mDelegateZalLoading = delegateZalLoading;
    mAccessControl = AccessController.getContext();
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException
  {
    return loadClass(name, true);
  }

  @Override
  protected synchronized Class loadClass(@NotNull String name, boolean resolve)
    throws ClassNotFoundException
  {
    if (mDelegateZalLoading)
    {
      if (name.startsWith("org.openzal."))
      {
        return getParent().loadClass(name);
      }
    }
    else
    {
      if (name.equals("org.openzal.zal.extension.ZalExtension") ||
          name.equals("org.openzal.zal.extension.ZalEntrypoint") ||
          name.equals("org.openzal.zal.extension.ZalExtensionController"))
      {
        return getParent().loadClass(name);
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

  protected Class<?> findClass(final String name)
    throws ClassNotFoundException
  {
    String path = name.replace('.', '/').concat(".class");
    Resource res = mUrlClassPath.getResource(path, false);
    if (res != null)
    {
      try
      {
        return defineClass(name, res);
      }
      catch (IOException e)
      {
        throw new ClassNotFoundException(name, e);
      }
    }
    else
    {
      throw new ClassNotFoundException(name);
    }
  }

  private String secondIfNull(String str1, String str2)
  {
    if( str1 == null )
    {
      return str2;
    }
    return str1;
  }

  protected Package definePackageIfMissing(
    String packageName,
    Manifest man,
    URL url
  )
    throws IllegalArgumentException
  {
    Package pkg = getPackage(packageName);
    if( pkg != null )
    {
      return pkg;
    }

    String specTitle = null, specVersion = null, specVendor = null;
    String implTitle = null, implVersion = null, implVendor = null;

    if( man != null )
    {
      Attributes attr = man.getAttributes(
        packageName.replace('.', '/').concat("/")
      );
      if (attr != null)
      {
        specTitle   = attr.getValue(Attributes.Name.SPECIFICATION_TITLE);
        specVersion = attr.getValue(Attributes.Name.SPECIFICATION_VERSION);
        specVendor  = attr.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        implTitle   = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        implVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        implVendor  = attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
      }

      attr = man.getMainAttributes();
      if (attr != null)
      {
        specTitle = secondIfNull(specTitle, attr.getValue(Attributes.Name.SPECIFICATION_TITLE));
        specVersion = secondIfNull(specVersion,attr.getValue(Attributes.Name.SPECIFICATION_VERSION));
        specVendor = secondIfNull(specVendor,attr.getValue(Attributes.Name.SPECIFICATION_VENDOR));
        implTitle = secondIfNull(implTitle,attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE));
        implVersion = secondIfNull(implVersion,attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION));
        implVendor = secondIfNull(implVendor,attr.getValue(Attributes.Name.IMPLEMENTATION_VENDOR));
      }
    }

    return definePackage(
      packageName,
      specTitle,
      specVersion,
      specVendor,
      implTitle,
      implVersion,
      implVendor,
      null
    );
  }

  private Class<?> defineClass(String name, Resource res) throws IOException
  {
    int lastIndexOf = name.lastIndexOf('.');
    URL url = res.getCodeSourceURL();
    if (lastIndexOf != -1)
    {
      definePackageIfMissing(
        name.substring(0, lastIndexOf),
        res.getManifest(),
        url
      );
    }

    java.nio.ByteBuffer rawClassBuffer = res.getByteBuffer();
    if (rawClassBuffer != null)
    {
      CodeSource cs = new CodeSource(url, sEmptyCodeSigner);
      ProtectionDomain domain = new ProtectionDomain(cs, sAllPermission);

      return defineClass(
        name,
        rawClassBuffer,
        domain
      );
    }
    else
    {
      CodeSource cs = new CodeSource(url, sEmptyCodeSigner);
      ProtectionDomain domain = new ProtectionDomain(cs, sAllPermission);

      byte[] rawClass = res.getBytes();
      return defineClass(
        name,
        rawClass,
        0,
        rawClass.length,
        domain
      );
    }
  }
}
