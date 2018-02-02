package org.openzal.zal.ldap;

import org.jetbrains.annotations.NotNull;

public class ResultCode
{
  @NotNull
  private final com.unboundid.ldap.sdk.ResultCode mResultCode;

  public static final ResultCode INVALID_CREDENTIALS = new ResultCode(com.unboundid.ldap.sdk.ResultCode.INVALID_CREDENTIALS);
  public static final ResultCode AUTHORIZATION_DENIED = new ResultCode(com.unboundid.ldap.sdk.ResultCode.AUTHORIZATION_DENIED);
  public static final ResultCode PARAM_ERROR = new ResultCode(com.unboundid.ldap.sdk.ResultCode.PARAM_ERROR);

  public ResultCode(@NotNull Object resultCode)
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
    return mResultCode.equals(o);
  }

}
