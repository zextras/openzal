package org.openzal.zal;

import com.zimbra.cs.mailbox.ZalZimbraSimulator;
import com.zimbra.soap.account.message.GetSMIMEPublicCertsRequest;
import com.zimbra.soap.account.message.GetSMIMEPublicCertsResponse;
import com.zimbra.soap.account.type.SMIMEPublicCertInfo;
import com.zimbra.soap.account.type.SMIMEPublicCertsInfo;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openzal.zal.extension.ConfigZimletStatus;
import org.openzal.zal.soap.SoapTransport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SuppressWarnings("ConstantConditions")
public class AccountIT
{
  private ZalZimbraSimulator mZimbraSimulator;
  private Provisioning    mProvisioning;
  private Account         mAccount;
  private Domain          mMainDomain;

  @Before
  public void setup() throws Exception
  {
    mZimbraSimulator = new ZalZimbraSimulator();
    mProvisioning = mZimbraSimulator.getProvisioning();
    mMainDomain = mProvisioning.createDomain("example.com",new HashMap<String, Object>());
    mAccount = mProvisioning.createAccount("test@example.com","",new HashMap<String, Object>());
  }

  @After
  public void cleanup() throws Exception
  {
    mZimbraSimulator.cleanup();
  }

  @Test
  public void no_aliases_only_one_address_returned() throws Exception
  {
    List<String> aliases;

    aliases = new LinkedList<String>(
      mAccount.getAllAddressesIncludeDomainAliases(mProvisioning)
    );

    assertEquals(
      1L,
      (long) aliases.size()
    );
    assertEquals(
      "test@example.com",
      aliases.get(0)
    );
  }

  @Test
  public void two_alias_three_addresses_returned() throws Exception
  {
    mAccount.addAlias("alias_1@example.com");
    mAccount.addAlias("alias_2@example.com");

    List<String> aliases;

    aliases = new LinkedList<String>(
      mAccount.getAllAddressesIncludeDomainAliases(mProvisioning)
    );
    Collections.sort(aliases);

    assertEquals(
      3L,
      (long) aliases.size()
    );
    assertEquals(
      "alias_1@example.com",
      aliases.get(0)
    );
    assertEquals(
      "alias_2@example.com",
      aliases.get(1)
    );
    assertEquals(
      "test@example.com",
      aliases.get(2)
    );
  }

  @Test
  public void one_alias_plus_domain_alias_four_addresses_returned() throws Exception
  {
    List<String> aliases;

    mAccount.addAlias("alias@example.com");
    Domain aliasDomain = mProvisioning.createDomain("aliasdomain.com", new HashMap<String, Object>());

    HashMap<String, Object> attrs = new HashMap<String, Object>();
    attrs.put("zimbraDomainAliasTargetId", mMainDomain.getId());
    mProvisioning.modifyAttrs(aliasDomain,attrs);

    aliases = new LinkedList<String>(
      mAccount.getAllAddressesIncludeDomainAliases(mProvisioning)
    );
    Collections.sort(aliases);

    assertEquals(
      4L,
      (long) aliases.size()
    );
    assertEquals(
      "alias@aliasdomain.com",
      aliases.get(0)
    );
    assertEquals(
      "alias@example.com",
      aliases.get(1)
    );
    assertEquals(
      "test@aliasdomain.com",
      aliases.get(2)
    );
    assertEquals(
      "test@example.com",
      aliases.get(3)
    );
  }

  @Test
  public void alias_on_other_domain_returned() throws Exception
  {
    List<String> aliases;

    mAccount.addAlias("alias@otherdomain.com");
    mProvisioning.createDomain("otherdomain.com", new HashMap<String, Object>());
    Domain aliasDomain = mProvisioning.createDomain("example-alias.com", new HashMap<String, Object>());
    HashMap<String, Object> attrs = new HashMap<String, Object>();
    attrs.put("zimbraDomainAliasTargetId", mMainDomain.getId());
    mProvisioning.modifyAttrs(aliasDomain,attrs);

    aliases = new LinkedList<String>(
      mAccount.getAllAddressesIncludeDomainAliases(mProvisioning)
    );
    Collections.sort(aliases);

    assertEquals(
      3L,
      (long) aliases.size()
    );
    assertEquals(
      "alias@otherdomain.com",
      aliases.get(0)
    );
    assertEquals(
      "test@example-alias.com",
      aliases.get(1)
    );
    assertEquals(
      "test@example.com",
      aliases.get(2)
    );
  }

