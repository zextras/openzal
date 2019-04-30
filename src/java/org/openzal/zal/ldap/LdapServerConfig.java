package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class LdapServerConfig
{
  @Nonnull
  private final com.zimbra.cs.ldap.LdapServerConfig mLdapServerConfig;

  public LdapServerConfig(@Nonnull Object ldapServerConfig)
  {
    mLdapServerConfig = (com.zimbra.cs.ldap.LdapServerConfig)ldapServerConfig;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLdapServerConfig);
  }

}
