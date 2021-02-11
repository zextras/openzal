package org.openzal.zal;

import com.zimbra.cs.account.auth.ZimbraCustomAuth;

import java.util.List;
import java.util.Map;

public abstract class CustomAuth
{
  private final Provisioning mProvisioning;

  private static class RequestProxyClass extends ZimbraCustomAuth
  {
    private final CustomAuth mCustomAuth;

    public RequestProxyClass(CustomAuth customAuth)
    {
      mCustomAuth = customAuth;
    }

    @Override
    public void authenticate(
      com.zimbra.cs.account.Account account, String password, Map<String, Object> context, List<String> args
    )
      throws Exception
    {
      mCustomAuth.authenticate(new Account(account), password, context);
    }
  }

  public CustomAuth(Provisioning provisioning)
  {
    mProvisioning = provisioning;
  }

  public static void register(String name, CustomAuth customAuth)
  {
    ZimbraCustomAuth.register(name, new RequestProxyClass(customAuth));
  }

  public void authenticate(
    Account account, String password, Map<String, Object> context
  ) throws Exception
  {
    authenticateInternal(account, password, context);
  }

  private void authenticateInternal(Account account, String password, Map<String, Object> context)
  {
    mProvisioning.authAccountWithLdap(account, password, context);
  }
}
