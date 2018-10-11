package org.openzal.zal.lucene.analysis;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class Analyzer
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private org.apache.lucene.analysis.Analyzer mZObject;
  /* $endif $ */

  public Analyzer(Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.analysis.Analyzer) zObject;
    /* $endif $ */
  }


  public TokenStream tokenStream(String text)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return tokenStream(null, text);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public TokenStream tokenStream(Reader reader)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return tokenStream(null, reader);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public TokenStream tokenStream(String field, Reader reader)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new TokenStream(mZObject.tokenStream(field, reader));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public TokenStream tokenStream(String field, String text)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new TokenStream(mZObject.tokenStream(field, new StringReader(text)));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public Tokenizer reusableTokenStream(String field, Reader reader)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return new Tokenizer(mZObject.reusableTokenStream(field, reader));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  protected org.apache.lucene.analysis.Analyzer getZimbra()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mZObject;
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
