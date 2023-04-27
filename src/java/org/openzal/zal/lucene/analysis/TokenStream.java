package org.openzal.zal.lucene.analysis;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

public class TokenStream extends AttributeSource implements Closeable
{
  public TokenStream(@Nonnull Object zObject)
  {
    super((org.apache.lucene.analysis.TokenStream) zObject);
  }

  public boolean incrementToken()
    throws IOException
  {
    return toZimbra(org.apache.lucene.analysis.TokenStream.class).incrementToken();
  }

  @Override
  public void close()
    throws IOException
  {
    toZimbra(org.apache.lucene.analysis.TokenStream.class).close();
  }

  public void end()
    throws IOException
  {
    toZimbra(org.apache.lucene.analysis.TokenStream.class).end();
  }

  public void reset()
    throws IOException
  {
    toZimbra(org.apache.lucene.analysis.TokenStream.class).reset();
  }
}
