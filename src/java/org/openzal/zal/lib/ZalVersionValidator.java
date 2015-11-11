package org.openzal.zal.lib;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ZalVersionValidator
{
  private final static String ATTR_VERSION            = "Specification-Version";
  private final static String ATTR_ZAL_IMPLEMENTATION = "Implementation-Version";

  public Version validate(JarAccessor jar, ZimbraVersion zimbraVersion) throws IOException, NoSuchAlgorithmException
  {
    validateVersion(jar, zimbraVersion);

    return new Version(jar.getAttributeInManifest(ATTR_VERSION));
  }

  private void validateVersion(JarAccessor jar, ZimbraVersion zalVersion) throws IOException
  {
    Version zalImplementationVersion = new Version(jar.getAttributeInManifest(ATTR_ZAL_IMPLEMENTATION));
    if (!zalImplementationVersion.equals(zalVersion))
    {
      throw new RuntimeException(
        "Zimbra version mismatch - ZAL " + jar.getPath() + " built for Zimbra: " + zalImplementationVersion.toString()
      );
    }
  }
}
