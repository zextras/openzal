package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class LdapServerConfig
{
  @NotNull
  private final com.zimbra.cs.ldap.LdapServerConfig mLdapServerConfig;

  public LdapServerConfig(@NotNull Object ldapServerConfig)
  {
    mLdapServerConfig = (com.zimbra.cs.ldap.LdapServerConfig)ldapServerConfig;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLdapServerConfig);
  }

}
