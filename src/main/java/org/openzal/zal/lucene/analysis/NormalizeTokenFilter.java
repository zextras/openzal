package org.openzal.zal.lucene.analysis;

import java.io.IOException;

public final class NormalizeTokenFilter extends CharFilter
{

  public NormalizeTokenFilter(CharStream in)
  {
    super(in);
  }


  public NormalizeTokenFilter(Object zObject)
  {
    super((com.zimbra.cs.index.analysis.NormalizeTokenFilter)zObject);
  }


  public int read()
    throws IOException
  {
    return toZimbra(com.zimbra.cs.index.analysis.NormalizeTokenFilter.class).read();
  }

  public int read(char[] buf, int offset, int len)
    throws IOException
  {
    return toZimbra(com.zimbra.cs.index.analysis.NormalizeTokenFilter.class).read(buf, offset, len);
  }

  public static int normalize(int c)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(c);
  }

  public static int normalize(int c, int p)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(c, p);
  }

  public static String normalize(String value)
  {
    return com.zimbra.cs.index.analysis.NormalizeTokenFilter.normalize(value);
  }
}
