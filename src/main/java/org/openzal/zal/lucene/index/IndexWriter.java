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
  private final org.apache.lucene.index.IndexWriter mZObject;

  public IndexWriter(@Nonnull Object zObject)
  {
    mZObject = (org.apache.lucene.index.IndexWriter) zObject;
  }

  public void addDocument(Document document)
    throws IOException
  {
    mZObject.addDocument(document.toZimbra(com.zimbra.cs.index.IndexDocument.class).toDocument());
  }

  public void addDocument(List<Document> documentList)
    throws IOException
  {
    for( Document document : documentList )
    {
      addDocument(document);
    }
  }

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    for( Term term : terms )
    {
      mZObject.deleteDocuments(term.toZimbra(org.apache.lucene.index.Term.class));
    }
  }

  public void deleteDocuments(Query... queries)
    throws IOException
  {
    for( Query query : queries )
    {
      mZObject.deleteDocuments(query.toZimbra(org.apache.lucene.search.Query.class));
    }
  }

  public void deleteAll()
    throws IOException
  {
    mZObject.deleteAll();
  }

  public void deleteUnusuedFiles()
    throws IOException
  {
    mZObject.deleteUnusedFiles();
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mZObject);
  }

  @Override
  public void close()
    throws IOException
  {
    mZObject.close();
  }
}
