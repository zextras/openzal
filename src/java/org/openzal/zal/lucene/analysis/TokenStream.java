package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;

public class TokenStream
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.analysis.TokenStream mZObject;
  /* $endif $ */

  public TokenStream(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.analysis.TokenStream) zObject;
    /* $endif $ */
  }

  public boolean incrementToken()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.incrementToken();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void end()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.end();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void reset()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.end();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
