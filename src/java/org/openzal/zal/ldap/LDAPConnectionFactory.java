package org.openzal.zal.ldap;

import javax.net.SocketFactory;

public class LDAPConnectionFactory
{
  public LDAPConnection create(SocketFactory socketFactory, String host, int port) throws LDAPException
  {
    return new LDAPConnection(socketFactory, host, port);
  }
}
