package com.zextras.lib;

import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountHelperTest {
  private ZEProvisioningSimulator mProvisioning;

  @Before
  public void setUp() throws Exception {
    mProvisioning = new ZEProvisioningSimulator();
  }

  @Test
  public void test_requireTheNameOfAccount_returnTheAccountName()
  {
    mProvisioning.addUser("test", "test@example.com");
    AccountHelper accountHelper = new AccountHelper("test@example.com", mProvisioning);
    assertEquals("test", accountHelper.getName());
    assertEquals("test@example.com", accountHelper.getMainAddress());
  }

  @Test
  public void test_requireTheNameOfNonExistingAccount_returnTheEmail()
  {
    AccountHelper accountHelper = new AccountHelper("test@example.com", mProvisioning);
    assertEquals("test@example.com", accountHelper.getName());
    assertEquals("test@example.com", accountHelper.getMainAddress());
  }
}
