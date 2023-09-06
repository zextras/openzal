package org.openzal.zal.http;

public class MimeTypes
{
  public String getCharsetEncoding(String contentType)
  {
    return org.eclipse.jetty.http.MimeTypes.inferCharsetFromContentType(contentType);
  }
}