  @Test
  public void test_user_mandatory_zimlets()
  {
    String[] installedZimlets = {"!zimlet1", "+zimlet2", "-zimlet3"};
    HashMap<String, Object> attrsMap = new HashMap<>();
    attrsMap.put("zimbraZimletAvailableZimlets", installedZimlets);
    mAccount.setAttrs(attrsMap);
    Map<String, ConfigZimletStatus> userZimlets = mAccount.getUserAvailableZimlets();
    assertEquals(3, userZimlets.entrySet().size());
    assertEquals(userZimlets.get("zimlet1"), ConfigZimletStatus.Mandatory);
    assertEquals(userZimlets.get("zimlet2"), ConfigZimletStatus.Enabled);
    assertEquals(userZimlets.get("zimlet3"), ConfigZimletStatus.Disabled);
  }

  @Test
  public void include_allow_from_addresses() throws Exception
  {
    HashMap<String, Object> attrs = new HashMap<String, Object>();
    attrs.put("zimbraAllowFromAddress", "other@domain123.com");
    mProvisioning.modifyAttrs(mAccount,attrs);

    LinkedList<String> aliases = new LinkedList<String>(
      mAccount.getAllAddressesAllowedInFrom(mProvisioning)
    );
    Collections.sort(aliases);

    assertEquals(
      2L,
      (long) aliases.size()
    );
    assertEquals(
      "other@domain123.com",
      aliases.get(0)
    );
    assertEquals(
      "test@example.com",
      aliases.get(1)
    );
  }

