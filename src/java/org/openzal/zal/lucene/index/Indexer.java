package org.openzal.zal.lucene.index;

import com.zimbra.cs.index.LuceneIndex;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Folder;
import org.openzal.zal.Item;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.lucene.document.Document;
import org.openzal.zal.lucene.search.Query;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Indexer
  implements Closeable
{
  private final com.zimbra.cs.index.Indexer mZObject;

  private Object rIndexWriterRef;
  private Method rmIndexWriterRefGet;

  public Indexer(@NotNull Object zObject)
  {
    if( zObject.getClass().getCanonicalName().equals("com.zimbra.cs.index.LuceneIndex.LuceneIndexerImpl") )
    {
      mZObject = (com.zimbra.cs.index.Indexer) zObject;

      try
      {
        Class target = LuceneIndex.class.getClassLoader().loadClass("com.zimbra.cs.index.LuceneIndex$IndexWriterRef");
        Field writer = zObject.getClass().getDeclaredField("writer");
        writer.setAccessible(true);

        rIndexWriterRef = writer.get(zObject);
        rmIndexWriterRefGet = target.getDeclaredMethod("get");
        rmIndexWriterRefGet.setAccessible(true);


      }
      catch( Exception e )
      {
        throw ExceptionWrapper.wrap(e);
      }
    }
    else
    {
      throw new UnsupportedOperationException(String.format(
        "%s wrap not supported!",
        zObject.getClass().getCanonicalName()
      ));
    }
  }

  public void addDocument(Folder folder, Item item, List<Document> documentList)
    throws IOException
  {
    List<com.zimbra.cs.index.IndexDocument> zimbraDocumentList = new ArrayList<>();

    for( Document document : documentList )
    {
      zimbraDocumentList.add(document.toZimbra(com.zimbra.cs.index.IndexDocument.class));
    }

    mZObject.addDocument(
      folder == null ? null : folder.toZimbra(com.zimbra.cs.mailbox.Folder.class),
      item.toZimbra(com.zimbra.cs.mailbox.MailItem.class),
      zimbraDocumentList
    );
  }

  public void addDocument(Document document)
    throws IOException
  {
    getIndexWriter().addDocument(document);
  }

  public void addDocument(List<Document> documentList)
    throws IOException
  {
    getIndexWriter().addDocument(documentList);
  }

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    getIndexWriter().deleteDocuments(terms);
  }

  public void deleteDocuments(Query...queries)
    throws IOException
  {
    getIndexWriter().deleteDocuments(queries);
  }

  public void deleteAll()
    throws IOException
  {
    getIndexWriter().deleteAll();
  }

  public void deleteUnusuedFiles()
    throws IOException
  {
    getIndexWriter().deleteUnusuedFiles();
  }

  public void compact()
  {
    mZObject.compact();
  }

  public int maxDocs()
  {
    return mZObject.maxDocs();
  }

  private IndexWriter getIndexWriter()
  {
    try
    {
      return new IndexWriter(rmIndexWriterRefGet.invoke(rIndexWriterRef));
    }
    catch( Exception e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Override
  public void close()
    throws IOException
  {
    mZObject.close();
  }

  @Override
  public String toString()
  {
    return mZObject.toString();
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
