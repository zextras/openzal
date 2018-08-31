package org.openzal.zal.lucene.search;

import com.sun.istack.NotNull;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiPhraseQuery;
import org.openzal.zal.lucene.index.LuceneTerm;

import java.util.List;

public class LuceneMultiPhraseQuery
  extends LuceneQuery
{

  public LuceneMultiPhraseQuery(LuceneTerm... terms)
  {
    super(new MultiPhraseQuery());

    if(terms.length > 0)
    {
      add(terms);
    }
  }

  public void add(@NotNull LuceneTerm term)
  {
    toZimbra().add(term.toZimbra());
  }

  public void add(@NotNull List<LuceneTerm> terms)
  {
    add(terms.toArray(new LuceneTerm[0]));
  }

  public void add(@NotNull LuceneTerm[] terms)
  {
    Term[] zimbraArray = new Term[terms.length];

    for(int i = 0; i < zimbraArray.length; i++)
    {
      zimbraArray[i] = terms[i].toZimbra();
    }

    toZimbra().add(zimbraArray);
  }

  @Override
  public org.apache.lucene.search.MultiPhraseQuery toZimbra()
  {
    return (MultiPhraseQuery) super.toZimbra();
  }
}
