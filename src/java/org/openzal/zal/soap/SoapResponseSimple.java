package org.openzal.zal.soap;

import org.junit.Ignore;

import java.util.HashMap;

public class SoapResponseSimple implements SoapResponse
{
  private final HashMap<String, Object> mMap;

  public SoapResponseSimple()
  {
    mMap = new HashMap<String, Object>();
  }

  @Override
  public void setValue(String key, String value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setValue(String key, boolean value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setValue(String key, long value)
  {
    mMap.put(key, value);
  }

  @Override
  public void setQName(QName qName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setResponse(SoapResponse soapResponse)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SoapResponse createNode(String name)
  {
    throw new UnsupportedOperationException();
  }

  public Object getAttribute(String responses)
  {
    return mMap.get(responses);
  }
}
