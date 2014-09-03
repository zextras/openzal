package com.zextras.admin.report;

import com.zextras.admin.ZxAdminAction;
import com.zextras.admin.ZxDelegationSettings;
import com.zextras.admin.ZxDomainAdminPrivilege;
import org.openzal.zal.ZEAccount;
import org.openzal.zal.ZEDomain;
import org.openzal.zal.ZEProvisioningSimulator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AdminReportUpdaterTest
{
  private AdminReportUpdater      mAdminReportUpdater;
  private ZEProvisioningSimulator mProvisioning;

  @Before
  public void setup()
  {
    mAdminReportUpdater = new AdminReportUpdater();
    mProvisioning = new ZEProvisioningSimulator();
  }

  private void populateAdminActitityReport(ZxAdminActivityReport adminActivityReport )
  {
    ZxAdminActivityStats exampleAdminActivityStats = new ZxAdminActivityStats();
    exampleAdminActivityStats.registerAction(
      new ZxAdminAction("admin@example.com","lol",false, "127.0.0.1", 0)
    );
    adminActivityReport.mAdminActivity.put("admin@example.com", exampleAdminActivityStats);

    ZxAdminActivityStats testAdminActivityStats = new ZxAdminActivityStats();
    testAdminActivityStats.registerAction(
      new ZxAdminAction("admin@test.com", "asd", false, "127.0.0.1", 0)
    );
    adminActivityReport.mAdminActivity.put("admin@test.com", testAdminActivityStats);
  }

  @Test
  public void only_delegated_admin_test_com_is_removed_from_action_list() throws Exception
  {
    ZEAccount domainAdminExample = mProvisioning.addUser("admin@example.com");
    ZEAccount domainAdminTest = mProvisioning.addUser("admin@test.com");

    domainAdminExample.setIsDelegatedAdminAccount(true);
    domainAdminTest.setIsDelegatedAdminAccount(true);

    ZEDomain domainExample = mProvisioning.createFakeDomain("example.com");
    ZEDomain domainTest = mProvisioning.createFakeDomain("test.com");

    ZxDelegationSettings zxDelegationSettings = new ZxDelegationSettings(
      Arrays.<ZxDomainAdminPrivilege>asList(
        new ZxDomainAdminPrivilege(domainAdminExample, domainExample),
        new ZxDomainAdminPrivilege(domainAdminTest, domainTest)
      )
    );

    ZxAdminActivityReport zxAdminActivityReport = new ZxAdminActivityReport();
    populateAdminActitityReport( zxAdminActivityReport );

    assertEquals(
      1,
      zxAdminActivityReport.mAdminActivity.get("admin@example.com").mAdminClientActivity.size()
    );

    assertEquals(
      1,
      zxAdminActivityReport.mAdminActivity.get("admin@test.com").mAdminClientActivity.size()
    );

    mAdminReportUpdater.filterReportForDelegatedAdmin(
      zxAdminActivityReport,
      zxDelegationSettings,
      domainAdminExample,
      mProvisioning
    );

    assertNull(
      zxAdminActivityReport.mAdminActivity.get("admin@test.com")
    );

    assertEquals(
      1,
      zxAdminActivityReport.mAdminActivity.get("admin@example.com").mAdminClientActivity.size()
    );
  }
}
