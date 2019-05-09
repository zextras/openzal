

package org.openzal.zal.index.x.solr;

import org.openzal.zal.index.x.IndexDocument;
import org.openzal.zal.index.x.IndexSearchResult;

import javax.annotation.Nonnull;

public class SolrIndexSearchResult
  extends IndexSearchResult
{
  /* $if ZimbraX == 1 $ */
  private SolrRequestHelper mRequestHelper;
  /* $endif $ */

  public SolrIndexSearchResult(@Nonnull SolrRequestHelper requestHelper, IndexDocument indexDocument, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $ */
    super(indexDocument, (com.zimbra.cs.index.ZimbraScoreDoc) zObject);

    /* $else $
    super(indexDocument, null);

    /* $endif $ */
  }
}
