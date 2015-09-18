package org.openzal.zal.lib;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ZalJarValidatorTest
{
  private ZalJarValidator mValidator;
  private JarAccessor     mJar;

  @Before
  public void setup() throws Exception
  {
    mValidator = new ZalJarValidator();
    mJar = mock(JarAccessor.class);
    when(mJar.getAttributeInManifest("Implementation-Version")).thenReturn("1.2.3");
    when(mJar.getAttributeInManifest("Specification-Version")).thenReturn("3.2.1");
  }

  @Test
  public void validating_digest_with_force_true() throws Exception
  {
    mValidator.validate(mJar, new ZimbraVersion(1, 2, 3));

    verify(mJar, times(1)).validateDigest(true);
  }

  @Test
  public void validating_same_zimbra_version() throws Exception
  {
    mValidator.validateVersion(mJar, new ZimbraVersion(1, 2, 3));
  }

  @Test ( expected = RuntimeException.class )
  public void validating_different_zimbra_version_fails() throws Exception
  {
    mValidator.validateVersion(mJar, new ZimbraVersion(1, 2, 4));
  }
}