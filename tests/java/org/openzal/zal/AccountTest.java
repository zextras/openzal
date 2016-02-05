package org.openzal.zal;

import com.zimbra.cs.mailbox.ZimbraSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


@SuppressWarnings("ConstantConditions")
public class AccountTest
{
  private ZimbraSimulator mZimbraSimulator;
  private Provisioning    mProvisioning;
  private Account         mAccount;
  private Domain          mMainDomain;

  @Before
  public void setup() throws Exception
  {
    mZimbraSimulator = new ZimbraSimulator();
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
}