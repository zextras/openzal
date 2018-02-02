package org.openzal.zal.ldap;

import com.unboundid.ldap.sdk.LDAPSearchException;
import org.jetbrains.annotations.NotNull;

import javax.net.SocketFactory;
import java.io.Closeable;

public class LDAPConnection implements Closeable, LDAPInterface
{
  @NotNull
  private final com.unboundid.ldap.sdk.LDAPConnection mLDAPConnection;

  public LDAPConnection(SocketFactory socketFactory, String host, int port, String bindDN, String bindPassword)
    throws LDAPException
  {
    try
    {
      mLDAPConnection = new com.unboundid.ldap.sdk.LDAPConnection(socketFactory, host, port, bindDN, bindPassword);
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mLDAPConnection);
  }

  @Override
  public void close()
  {
    mLDAPConnection.close();
  }

  public Schema getSchema()
    throws LDAPException
  {
    try
    {
      return new Schema(mLDAPConnection.getSchema());
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }
  }

  public SearchResult search(String baseDN, SearchScope sub, String s, String[] strings) throws LDAPException
  {
    try
    {
      return new SearchResult(mLDAPConnection.search(baseDN,sub.toZimbra(com.unboundid.ldap.sdk.SearchScope.class),s,strings));
    }
    catch (LDAPSearchException e)
    {
      throw new LDAPException(e);
    }
  }
}
