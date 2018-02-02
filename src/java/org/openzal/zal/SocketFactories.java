package org.openzal.zal;

import com.zimbra.cs.ldap.unboundid.UnixDomainSocketFactory;
import org.jetbrains.annotations.NotNull;

import javax.net.SocketFactory;

public class SocketFactories
{
  @NotNull
  private final com.zimbra.common.net.SocketFactories mSocketFactories;

  public SocketFactories(@NotNull Object socketFactories)
  {
    mSocketFactories = (com.zimbra.common.net.SocketFactories) socketFactories;
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mSocketFactories);
  }

  public static SocketFactory dummySSLSocketFactory()
  {
    return com.zimbra.common.net.SocketFactories.dummySSLSocketFactory();
  }

  public static SocketFactory defaultSSLSocketFactory()
  {
    return com.zimbra.common.net.SocketFactories.defaultSSLSocketFactory();
  }

  public static SocketFactory unixDomainSocketFactory()
  {
    return new UnixDomainSocketFactory();
  }
}