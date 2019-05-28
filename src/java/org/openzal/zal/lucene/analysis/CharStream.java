package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

import java.io.FilterReader;

public class CharStream
  extends FilterReader
{
  public CharStream(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    super((org.apache.lucene.analysis.CharStream) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public int correctOffset(int currentOff)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return in.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return target.cast(in);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
