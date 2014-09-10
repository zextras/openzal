package org.openzal.zal.soap;

import org.openzal.zal.Continuation;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ZimbraContextSimple implements ZimbraContext
{
  private final String  mTargetAccountId;
  private final String  mAuthenticatedAccountId;
  private final String  mRequesterIp;
  private final boolean mDelegatedAuth;
  private final Map<String, String> mParameters;

  public ZimbraContextSimple()
  {
    this("","","",false,new HashMap<String, String>());
  }

  public ZimbraContextSimple(
    String targetAccountId,
    String authenticatedAccountId,
    String requesterIp,
    boolean delegatedAuth,
    Map<String, String> parameters
  )
  {
    mTargetAccountId = targetAccountId;
    mAuthenticatedAccountId = authenticatedAccountId;
    mRequesterIp = requesterIp;
    mDelegatedAuth = delegatedAuth;
    mParameters = parameters;
  }

  @Override
  public String getTargetAccountId()
  {
    return mTargetAccountId;
  }

  @Override
  public String getAuthenticatedAccontId()
  {
    return mAuthenticatedAccountId;
  }

  @Override
  public String getRequesterIp()
  {
    return mRequesterIp;
  }

  @Override
  public SoapResponse execLocalRequest()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpServletRequest getHttpServletRequest()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Continuation getContinuation()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDelegatedAuth()
  {
    return mDelegatedAuth;
  }

  @Override
  public SoapNode getSubNode(String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, String> getParameterMap()
  {
    return mParameters;
  }

  @Override
  public String getParameter(String key, String def)
  {
    String value = mParameters.get(key);
    return value == null ? def : value;
  }

  @Override
  public String getNodeName()
  {
    return "";
  }

  public ZimbraContextSimple set(String key, String value)
  {
    mParameters.put(key, value);
    return this;
  }

  @Override
  public String getText()
  {
    return "";
  }
}
