package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class LdapServerType
{
  @Nonnull
  private final com.zimbra.cs.ldap.LdapServerType mLdapServerType;

  public final static LdapServerType MASTER = new LdapServerType(com.zimbra.cs.ldap.LdapServerType.MASTER);
  public final static LdapServerType REPLICA = new LdapServerType(com.zimbra.cs.ldap.LdapServerType.REPLICA);

  public LdapServerType(@Nonnull Object ldapServerType)
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
