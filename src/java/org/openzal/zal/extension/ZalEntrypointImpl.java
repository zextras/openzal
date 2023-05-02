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

import javax.annotation.Nullable;
import org.openzal.zal.BuildProperties;
import org.openzal.zal.ZalVersion;
import org.openzal.zal.lib.JarAccessor;
import org.openzal.zal.lib.Version;
import org.openzal.zal.lib.ZimbraVersion;
import org.openzal.zal.log.ZimbraLog;
import org.openzal.zal.tools.JarUtils;
import org.openzal.zal.lib.ExtensionVersionValidator;

import java.io.File;
import java.lang.ref.WeakReference;

public class ZalEntrypointImpl implements ZalEntrypoint
{
  private       String                    mDirectoryName;
  private       File                      mDirectory;
  private       ExtensionManager          mExtensionManager;
  private       boolean                   mExtensionPathExists;
  private       ZalEntrypoint             mZalEntryPoint;
  private       File                      mCustomExtensionDirectory;

  @Nullable
  private WeakReference<ClassLoader> mPreviousExtension;

  private static final String ZAL_FILE     = "/zal.jar";
  private static final String ZEXTRAS_FILE = "/carbonio.jar";

  public ZalEntrypointImpl()
  {
    mExtensionManager = null;
    mDirectoryName = JarUtils.getCurrentJar().getParentFile().getName();
    mDirectory = JarUtils.getCurrentJar().getParentFile();
    mExtensionPathExists = false;
    mZalEntryPoint = null;
    mCustomExtensionDirectory = null;
    mPreviousExtension = new WeakReference<ClassLoader>(null);
  }

  private ExtensionManager getExtensionManager()
  {
    try
    {
      if (mExtensionManager == null)
      {
        mExtensionManager = (ExtensionManager) this.getClass().getClassLoader().loadClass(
          "org.openzal.zal.extension.ExtensionManagerImpl"
        ).newInstance();
      }

      return mExtensionManager;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void provideCustomClassLoader(ClassLoader classLoader)
  {
    getExtensionManager().setCustomClassLoader(classLoader);
  }

  @Override
  public void provideCustomExtensionController(ZalExtensionController zalExtensionController)
  {
    getExtensionManager().setCustomZalExtensionController(zalExtensionController);
  }

  @Override
  public void provideCustomExtensionDirectory(File extensionDirectory)
  {
    mDirectory = extensionDirectory;
    getExtensionManager().setCustomExtensionDirectory(extensionDirectory);
  }

  @Override
  public void providePreviousExtension(WeakReference<ClassLoader> previousExtension)
  {
    mPreviousExtension = previousExtension;
  }

  @Override
  public String getName()
  {
    return "Zimbra Abstraction Layer for: " + mDirectoryName;
  }

  @Override
  public void init()
  {
    ZimbraLog.mailbox.info("Starting ZAL version " + ZalVersion.current + " commit " + BuildProperties.getCommitFull());

    File extensionPathFile = new File(mDirectory, "extension-path");
    mExtensionPathExists = extensionPathFile.exists();

    try
    {
      if (mExtensionPathExists)
      {
        ZimbraLog.mailbox.info("File "+extensionPathFile.getAbsolutePath()+" present, using tiny boot");

        if( mCustomExtensionDirectory != null )
        {
          mZalEntryPoint = new TinyBoot(extensionPathFile).createZalEntryPoint(mCustomExtensionDirectory, new Controller());
        }
        else
        {
          mZalEntryPoint = new TinyBoot(extensionPathFile).createZalEntryPoint(new Controller());
        }

        mZalEntryPoint.providePreviousExtension(mPreviousExtension);
        mZalEntryPoint.init();
      }
      else
      {
        Zimbra.overrideExtensionMap();
        ZimbraLog.mailbox.info("File "+extensionPathFile.getAbsolutePath()+" not present, using standard boot");
        getExtensionManager().loadExtension();
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Unable to load extension", e);
    }
  }

  class Controller implements ZalExtensionController
  {
    private void checkState()
    {
      if( mZalEntryPoint == null )
      {
        throw new RuntimeException();
      }
    }

    @Override
    public void shutdown()
    {
      checkState();
      destroy();
    }

    @Override
    public void reboot()
    {
      checkState();
      destroy();
      init();
      postInit();
    }

    @Override
    public void reload(File extensionDirectory, WeakReference<ClassLoader> previousClassLoader)
    {
      checkState();
      mPreviousExtension = previousClassLoader;
      destroy();
      mCustomExtensionDirectory = extensionDirectory;
      init();
      postInit();
    }

    @Override
    public boolean canControlExtension()
    {
      return true;
    }
  }

  @Override
  public void postInit()
  {
    if( mZalEntryPoint != null )
    {
      mZalEntryPoint.postInit();
    }
    else
    {
      if( mExtensionManager != null )
      {
        mExtensionManager.startExtension(mPreviousExtension);
      }
    }
  }

  @Override
  public void destroy()
  {
    if( mZalEntryPoint != null )
    {
      mZalEntryPoint.destroy();
    }
    else
    {
      if( mExtensionManager != null )
      {
        mExtensionManager.shutdownExtension();
      }
    }

    WeakReference<Object> weakReference = new WeakReference<Object>(new Object());

    while (weakReference.get() != null)
    {
      System.gc();
    }

    mZalEntryPoint = null;
  }
}
