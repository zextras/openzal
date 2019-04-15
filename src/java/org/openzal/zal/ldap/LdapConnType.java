package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class LdapConnType
{
  @Nonnull
  private final com.zimbra.cs.ldap.LdapConnType mLdapConnType;

  public final static LdapConnType PLAIN = new LdapConnType(com.zimbra.cs.ldap.LdapConnType.PLAIN);
  public final static LdapConnType LDAPS = new LdapConnType(com.zimbra.cs.ldap.LdapConnType.LDAPS);
  public final static LdapConnType STARTTLS = new LdapConnType(com.zimbra.cs.ldap.LdapConnType.STARTTLS);
  public final static LdapConnType LDAPI = new LdapConnType(com.zimbra.cs.ldap.LdapConnType.LDAPI);

  public LdapConnType(@Nonnull Object ldapConnType)
  {
    mLdapConnType = (com.zimbra.cs.ldap.LdapConnType)ldapConnType;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLdapConnType);
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof LdapConnType)
    {
      LdapConnType that = (LdapConnType)o;
      return mLdapConnType == that.mLdapConnType;
    }
    return false;
  }
}
