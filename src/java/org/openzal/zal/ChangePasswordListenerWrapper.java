package org.openzal.zal;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.ldap.ChangePasswordListener;

import java.util.Map;

public class ChangePasswordListenerWrapper extends ChangePasswordListener
{
  private final org.openzal.zal.ChangePasswordListener mListener;

  public ChangePasswordListenerWrapper(org.openzal.zal.ChangePasswordListener listener)
  {
    mListener = listener;
  }

  @Override
  public void preModify(Account acct, String newPassword, Map context, Map<String, Object> attrsToModify)
  {
    mListener.invokePreModify(new org.openzal.zal.Account(acct), newPassword, context, attrsToModify);
  }

  @Override
  public void postModify(Account acct, String newPassword, Map context)
  {
    mListener.invokePostModify(new org.openzal.zal.Account(acct), newPassword, context);
  }
}
