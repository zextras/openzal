package com.zimbra.cs.account.ldap;


/* $if ZimbraVersion >= 8.0.0 $*/
import com.zimbra.cs.account.ldap.entry.LdapDomain;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.ZAttributes;
/* $else$
import com.zimbra.cs.account.ldap.LdapDomain;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
 $endif$ */

import com.zimbra.cs.account.Provisioning;

import java.util.Map;

public class LdapDomainProxy extends LdapDomain
{
  public LdapDomainProxy(
    String dn,

/* $if ZimbraVersion >= 8.0.0 $*/
    ZAttributes attrs,
/* $else$
    Attributes attrs,
 $endif$ */

    Map<String, Object> defaults,
    Provisioning prov
  )
/* $if ZimbraVersion >= 8.0.0 $*/
    throws LdapException
/* $else$
    throws NamingException
 $endif$ */
  {
    super(dn, attrs, defaults, prov);
  }
}
