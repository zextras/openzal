package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

import java.io.FilterReader;

public class CharStream
  extends FilterReader
{
  public CharStream(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $
    super((org.apache.lucene.analysis.CharStream) zObject);
    /* $else $ */
    super(null);
    /* $endif $ */
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

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return target.cast(in);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
