package org.openzal.zal;

import com.zimbra.cs.account.Account;
/* $if ZimbraVersion >= 8.7.0 $ */
import com.zimbra.cs.account.auth.twofactor.TwoFactorAuth;
/* $endif $ */

public class TwoFactorAuthChangeListenerWrapper
{
  private final TwoFactorAuthChangeListener mListener;

  TwoFactorAuthChangeListenerWrapper(TwoFactorAuthChangeListener listener)
  {
    mListener = listener;
  }

  public void register(String name)
  {
    /* $if ZimbraVersion >= 8.7.0 $ */
    TwoFactorAuth.TwoFactorChangeListener.register(name, new TwoFactorAuth.TwoFactorChangeListener()
    {
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
    });
    /* $endif $ */
  }

  public static TwoFactorAuthChangeListenerWrapper wrap(TwoFactorAuthChangeListener listener)
  {
    return new TwoFactorAuthChangeListenerWrapper(listener);
  }
}
