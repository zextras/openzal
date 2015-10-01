package org.openzal.zal.tools;

import org.openzal.zal.extension.BootstrapClassLoader;

import java.lang.reflect.Method;

public class BootCli
{
  private final BootstrapClassLoader mClassLoader;
  private final String mExtensionCli;

  public BootCli(BootstrapClassLoader classLoader, String extensionCli)
  {
    mClassLoader = classLoader;
    mExtensionCli = extensionCli;
  }

  public void run(String[] args) throws Exception
  {
    Class<?> extensionCliClass = mClassLoader.loadClass(mExtensionCli);
    Method method = extensionCliClass.getDeclaredMethod("main", String[].class);
    Object parameters[] = new Object[1];
    parameters[0] = args;
    method.invoke(null, parameters);
  }
}
