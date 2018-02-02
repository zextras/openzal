package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class LDAPException extends Exception
{
  @NotNull
  private final com.unboundid.ldap.sdk.LDAPException mLDAPException;

  public LDAPException(@NotNull Object ldapException)
  {
    mLDAPException = (com.unboundid.ldap.sdk.LDAPException)ldapException;
  }

  public LDAPException(@NotNull ResultCode paramError, String s)
  {
    mLDAPException = new com.unboundid.ldap.sdk.LDAPException(paramError.toZimbra(com.unboundid.ldap.sdk.ResultCode.class),s);
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLDAPException);
  }

  public ResultCode getResultCode()
  {
    return new ResultCode(mLDAPException.getResultCode());
  }
}
