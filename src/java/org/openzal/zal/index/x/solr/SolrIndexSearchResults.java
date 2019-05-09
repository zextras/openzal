

package org.openzal.zal.index.x.solr;

import org.openzal.zal.index.x.IndexSearchResult;
import org.openzal.zal.index.x.IndexSearchResults;

import javax.annotation.Nonnull;

public class SolrIndexSearchResults
  extends IndexSearchResults
{
  /* $if ZimbraX == 1 $
  private SolrRequestHelper mRequestHelper;
  /* $endif $ */

  public SolrIndexSearchResults(@Nonnull SolrRequestHelper requestHelper, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    super((com.zimbra.cs.index.ZimbraTopDocs) zObject);

    /* $else $ */
    super(null);

    /* $endif $ */
  }

  @Override
  public IndexSearchResult getResult(int index)
  {
    /* $if ZimbraX == 1 $
    {
      return new SolrIndexSearchResult(
        mRequestHelper,
        new SolrIndexDocument(toZimbra(com.zimbra.cs.index.ZimbraTopDocs.class).getDoc(index)),
        toZimbra(com.zimbra.cs.index.ZimbraTopDocs.class).getScoreDoc(index)
      );
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
