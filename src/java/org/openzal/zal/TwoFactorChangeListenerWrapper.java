package org.openzal.zal;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.auth.twofactor.TwoFactorAuth;

public class TwoFactorChangeListenerWrapper extends TwoFactorAuth.TwoFactorChangeListener
{
  private final TwoFactorChangeListener mListener;

  public TwoFactorChangeListenerWrapper(TwoFactorChangeListener listener)
  {
    mListener = listener;
  }

  @Override
  public void twoFactorAuthEnabled(Account acct)
  {
    mListener.twoFactorAuthEnabled(new org.openzal.zal.Account(acct));
  }

  @Override
  public void twoFactorAuthDisabled(Account acct)
  {
    mListener.twoFactorAuthDisabled(new org.openzal.zal.Account(acct));
  }

  @Override
  public void appSpecificPasswordRevoked(Account acct, String appName)
  {
    mListener.appSpecificPasswordRevoked(new org.openzal.zal.Account(acct), appName);
  }
}
