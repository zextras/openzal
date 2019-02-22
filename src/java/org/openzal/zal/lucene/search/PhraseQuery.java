package org.openzal.zal.lucene.search;

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.lucene.index.Term;

import java.util.List;

public class PhraseQuery
  extends Query
{

  public PhraseQuery(Term... terms)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    this(new org.apache.lucene.search.PhraseQuery());

    if( terms.length > 0 )
    {
      add(terms);
    }
    /* $else $
    this((Object) null);
    /* $endif $ */
  }

  public PhraseQuery(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    super((org.apache.lucene.search.PhraseQuery) zObject);
    /* $else $
    super(null);
    /* $endif $ */
  }

  public void add(@NotNull Term term)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    toZimbra(org.apache.lucene.search.PhraseQuery.class).add(term.toZimbra(org.apache.lucene.index.Term.class));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull Term term, int position)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    toZimbra(org.apache.lucene.search.PhraseQuery.class).add(term.toZimbra(org.apache.lucene.index.Term.class), position);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull List<Term> terms)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    add(terms.toArray(new Term[0]));
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void add(@NotNull Term[] terms)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    for(int i = 0; i < terms.length; i++)
    {
      add(terms[i], i);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

}
