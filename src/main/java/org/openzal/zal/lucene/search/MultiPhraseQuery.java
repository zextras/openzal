package org.openzal.zal.lucene.search;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.index.Term;

import java.util.List;

public class MultiPhraseQuery
  extends Query
{

  public MultiPhraseQuery(Term... terms)
  {
    this(new org.apache.lucene.search.MultiPhraseQuery());

    if( terms.length > 0 )
    {
      add(terms);
    }
  }

  public MultiPhraseQuery(@Nonnull Object zObject)
  {
    super((org.apache.lucene.search.MultiPhraseQuery) zObject);
  }

  public void add(@Nonnull Term term)
  {
    toZimbra(org.apache.lucene.search.MultiPhraseQuery.class).add(term.toZimbra(org.apache.lucene.index.Term.class));
  }

  public void add(@Nonnull List<Term> terms)
  {
    add(terms.toArray(new Term[0]));
  }

  public void add(@Nonnull Term[] terms)
  {
    org.apache.lucene.index.Term[] zimbraArray = new org.apache.lucene.index.Term[terms.length];

    for( int i = 0; i < zimbraArray.length; i++ )
    {
      zimbraArray[i] = terms[i].toZimbra(org.apache.lucene.index.Term.class);
    }

    toZimbra(org.apache.lucene.search.MultiPhraseQuery.class).add(zimbraArray);
  }

}
