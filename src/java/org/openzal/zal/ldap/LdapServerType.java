package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class LdapServerType
{
  @NotNull
  private final com.zimbra.cs.ldap.LdapServerType mLdapServerType;

  public final static LdapServerType MASTER = new LdapServerType(com.zimbra.cs.ldap.LdapServerType.MASTER);
  public final static LdapServerType REPLICA = new LdapServerType(com.zimbra.cs.ldap.LdapServerType.REPLICA);

  public LdapServerType(@NotNull Object ldapServerType)
  {
    mLdapServerType = (com.zimbra.cs.ldap.LdapServerType)ldapServerType;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLdapServerType);
  }

  public boolean isMaster() {
    return mLdapServerType.isMaster();
  }
}
