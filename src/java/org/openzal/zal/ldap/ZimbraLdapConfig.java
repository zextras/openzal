package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class ZimbraLdapConfig
{
  @Nonnull
  private final com.zimbra.cs.ldap.LdapServerConfig.ZimbraLdapConfig mZimbraLdapConfig;

  public ZimbraLdapConfig(@Nonnull org.openzal.zal.ldap.LdapServerType serverType)
  {
    mZimbraLdapConfig = new com.zimbra.cs.ldap.LdapServerConfig.ZimbraLdapConfig(
      serverType.toZimbra(com.zimbra.cs.ldap.LdapServerType.class));
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZimbraLdapConfig);
  }

  public LdapConnType getConnType() {
    return new LdapConnType(mZimbraLdapConfig.getConnType());
  }

  public boolean sslAllowUntrustedCerts() {
    return mZimbraLdapConfig.sslAllowUntrustedCerts();
  }

  public String getLdapURL()
  {
    return mZimbraLdapConfig.getLdapURL();
  }
}
