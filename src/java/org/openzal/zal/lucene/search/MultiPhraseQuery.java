package org.openzal.zal.lucene.search;

import com.sun.istack.NotNull;
import org.openzal.zal.lucene.index.Term;

import java.util.List;

public class MultiPhraseQuery
  extends Query
{

  public MultiPhraseQuery(Term... terms)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    super(new org.apache.lucene.search.MultiPhraseQuery());

    if( terms.length > 0 )
    {
      add(terms);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  protected org.apache.lucene.search.MultiPhraseQuery getZimbra()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return (org.apache.lucene.search.MultiPhraseQuery) super.getZimbra();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    getZimbra().add(term.toZimbra(org.apache.lucene.index.Term.class));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull List<Term> terms)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    add(terms.toArray(new Term[0]));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull Term[] terms)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    org.apache.lucene.index.Term[] zimbraArray = new org.apache.lucene.index.Term[terms.length];

    for( int i = 0; i < zimbraArray.length; i++ )
    {
      zimbraArray[i] = terms[i].toZimbra(org.apache.lucene.index.Term.class);
    }

    getZimbra().add(zimbraArray);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

}
