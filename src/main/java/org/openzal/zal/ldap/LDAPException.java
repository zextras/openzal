package org.openzal.zal.ldap;

import com.zimbra.cs.ldap.LdapException;
import javax.annotation.Nonnull;

public class LDAPException extends Exception
{
  @Nonnull
  private final com.unboundid.ldap.sdk.LDAPException mLDAPException;

  public LDAPException(@Nonnull Object ldapException)
  {
    mLDAPException = (com.unboundid.ldap.sdk.LDAPException)ldapException;
  }

  public LDAPException(@Nonnull ResultCode paramError, String s)
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

  public String getDiagnosticMessage()
  {
    return mLDAPException.getDiagnosticMessage();
  }

  public String getMessage()
  {
    com.unboundid.ldap.sdk.ResultCode rc = mLDAPException.getResultCode();
    com.zimbra.cs.ldap.LdapException ldapException = null;

    if (com.unboundid.ldap.sdk.ResultCode.ENTRY_ALREADY_EXISTS == rc)
    {
      ldapException = LdapException.ENTRY_ALREADY_EXIST(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.NOT_ALLOWED_ON_NONLEAF == rc)
    {
      ldapException = LdapException.CONTEXT_NOT_EMPTY(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.UNDEFINED_ATTRIBUTE_TYPE == rc)
    {
      ldapException = LdapException.INVALID_ATTR_NAME(null,mLDAPException);
    }
    else if ( com.unboundid.ldap.sdk.ResultCode.CONSTRAINT_VIOLATION == rc ||
              com.unboundid.ldap.sdk.ResultCode.INVALID_ATTRIBUTE_SYNTAX == rc)
    {
      ldapException = LdapException.INVALID_ATTR_VALUE(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.OBJECT_CLASS_VIOLATION == rc)
    {
      ldapException = LdapException.OBJECT_CLASS_VIOLATION(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.SIZE_LIMIT_EXCEEDED == rc)
    {
      ldapException = LdapException.SIZE_LIMIT_EXCEEDED(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.NO_SUCH_OBJECT == rc)
    {
      ldapException = LdapException.ENTRY_NOT_FOUND(null,mLDAPException);
    }
    else if (com.unboundid.ldap.sdk.ResultCode.FILTER_ERROR == rc)
    {
      ldapException = LdapException.INVALID_SEARCH_FILTER(null,mLDAPException);
    }

    if (ldapException != null)
    {
      return ldapException.getMessage();
    }

    Throwable realException = mLDAPException;
    while (realException.getCause() != null)
    {
      realException = realException.getCause();
    }
    return realException.getMessage();
  }
}
