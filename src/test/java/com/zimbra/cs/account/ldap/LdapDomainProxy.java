package com.zimbra.cs.account.ldap;


import com.zimbra.cs.account.ldap.entry.LdapDomain;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.ZAttributes;

import com.zimbra.cs.account.Provisioning;

import java.util.Map;

public class LdapDomainProxy extends LdapDomain
{
  public LdapDomainProxy(
    String dn,

    ZAttributes attrs,

    Map<String, Object> defaults,
    Provisioning prov
  )
    throws LdapException
  {
    super(dn, attrs, defaults, prov);
  }
}
