package org.openzal.zal.lib;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExtensionVersionValidatorIT
{
  private ExtensionVersionValidator mValidator;
  private JarAccessor               mJar;

  @Before
  public void setup() throws Exception
  {
    mJar = mock(JarAccessor.class);

    mValidator = new ExtensionVersionValidator();
  }

  @Test
  public void validating_same_zal_version() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(1,2,3));
  }

  @Test
  public void validating_greater_zal_micro_version() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(1,2,4));
  }

  @Test
  public void validating_lower_zal_micro_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(1,2,2));
  }

  @Test
  public void validating_different_zal_minor_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(1,3,3));
  }

  @Test
  public void validating_different_zal_major_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(2,2,3));
  }

  @Test
  public void validating_different_zal_major_and_minor_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, Version.of(2,3,3));
  }
}