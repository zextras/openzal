package org.openzal.zal;

import com.zimbra.cs.account.Account;
/* $if ZimbraVersion >= 8.7.0 $ */
import com.zimbra.cs.account.auth.twofactor.TwoFactorAuth;
/* $endif $ */

/* $if ZimbraVersion >= 8.7.0 $ */
public class TwoFactorChangeListenerWrapper extends TwoFactorAuth.TwoFactorChangeListener
/* $else $
public class TwoFactorChangeListenerWrapper
/* $endif $ */
{
  /* $if ZimbraVersion >= 8.7.0 $ */
  private final TwoFactorChangeListener mListener;
  /* $else $
  private final Object mListener;
  /* $endif $ */

  public TwoFactorChangeListenerWrapper(TwoFactorChangeListener listener)
  /* $else $
  public TwoFactorChangeListenerWrapper(Object listener)
  /* $endif $ */
  {
    mListener = listener;
  }

  /* $if ZimbraVersion >= 8.7.0 $ */
  @Override
  public void twoFactorAuthEnabled(Account acct)
  {
    mListener.twoFactorAuthEnabled(new org.openzal.zal.Account(acct));
  }
  /* $else $
  public void twoFactorAuthEnabled(Account acct) {}
  /* $endif $ */

  /* $if ZimbraVersion >= 8.7.0 $ */
  @Override
  public void twoFactorAuthDisabled(Account acct)
  {
    mListener.twoFactorAuthDisabled(new org.openzal.zal.Account(acct));
  }
  /* $else $
  public void twoFactorAuthDisabled(Account acct) {}
  /* $endif $ */

  /* $if ZimbraVersion >= 8.7.0 $ */
  @Override
  public void appSpecificPasswordRevoked(Account acct, String appName)
  {
    mListener.appSpecificPasswordRevoked(new org.openzal.zal.Account(acct), appName);
  }
  /* $else $
  public void appSpecificPasswordRevoked(Account acct, String appName) {}
  /* $endif $ */
}
