package org.openzal.zal.http;

public class MimeTypes
{
  public String getCharsetEncoding(String contentType)
  {
/* $if ZimbraVersion >= 8.5.0 $ */
    return org.eclipse.jetty.http.MimeTypes.inferCharsetFromContentType(contentType);
/* $else $
    org.eclipse.jetty.io.Buffer buffer = new org.eclipse.jetty.io.ByteArrayBuffer(contentType);
    return org.eclipse.jetty.http.MimeTypes.getCharsetFromContentType(buffer);
/* $endif $ */
  }
}
