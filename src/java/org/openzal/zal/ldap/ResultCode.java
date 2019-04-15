package org.openzal.zal.ldap;

import javax.annotation.Nonnull;

public class ResultCode
{
  @Nonnull
  private final com.unboundid.ldap.sdk.ResultCode mResultCode;

  public static final ResultCode SUCCESS = new ResultCode(com.unboundid.ldap.sdk.ResultCode.SUCCESS);
  public static final ResultCode CONNECT_ERROR = new ResultCode(com.unboundid.ldap.sdk.ResultCode.CONNECT_ERROR);
  public static final ResultCode INVALID_CREDENTIALS = new ResultCode(com.unboundid.ldap.sdk.ResultCode.INVALID_CREDENTIALS);
  public static final ResultCode AUTHORIZATION_DENIED = new ResultCode(com.unboundid.ldap.sdk.ResultCode.AUTHORIZATION_DENIED);
  public static final ResultCode PARAM_ERROR = new ResultCode(com.unboundid.ldap.sdk.ResultCode.PARAM_ERROR);

  public ResultCode(@Nonnull Object resultCode)
  {
    mResultCode = (com.unboundid.ldap.sdk.ResultCode)resultCode;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mResultCode);
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof ResultCode)
    {
      return mResultCode.equals(((ResultCode) o).toZimbra(com.unboundid.ldap.sdk.ResultCode.class));
    }
    return false;
  }

}
