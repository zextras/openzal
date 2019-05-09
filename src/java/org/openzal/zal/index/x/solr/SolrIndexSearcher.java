

package org.openzal.zal.index.x.solr;

import com.zimbra.common.service.ServiceException;
import java.io.IOException;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.index.x.IndexSearchResults;
import org.openzal.zal.index.x.IndexSearcher;
import org.openzal.zal.lucene.search.Query;

import javax.annotation.Nonnull;

public class SolrIndexSearcher
  extends IndexSearcher
{
  /* $if ZimbraX == 1 $
  private SolrRequestHelper mRequestHelper;
  /* $endif $ */

  public SolrIndexSearcher(@Nonnull String accountId, @Nonnull SolrRequestHelper requestHelper, @Nonnull Object zObject)
  {
    /* $if ZimbraX == 1 $
    super(accountId, (com.zimbra.cs.index.ZimbraIndexSearcher) zObject);

    mRequestHelper = requestHelper;

    /* $else $ */
    super(accountId, null);

    /* $endif $ */
  }

  @Override
  public IndexSearchResults search(Query query, int maxResults, String fieldId, String... fetchFields)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    {
      try
      {
        return new SolrIndexSearchResults(
          mRequestHelper,
          toZimbra(com.zimbra.cs.index.ZimbraIndexSearcher.class).search(
            query.toZimbra(org.apache.lucene.search.Query.class), null, maxResults, null, fieldId, fetchFields
          )
        );
      }
      catch( ServiceException e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    /* $else $ */
    {
      throw new UnsupportedOperationException();
    }
    /* $endif $ */
  }
}
