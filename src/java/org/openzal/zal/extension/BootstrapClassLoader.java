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

package org.openzal.zal.extension;

import javax.annotation.Nonnull;
import org.openzal.zal.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@SuppressWarnings({"SynchronizedMethod", "rawtypes", "CustomClassloader"})
public class BootstrapClassLoader extends ClassLoader
{
  private static final CodeSigner[] sEmptyCodeSigner = new CodeSigner[0];
  private static final Permissions  sAllPermission   = new Permissions();
  static {
    sAllPermission.add(new AllPermission());
  }

  private final boolean   mDelegateZalLoading;
  private final JarFile[] mJarFileList;
  private final URL[]     mUrls;
  private       boolean   mInitialized;

  public BootstrapClassLoader(URL[] urls, ClassLoader parent, boolean delegateZalLoading)
  {
    super(parent);
    mUrls = urls;
    mJarFileList = new JarFile[urls.length];
    mInitialized = false;
    mDelegateZalLoading = delegateZalLoading;
  }

  private void initilize() throws IOException
  {
    mInitialized = true;
    for( int n=0; n < mUrls.length; ++n)
    {
      mJarFileList[n] = new JarFile(
        new File(mUrls[n].getFile())
      );
    }
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException
  {
    return loadClass(name, true);
  }

  @Override
  protected Class loadClass(@Nonnull String name, boolean resolve)
    throws ClassNotFoundException
  {
    //System.out.printf("#### Loading class %s ",name);

    if(!mInitialized)
    {
      try
      {
        initilize();
      }
      catch (IOException e)
      {
        throw new ClassNotFoundException(name,e);
      }
    }

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
      catch (LinkageError ex)
      {
/*
  to understand if it was a concurrent class definition issue, we search the class again,
  if it's already loaded then we ignore the exception.
  this solution is way more safe then breaking java locking mechanism which may arise
  random deadlocks
*/
        cls = findLoadedClass(name);
        if( cls == null )
        {
          throw ex;
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

    for( int n=0; n < mJarFileList.length; ++n)
    {
      JarFile jarFile = mJarFileList[n];
      JarEntry entry = jarFile.getJarEntry(path);
      if (entry != null)
      {
        try
        {
          return defineClass(name, mUrls[n], jarFile, entry);
        }
        catch (IOException e)
        {
          throw new ClassNotFoundException(name, e);
        }
      }
    }

    throw new ClassNotFoundException(name);
  }

  @Override
  protected URL findResource(String name) {
    try
    {
      Enumeration<URL> enumation = getResources(name);
      if( enumation.hasMoreElements() )
      {
        return enumation.nextElement();
      }
      else
      {
        return null;
      }
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException
  {
    final LinkedList<URL> urls = new LinkedList<URL>();

    for( int n=0; n < mJarFileList.length; ++n)
    {
      JarFile jarFile = mJarFileList[n];
      JarEntry entry = jarFile.getJarEntry(name);
      if (entry != null)
      {
        //jar:file:/opt/zimbra/lib/ext/zextras/zextras.jar!/META-INF/services/com.hazelcast.instance.NodeExtension
        URL url = new URL("jar:"+mUrls[n]+"!/"+name);
        urls.add(url);
      }
    }

    return new Enumeration<URL>()
    {
      int idx = 0;

      @Override
      public boolean hasMoreElements()
      {
        return urls.size() > idx;
      }

      @Override
      public URL nextElement()
      {
        return urls.get(idx++);
      }
    };
  }

  private String secondIfNull(String str1, String str2)
  {
    if( str1 == null )
    {
      return str2;
    }
    return str1;
  }

  private void definePackageIfMissing(
    String packageName,
    Manifest man,
    URL url
  )
  {
    if( getPackage(packageName) != null )
    {
      return;
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

    try
    {
      definePackage(
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
    catch (IllegalArgumentException ignore){
      //could happen in concurrent class loading
      //another thread has already defined the package
    }
  }

  private Class<?> defineClass(String name, URL url, JarFile jarFile, JarEntry entry) throws IOException
  {
    int lastIndexOf = name.lastIndexOf('.');
    if (lastIndexOf != -1)
    {
      definePackageIfMissing(
        name.substring(0, lastIndexOf),
        jarFile.getManifest(),
        url
      );
    }

    byte[] buffer = new byte[ 32 * 1024 ];
    int idx = 0;
    InputStream inputStream = jarFile.getInputStream(entry);
    try
    {
      while( true )
      {
        int read = inputStream.read(buffer, idx, buffer.length - idx);
        if (read < 0)
        {
          break;
        }
        idx += read;
        if( buffer.length - idx == 0 )
        {
          buffer = Arrays.copyOf(buffer, buffer.length * 2);
        }
      }
    }
    finally
    {
      inputStream.close();
    }

    CodeSource cs = new CodeSource(url, sEmptyCodeSigner);
    ProtectionDomain domain = new ProtectionDomain(cs, sAllPermission);

    return defineClass(
      name,
      buffer,
      0,
      idx,
      domain
    );
  }
}
