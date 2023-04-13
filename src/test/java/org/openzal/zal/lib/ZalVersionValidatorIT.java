package org.openzal.zal.lib;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZalVersionValidatorIT
{
  private ZalVersionValidator mValidator;
  private JarAccessor         mJar;

  @Before
  public void setup() throws Exception
  {
    mValidator = new ZalVersionValidator();
    mJar = mock(JarAccessor.class);
    when(mJar.getAttributeInManifest("Implementation-Version")).thenReturn("1.2.3");
    when(mJar.getAttributeInManifest("Specification-Version")).thenReturn("3.2.1");
  }

  @Test
  public void validating_same_zimbra_version() throws Exception
  {
    mValidator.validate(mJar, new ZimbraVersion(1, 2, 3));
  }

  @Test
  public void validating_different_zimbra_version_fails() throws Exception
  {
    mValidator.validate(mJar, new ZimbraVersion(1, 2, 4));
  }
}