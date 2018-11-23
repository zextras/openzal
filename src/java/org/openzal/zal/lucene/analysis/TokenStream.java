package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

public class TokenStream extends AttributeSource implements Closeable
{
  public TokenStream(
    @NotNull
      Object zObject
  )
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super((org.apache.lucene.analysis.TokenStream) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public boolean incrementToken()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return toZimbra(org.apache.lucene.analysis.TokenStream.class).incrementToken();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    toZimbra(org.apache.lucene.analysis.TokenStream.class).close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void end()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    toZimbra(org.apache.lucene.analysis.TokenStream.class).end();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void reset()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    toZimbra(org.apache.lucene.analysis.TokenStream.class).end();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
