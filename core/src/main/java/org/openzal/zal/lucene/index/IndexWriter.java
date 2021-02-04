package org.openzal.zal.lucene.index;

import javax.annotation.Nonnull;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.search.Query;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class IndexWriter
  implements Closeable
{
  /* $if ZimbraVersion >= 8.5.0 $ */
  private final org.apache.lucene.index.IndexWriter mZObject;
  /* $endif $ */

  public IndexWriter(@Nonnull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject = (org.apache.lucene.index.IndexWriter) zObject;
    /* $endif $ */
  }

  public void addDocument(Document document)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject.addDocument(document.toZimbra(com.zimbra.cs.index.IndexDocument.class).toDocument());
    /* $else $
    throw new UnsupportedOperationException();
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

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    for( Term term : terms )
    {
      mZObject.deleteDocuments(term.toZimbra(org.apache.lucene.index.Term.class));
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteDocuments(Query... queries)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    for( Query query : queries )
    {
      mZObject.deleteDocuments(query.toZimbra(org.apache.lucene.search.Query.class));
    }
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteAll()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.deleteAll();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteUnusuedFiles()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.deleteUnusedFiles();
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

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public void close()
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
