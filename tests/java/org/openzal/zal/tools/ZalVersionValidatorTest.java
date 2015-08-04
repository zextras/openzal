package org.openzal.zal.tools;

import org.junit.Before;
import org.junit.Test;
import org.openzal.zal.lib.ZimbraVersion;

import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZalVersionValidatorTest
{
  private ZalVersionValidator mVersionValidator;
  private Attributes          mAttributes;

  @Before
  public void setup()
  {
    mAttributes = mock(Attributes.class);

    Manifest manifest = mock(Manifest.class);
    when(manifest.getMainAttributes()).thenReturn(mAttributes);
    JarInputStream jar = mock(JarInputStream.class);
    when(jar.getManifest()).thenReturn(manifest);

    mVersionValidator = new ZalVersionValidator(jar);
  }

  @Test(expected = RuntimeException.class)
  public void zimbra_version_mismatch()
  {
    when(mAttributes.getValue("Implementation-Version")).thenReturn("8.0.0");

    mVersionValidator.validateZimbraVersion(new ZimbraVersion(8, 0, 1));
  }

  @Test
  public void zimbra_valid_version()
  {
    when(mAttributes.getValue("Implementation-Version")).thenReturn("8.0.1");

    mVersionValidator.validateZimbraVersion(new ZimbraVersion(8, 0, 1));
  }
}