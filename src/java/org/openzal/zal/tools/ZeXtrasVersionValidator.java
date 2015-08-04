package org.openzal.zal.tools;

import org.openzal.zal.lib.Version;

import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class ZeXtrasVersionValidator
{
  private final static String ATTR_ZAL_REQUIRED = "ZAL-Required-Version";

  private final Manifest mManifest;

  public ZeXtrasVersionValidator(JarInputStream jar)
  {
    mManifest = jar.getManifest();
  }

  private Version getRequiredZALVersion()
  {
    return new Version(mManifest.getMainAttributes().getValue(ATTR_ZAL_REQUIRED));
  }

  public void validateZalVersion(Version zalVersion)
  {
    Version requiredZalVersion = getRequiredZALVersion();
    if (zalVersion.getMajor() != requiredZalVersion.getMajor() ||
        zalVersion.getMinor() != requiredZalVersion.getMinor() ||
        zalVersion.getMicro() < requiredZalVersion.getMicro()
      )
    {
      throw new RuntimeException(
        "Unable to load extension ZeXtras: it requires ZAL version "+
          requiredZalVersion.toString()+" but current version is "+zalVersion.toString()
      );
    }

  }
}
