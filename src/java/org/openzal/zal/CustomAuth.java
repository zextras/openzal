package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.auth.AuthMechanism;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openzal.zal.exceptions.ExceptionWrapper;

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
    try
    {
      (mProvisioning.toZimbra(LdapProvisioning.class)).zimbraLdapAuthenticate(
        account.toZimbra(com.zimbra.cs.account.Account.class),
        password,
        context
      );
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }
}
