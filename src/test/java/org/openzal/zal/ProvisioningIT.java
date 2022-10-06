package org.openzal.zal;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openzal.zal.lib.ZimbraVersion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProvisioningIT
{
  private Provisioning mProvisioning;

  @Before
  public void setup()
  {
    mProvisioning = new ProvisioningImp(null);
  }

  @Test
  public void is_valid_uid_with_valid_return_true()
  {
    assertTrue(mProvisioning.isValidUid("e0fafd89-1360-11d9-8661-000a95d98ef2"));
  }

  @Test
  public void not_valid_uid_with_valid_return_false()
  {
    assertFalse(mProvisioning.isValidUid("e0fafd89x1360y11d9-8661-000a95d98ef2"));
    assertFalse(mProvisioning.isValidUid("ciao"));
  }
}
