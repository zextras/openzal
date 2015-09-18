package org.openzal.zal.lib;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ExtensionJarValidator
{
  public final static String ATTR_ZAL_REQUIRED = "ZAL-Required-Version";

  public void validate(JarAccessor jar, Version zalVersion) throws IOException, NoSuchAlgorithmException
  {
    validate(jar, zalVersion, false);
  }

  public void validateForceDigestValidation(JarAccessor jar, Version zalVersion)
    throws IOException, NoSuchAlgorithmException
  {
    validate(jar, zalVersion, true);
  }

  public void validate(JarAccessor jar, Version zalVersion, boolean forceDigestValidation)
    throws IOException, NoSuchAlgorithmException
  {
    String requiredZalVersionString = jar.getAttributeInManifest(ATTR_ZAL_REQUIRED);
    if (requiredZalVersionString != null && !requiredZalVersionString.isEmpty())
    {
      Version requiredZalVersion = new Version(requiredZalVersionString);
      if (!zalVersion.truncate(2).equals(requiredZalVersion.truncate(2)) ||
           requiredZalVersion.getMicro() > zalVersion.getMicro()
        )
      {
        throw new RuntimeException(
          "Unable to load extension " + jar.getPath() + ": it requires ZAL version " +
            requiredZalVersion.toString() + " but current version is " + zalVersion.toString()
        );
      }
    }

    jar.validateDigest(forceDigestValidation);
  }
}
