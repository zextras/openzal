package org.openzal.zal.lucene.index;


import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.openzal.zal.Folder;
import org.openzal.zal.Item;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.search.Query;

/* $if ZimbraX == 1 $
import com.zimbra.common.service.ServiceException;
import org.openzal.zal.lucene.search.BooleanClause;
import org.openzal.zal.lucene.search.BooleanQuery;
import org.openzal.zal.lucene.search.TermQuery;
/* $endif $ */

import javax.annotation.Nonnull;

public class Indexer
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final IndexStore                  mIndexStore;
  private final com.zimbra.cs.index.Indexer mIndexer;
  /* $endif $ */

  /* $if ZimbraX == 1 $
  private com.zimbra.cs.index.solr.SolrRequestHelper mRequestHelper;

  /* $elseif ZimbraVersion >= 8.5.0 $ */
  private Object mIndexWriterRef;
  private Method mIndexWriterRefGet;

  private IndexWriter mIndexWriter;
  /* $endif $ */


  public Indexer(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public Indexer(IndexStore indexStore, @Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0  $ */
    mIndexStore = indexStore;
    mIndexer = (com.zimbra.cs.index.Indexer) zObject;
    /* $endif $ */

    /* $if ZimbraX == 1 $
    try
    {
      Field field = com.zimbra.cs.index.solr.SolrIndex.class.getDeclaredField("solrHelper");
      field.setAccessible(true);

      mRequestHelper = (com.zimbra.cs.index.solr.SolrRequestHelper) field.get(mIndexStore.toZimbra(com.zimbra.cs.index.solr.SolrIndex.class));
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $elseif ZimbraVersion >= 8.5.0  $ */
    try
    {
      Field writer = zObject.getClass().getDeclaredField("writer");
      writer.setAccessible(true);

      mIndexWriterRef = writer.get(zObject);

      Class target = com.zimbra.cs.index.LuceneIndex.class.getClassLoader().loadClass("com.zimbra.cs.index.LuceneIndex$IndexWriterRef");
      mIndexWriterRefGet = target.getDeclaredMethod("get");
      mIndexWriterRefGet.setAccessible(true);

      mIndexWriter = getIndexWriter();
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $endif $ */
  }

  public void addDocument(Document document, Object... idParts)
    throws IOException, ZimbraException
  {
    /* $if ZimbraX == 1 $
    {
      String accountId = mIndexStore.getMailboxIndex().getMailbox().getAccountId();

      document.add("id", mRequestHelper.getSolrId(accountId, idParts));

      addDocument(document);
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    {
      StringJoiner joiner = new StringJoiner("_");

      for( Object idPart : idParts )
      {
        joiner.add(idPart.toString());
      }

      document.add("id", joiner.toString());
      addDocument(document);
    }
    /* $endif $ */
  }

  public void addDocument(List<Document> documentList)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    for( Document document : documentList )
    {
      addDocument(document);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void addDocument(Document document)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    try
    {
      org.apache.solr.client.solrj.request.UpdateRequest request = new org.apache.solr.client.solrj.request.UpdateRequest();

      request.add(document.toZimbra(com.zimbra.cs.index.IndexDocument.class).toInputDocument());

      mRequestHelper.executeUpdate(mIndexStore.getMailboxIndex().getMailbox().getAccountId(), request);
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    mIndexWriter.addDocument(document);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void addDocument(Folder folder, Item item, List<Document> documentList)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    List<com.zimbra.cs.index.IndexDocument> zimbraDocumentList = new ArrayList<>();

    for( Document document : documentList )
    {
      zimbraDocumentList.add(document.toZimbra(com.zimbra.cs.index.IndexDocument.class));
    }

    mIndexer.addDocument(
      folder == null ? null : folder.toZimbra(com.zimbra.cs.mailbox.Folder.class),
      item.toZimbra(com.zimbra.cs.mailbox.MailItem.class),
      zimbraDocumentList
    );
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteDocument(Object... idParts)
    throws IOException
  {
    /* $if ZimbraX == 1 $
    try
    {
      org.apache.solr.client.solrj.request.UpdateRequest request = new org.apache.solr.client.solrj.request.UpdateRequest();

      String accountId = mIndexStore.getMailboxIndex().getMailbox().getAccountId();
      String id = mRequestHelper.getSolrId(accountId, idParts);

      BooleanQuery.Builder disjunctionBuilder = new BooleanQuery.Builder();
      disjunctionBuilder.add(new TermQuery(new Term("id", id)), BooleanClause.Occur.SHOULD);

      BooleanQuery.Builder query;
      if( mRequestHelper.needsAccountFilter() )
      {
        query = new BooleanQuery.Builder();
        query.add(new TermQuery(new Term("acct_id", accountId)), BooleanClause.Occur.MUST);
        query.add(disjunctionBuilder.build(), BooleanClause.Occur.MUST);
      }
      else
      {
        query = disjunctionBuilder;
      }

      request.deleteByQuery(query.build().toString());

      mRequestHelper.executeUpdate(accountId, request);
    }
    catch( ServiceException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $elseif ZimbraVersion >= 8.5.0 $ */
    StringJoiner joiner = new StringJoiner("_");

    for( Object idPart : idParts )
    {
      joiner.add(idPart.toString());
    }

    deleteDocuments(new Term("id", joiner.toString()));
    /* $endif $ */
  }

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    for( Term term : terms )
    {
      mIndexWriter.deleteDocuments(term);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteDocuments(Query... queries)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.deleteDocuments(queries);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteAll()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.deleteAll();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteUnusuedFiles()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.deleteUnusuedFiles();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void compact()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexer.compact();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int maxDocs()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mIndexer.maxDocs();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  private IndexWriter getIndexWriter()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    try
    {
      return new IndexWriter(mIndexWriterRefGet.invoke(mIndexWriterRef));
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mIndexer.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return mIndexer.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mIndexer);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
