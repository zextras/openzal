package org.openzal.zal;

import com.zimbra.cs.ldap.unboundid.UnixDomainSocketFactory;

import javax.net.SocketFactory;

public class SocketFactories
{
  public SocketFactory dummySSLSocketFactory()
  {
    return com.zimbra.common.net.SocketFactories.dummySSLSocketFactory();
  }

  public SocketFactory defaultSSLSocketFactory()
  {
    return com.zimbra.common.net.SocketFactories.defaultSSLSocketFactory();
  }

  public SocketFactory unixDomainSocketFactory()
  {
    return new UnixDomainSocketFactory();
  }
}