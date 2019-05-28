package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class Analyzer implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private org.apache.lucene.analysis.Analyzer mAnalyzer;
  /* $endif $ */

  public Analyzer(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mAnalyzer = (org.apache.lucene.analysis.Analyzer) zObject;
    /* $endif $ */
  }

  public TokenStream tokenStream(String fieldName, Reader reader)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new TokenStream(mAnalyzer.tokenStream(fieldName, reader));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Deprecated
  public TokenStream reusableTokenStream(String fieldName, Reader reader)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public int getPositionIncrementGap(String fieldName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mAnalyzer.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mAnalyzer.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mAnalyzer);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
