package org.openzal.zal.lucene.analysis;

import javax.annotation.Nonnull;

import java.io.FilterReader;

public class CharStream
  extends FilterReader
{
  public CharStream(@Nonnull Object zObject)
  {
    super((org.apache.lucene.analysis.CharStream) zObject);
  }

  public int correctOffset(int currentOff)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    return in.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(in);
  }
}
