package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class Analyzer implements Closeable
{
  private org.apache.lucene.analysis.Analyzer mAnalyzer;

  public Analyzer(@Nonnull Object zObject)
  {
    mAnalyzer = (org.apache.lucene.analysis.Analyzer) zObject;
  }

  public TokenStream tokenStream(String fieldName, Reader reader)
  {
    return new TokenStream(mAnalyzer.tokenStream(fieldName, reader));
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
    mAnalyzer.close();
  }

  @Override
  public String toString()
  {
    return mAnalyzer.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mAnalyzer);
  }
}
