package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class Analyzer implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.analysis.Analyzer mZObject;
  /* $endif $ */

  public Analyzer(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.analysis.Analyzer) zObject;
    /* $endif $ */
  }

  public TokenStream tokenStream(String fieldName, Reader reader)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new TokenStream(mZObject.tokenStream(fieldName, reader));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close() {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString() {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(
    @NotNull Class<T> target
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
