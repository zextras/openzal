package org.openzal.zal.lucene.analysis;

import com.sun.istack.NotNull;

import java.io.FilterReader;

public class CharStream
  extends FilterReader
{
  public CharStream(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.analysis.CharStream) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  protected org.apache.lucene.analysis.CharStream getZimbra()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return (org.apache.lucene.analysis.CharStream) in;
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int correctOffset(int currentOff)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return getZimbra().correctOffset(currentOff);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return getZimbra().toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@org.jetbrains.annotations.NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(getZimbra());
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
