package org.openzal.zal.ldap;

import javax.net.SocketFactory;

public class LDAPConnectionFactory
{
  public LDAPConnection create(SocketFactory socketFactory, String host, int port) throws LDAPException
  {
    return new LDAPConnection(socketFactory, host, port);
  }

  public LDAPConnection create(SocketFactory socketFactory, String host, int port, String bindDN, String bindPassword) throws LDAPException
  {
    return new LDAPConnection(socketFactory, host, port, bindDN, bindPassword);
  }

}
