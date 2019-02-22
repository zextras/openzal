package org.openzal.zal.lucene.index;

/* $if ZimbraX == 0 $ */
import com.zimbra.cs.index.LuceneIndex;
/* $endif */
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
  /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
  private final com.zimbra.cs.index.Indexer mZObject;
  private Object rIndexWriterRef;
  private Method rmIndexWriterRefGet;
  private IndexWriter mIndexWriter;
  /* $endif $ */


  public Indexer(@NotNull Object zObject)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
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

        mIndexWriter = getIndexWriter();
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

    mZObject.addDocument(
      folder == null ? null : folder.toZimbra(com.zimbra.cs.mailbox.Folder.class),
      item.toZimbra(com.zimbra.cs.mailbox.MailItem.class),
      zimbraDocumentList
    );
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void addDocument(Document document)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.addDocument(document);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void addDocument(List<Document> documentList)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.addDocument(documentList);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mIndexWriter.deleteDocuments(terms);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public void deleteDocuments(Query...queries)
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
    mZObject.compact();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public int maxDocs()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.maxDocs();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  private IndexWriter getIndexWriter()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    try
    {
      return new IndexWriter(rmIndexWriterRefGet.invoke(rIndexWriterRef));
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
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    mZObject.close();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  @Override
  public String toString()
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return mZObject.toString();
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }

  public <T> T toZimbra(@NotNull Class<T> target)
  {
    /* $if ZimbraVersion >= 8.5.0 && ZimbraX == 0 $ */
    return target.cast(mZObject);
    /* $else $
    throw new UnsupportedOperationException();
    /* $endif $ */
  }
}
