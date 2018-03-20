package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LdapServerPool
{
  @NotNull
  private final com.zimbra.cs.ldap.unboundid.LdapServerPool mLdapServerPool;

  public LdapServerPool(@NotNull ZimbraLdapConfig ldapServerConfig)
    throws LDAPException
  {
    try
    {
      mLdapServerPool = new com.zimbra.cs.ldap.unboundid.LdapServerPool(ldapServerConfig.toZimbra(com.zimbra.cs.ldap.LdapServerConfig.class));
    } catch (com.zimbra.cs.ldap.LdapException e)
    {
      throw new LDAPException(e);
    }
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLdapServerPool);
  }

  public List<LDAPURL> getUrls()
  {
    List<com.unboundid.ldap.sdk.LDAPURL> urls = mLdapServerPool.getUrls();
    List<LDAPURL> result = new ArrayList<LDAPURL>();

    for (com.unboundid.ldap.sdk.LDAPURL url : urls)
    {
      result.add(new LDAPURL(url));
    }

    return result;
  }

}
