package org.openzal.zal.ldap;

import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.ssl.SSLUtil;
import com.zimbra.common.net.TrustManagers;

import javax.annotation.Nonnull;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.Closeable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class LDAPConnection implements Closeable, LDAPInterface
{
  @Nonnull
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

  public LDAPConnection(SocketFactory socketFactory, String host, int port)
    throws LDAPException
  {
    try
    {
      mLDAPConnection = new com.unboundid.ldap.sdk.LDAPConnection(socketFactory, host, port);
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }
  }

  public LDAPConnection(Object connection)
  {
    mLDAPConnection = (com.unboundid.ldap.sdk.LDAPConnection)connection;
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
      com.unboundid.ldap.sdk.schema.Schema schema = mLDAPConnection.getSchema();
      if (schema != null)
      {
        return new Schema(schema);
      }
      return null;
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }
  }

  public ZalSearchResult search(String baseDN, SearchScope sub, String s, String[] strings) throws LDAPException
  {
    try
    {
      return new ZalSearchResult(mLDAPConnection.search(baseDN, sub.toZimbra(com.unboundid.ldap.sdk.SearchScope.class), s, strings));
    }
    catch (LDAPSearchException e)
    {
      throw new LDAPException(e);
    }
  }

  public ZalSearchResult searchAddSortControl(String baseDN, SearchScope sub, String filter, String[] attributes) throws LDAPException
  {
    try
    {
      SearchRequest searchRequest = new SearchRequest(
        baseDN,
        sub.toZimbra(com.unboundid.ldap.sdk.SearchScope.class),
        filter,
        attributes
      );
      Control sss = new ServerSideSortRequestControl(
        new SortKey("cn", CaseIgnoreStringMatchingRule.ORDERING_RULE_NAME, false)
      );
      searchRequest.setControls(sss);
      return new ZalSearchResult(mLDAPConnection.search(searchRequest));
    }
    catch( com.unboundid.ldap.sdk.LDAPException e )
    {
      throw new LDAPException(e);
    }
  }

  private static TrustManager getTrustManager(boolean allowUntrustedCerts) {
    if (allowUntrustedCerts) {
      return TrustManagers.dummyTrustManager();
    } else {
      return TrustManagers.customTrustManager();
    }
  }

  static SSLContext createSSLContext(boolean allowUntrustedCerts) throws GeneralSecurityException
  {
    TrustManager tm = getTrustManager(allowUntrustedCerts);
    SSLUtil sslUtil = new SSLUtil(tm);

    return sslUtil.createSSLContext();
  }

  public void startTLS(boolean sslAllowUntrustedCerts) throws LDAPException
  {
    SSLContext startTLSContext = null;
    ExtendedResult extendedResult = null;
    try
    {
      startTLSContext = createSSLContext(sslAllowUntrustedCerts);
      extendedResult = mLDAPConnection.processExtendedOperation(new StartTLSExtendedRequest(startTLSContext));
    }
    catch (GeneralSecurityException e)
    {
      throw new LDAPException(e);
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }

    if (extendedResult.getResultCode() != com.unboundid.ldap.sdk.ResultCode.SUCCESS)
    {
      throw new LDAPException(new ResultCode(extendedResult.getResultCode()),"unable to send or receive startTLS extended operation");
    }
  }

  public void bind(String baseDN, String bindPassword) throws LDAPException
  {
    try
    {
      mLDAPConnection.bind(baseDN, bindPassword);
    }
    catch (com.unboundid.ldap.sdk.LDAPException e)
    {
      throw new LDAPException(e);
    }
  }
}
