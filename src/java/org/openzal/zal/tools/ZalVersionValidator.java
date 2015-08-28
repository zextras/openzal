package org.openzal.zal.tools;

import org.openzal.zal.lib.Version;
import org.openzal.zal.lib.ZimbraVersion;

import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class ZalVersionValidator
{
  private final static String ATTR_VERSION            = "Specification-Version";
  private final static String ATTR_ZAL_IMPLEMENTATION = "Implementation-Version";

  private final Manifest mManifest;

  public ZalVersionValidator(JarInputStream jar)
  {
    mManifest = jar.getManifest();
  }

  public Version getVersion()
  {
    return new Version(mManifest.getMainAttributes().getValue(ATTR_VERSION));
  }

  private Version getZalImplementationVersion()
  {
    return new Version(mManifest.getMainAttributes().getValue(ATTR_ZAL_IMPLEMENTATION));
  }

  public void validateZimbraVersion(ZimbraVersion current)
  {
    Version zalImplementationVersion = getZalImplementationVersion();
    if (zalImplementationVersion.getMajor() != current.getMajor() ||
        zalImplementationVersion.getMinor() != current.getMinor() ||
        zalImplementationVersion.getMicro() != current.getMicro()
      )
    {
      throw new RuntimeException("Zimbra version mismatch - ZAL built for Zimbra: " + zalImplementationVersion.toString());
    }
  }
}