  @Test
  public void getCertificates()
    throws Exception
  {
    SoapTransport soapTransport = mock(SoapTransport.class);
    GetSMIMEPublicCertsResponse response = mock(GetSMIMEPublicCertsResponse.class);
    SMIMEPublicCertsInfo certs = mock(SMIMEPublicCertsInfo.class);
    List<SMIMEPublicCertInfo> certList = new ArrayList<SMIMEPublicCertInfo>()
    {{
      SMIMEPublicCertInfo cert = mock(SMIMEPublicCertInfo.class);

      doAnswer(new Answer<String>()
      {
        @Override
        public String answer(InvocationOnMock invocation)
          throws Throwable
        {
          String data;

          try( FileReader file = new FileReader("it/data/certificates/comodo.crt") )
          {
            data = Utils.encodeFSSafeBase64(IOUtils.toByteArray(file));
          }
          return data;
        }
      }).when(cert).getValue();

      add(cert);
    }};

    when(soapTransport.invokeWithoutSession(any(GetSMIMEPublicCertsRequest.class))).thenReturn(response);
    /* $if ZimbraVersion >= 8.8.12 || ZimbraX == 1 $ */
    when(response.getCerts()).thenReturn(Collections.singletonList(certs));
    /* $else$
    when(response.getCerts()).thenReturn(certs);
    /* $endif$ */
    when(certs.getCerts()).thenReturn(certList);

    assertEquals(
      "MIIFPjCCBCagAwIBAgIRAOL624PLR41T3r9g9rVuE0QwDQYJKoZIhvcNAQELBQAw"
        + "gZcxCzAJBgNVBAYTAkdCMRswGQYDVQQIExJHcmVhdGVyIE1hbmNoZXN0ZXIxEDAO"
        + "BgNVBAcTB1NhbGZvcmQxGjAYBgNVBAoTEUNPTU9ETyBDQSBMaW1pdGVkMT0wOwYD"
        + "VQQDEzRDT01PRE8gUlNBIENsaWVudCBBdXRoZW50aWNhdGlvbiBhbmQgU2VjdXJl"
        + "IEVtYWlsIENBMB4XDTE4MDQwNTAwMDAwMFoXDTE5MDQwNTIzNTk1OVowKjEoMCYG"
        + "CSqGSIb3DQEJARYZZGV2MUB6aW1icmE4OC56ZXh0cmFzLmNvbTCCASIwDQYJKoZI"
        + "hvcNAQEBBQADggEPADCCAQoCggEBAMShhiKsnzbZVdKFiUsAxLZMGQYJA2d7fUws"
        + "5PGs/RbXdqLoRgLoHHZ60YH2WYtl2t97szO3tJuJGC2vyrUTYjUoT0ykWzGXrPKV"
        + "0SVVhZW8J9Dz2gy+klMuOfVIPgaLikL/P9tXhEPOkr7t3fHeWKLm+j5q7VzZbjU3"
        + "Y/v0ApXa13MUYWxSQDxV1jzHStugLGmN8dLASGEtfrzf3W4kFCuvfd8EiteCgVca"
        + "ZCCxyhSb/3ttU+U2LyYWyhFHRqpEeIIeHimj0Mu/Bw6qzl8/syzgZtZJSGYiWm8s"
        + "by11T7yg6tBBCdpFNMLBGGOPUQe70a7BD7LaltD1c+p+een2jcECAwEAAaOCAe8w"
        + "ggHrMB8GA1UdIwQYMBaAFIKvbIz4xf6WYXzoHz0rcUhexIvAMB0GA1UdDgQWBBRO"
        + "CeKo+WahAnpSV874SryCOt7DBzAOBgNVHQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIw"
        + "ADAgBgNVHSUEGTAXBggrBgEFBQcDBAYLKwYBBAGyMQEDBQIwEQYJYIZIAYb4QgEB"
        + "BAQDAgUgMEYGA1UdIAQ/MD0wOwYMKwYBBAGyMQECAQEBMCswKQYIKwYBBQUHAgEW"
        + "HWh0dHBzOi8vc2VjdXJlLmNvbW9kby5uZXQvQ1BTMFoGA1UdHwRTMFEwT6BNoEuG"
        + "SWh0dHA6Ly9jcmwuY29tb2RvY2EuY29tL0NPTU9ET1JTQUNsaWVudEF1dGhlbnRp"
        + "Y2F0aW9uYW5kU2VjdXJlRW1haWxDQS5jcmwwgYsGCCsGAQUFBwEBBH8wfTBVBggr"
        + "BgEFBQcwAoZJaHR0cDovL2NydC5jb21vZG9jYS5jb20vQ09NT0RPUlNBQ2xpZW50"
        + "QXV0aGVudGljYXRpb25hbmRTZWN1cmVFbWFpbENBLmNydDAkBggrBgEFBQcwAYYY"
        + "aHR0cDovL29jc3AuY29tb2RvY2EuY29tMCQGA1UdEQQdMBuBGWRldjFAemltYnJh"
        + "ODguemV4dHJhcy5jb20wDQYJKoZIhvcNAQELBQADggEBADgwBRPcnCavwfYUJwIv"
        + "OmpZajYfnXwk/CzmEnqtuG+KiD9rt8xoeTOOoUW+HbQfH1bIgS6+zwz1Y66+wh+W"
        + "sSVFnfAIGv/JKLHXX9i7/xEK11MARn9F5FmEfW+1TbXONMBjPj1+DpCe0Q4GPUYq"
        + "GVVBR4cb9FUGwI9Aqy1aZEUkhOA5u86rKq39J+i/tWtytw01VKmukMXv72LzT51R"
        + "nluZgg7B4BlkUU43jwf4+o5FH2u5M0AUWqJqXCp9wAEe6weFzKnSRoXY4AEvEctI"
        + "CBRHkXiGZ45cnujXb4PoZcoTtokL4JQN/xIcma08s1GvH24z3rOtC5AEVwlZ0gcW"
        + "LmE=",
      mAccount.getCertificates(soapTransport).get(0));
  }
}