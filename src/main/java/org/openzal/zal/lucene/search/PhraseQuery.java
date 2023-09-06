package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

import java.util.List;

public class PhraseQuery
  extends Query
{

  public PhraseQuery(Term... terms)
  {
    this(new org.apache.lucene.search.PhraseQuery());

    if( terms.length > 0 )
    {
      add(terms);
    }
  }

  public PhraseQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.PhraseQuery) zObject);
  }

  public void add(@Nonnull Term term)
  {
    toZimbra(org.apache.lucene.search.PhraseQuery.class).add(term.toZimbra(org.apache.lucene.index.Term.class));
  }

  public void add(@Nonnull Term term, int position)
  {
    toZimbra(org.apache.lucene.search.PhraseQuery.class).add(term.toZimbra(org.apache.lucene.index.Term.class), position);
  }

  public void add(@Nonnull List<Term> terms)
  {
    add(terms.toArray(new Term[0]));
  }

  public void add(@Nonnull Term[] terms)
  {
    for(int i = 0; i < terms.length; i++)
    {
      add(terms[i], i);
    }
  }

}
