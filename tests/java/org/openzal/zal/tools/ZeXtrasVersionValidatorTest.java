package org.openzal.zal.tools;

import org.junit.Before;
import org.junit.Test;
import org.openzal.zal.lib.Version;

import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZeXtrasVersionValidatorTest
{
  private ZeXtrasVersionValidator mVersionValidator;
  private Attributes              mAttributes;

  @Before
  public void setup()
  {
    mAttributes = mock(Attributes.class);

    Manifest manifest = mock(Manifest.class);
    when(manifest.getMainAttributes()).thenReturn(mAttributes);
    JarInputStream jar = mock(JarInputStream.class);
    when(jar.getManifest()).thenReturn(manifest);

    mVersionValidator = new ZeXtrasVersionValidator(jar);
  }

  @Test(expected = RuntimeException.class)
  public void zal_version_mismatch()
  {
    when(mAttributes.getValue("ZAL-Required-Version")).thenReturn("2.0.1");

    mVersionValidator.validateZalVersion(new Version(2, 0, 0));
  }

  @Test
  public void zal_valid_version()
  {
    when(mAttributes.getValue("ZAL-Required-Version")).thenReturn("2.0.1");

    mVersionValidator.validateZalVersion(new Version(2, 0, 2));
  }
}