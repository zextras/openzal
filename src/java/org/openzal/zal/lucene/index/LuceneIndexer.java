package org.openzal.zal.lucene.index;

import com.zimbra.cs.index.LuceneIndex;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.jetbrains.annotations.NotNull;
import org.openzal.zal.Folder;
import org.openzal.zal.Item;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.lib.ZalWrapper;
import org.openzal.zal.lucene.document.LuceneDocument;
import org.openzal.zal.lucene.search.LuceneQuery;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndexer
  implements ZalWrapper<com.zimbra.cs.index.Indexer>,
             Closeable
{
  private final com.zimbra.cs.index.Indexer mZObject;

  private Object rIndexWriterRef;
  private Method rmIndexWriterRefGet;

  public LuceneIndexer(@NotNull Object zObject)
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

  public void addDocument(Folder folder, Item item, List<LuceneDocument> documentList)
    throws IOException
  {
    List<com.zimbra.cs.index.IndexDocument> zimbraDocumentList = new ArrayList<>();

    for( LuceneDocument document : documentList )
    {
      zimbraDocumentList.add(document.toZimbra());
    }

    mZObject.addDocument(
      folder == null ? null : folder.toZimbra(com.zimbra.cs.mailbox.Folder.class),
      item.toZimbra(com.zimbra.cs.mailbox.MailItem.class),
      zimbraDocumentList
    );
  }

  public void addDocument(LuceneDocument document)
    throws IOException
  {
    getWriter().addDocument(document.toZimbra().toDocument());
  }

  public void addDocument(List<LuceneDocument> documents)
    throws IOException
  {
    for( LuceneDocument document : documents )
    {
      addDocument(document);
    }
  }

  public void deleteDocuments(LuceneTerm term)
    throws IOException
  {
    getWriter().deleteDocuments(term.toZimbra());
  }

  public void deleteDocuments(LuceneTerm... terms)
    throws IOException
  {
    Term[] zimbraArray = new Term[terms.length];

    for( int i = 0; i < zimbraArray.length; i++ )
    {
      zimbraArray[i] = terms[i].toZimbra();
    }

    getWriter().deleteDocuments(zimbraArray);
  }

  public void deleteDocuments(LuceneQuery query)
    throws IOException
  {
    getWriter().deleteDocuments(query.toZimbra());
  }

  public void deleteDocuments(LuceneQuery... queries)
    throws IOException
  {
    Query[] zimbraArray = new Query[queries.length];

    for( int i = 0; i < zimbraArray.length; i++ )
    {
      zimbraArray[i] = queries[i].toZimbra();
    }

    getWriter().deleteDocuments(zimbraArray);
  }

  public void deleteAll()
    throws IOException
  {
    getWriter().deleteAll();
  }

  public void compact()
  {
    mZObject.compact();
  }

  public int maxDocs()
  {
    return mZObject.maxDocs();
  }

  private IndexWriter getWriter()
  {
    try
    {
      return (IndexWriter) rmIndexWriterRefGet.invoke(rIndexWriterRef);
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

  @Override
  public com.zimbra.cs.index.Indexer toZimbra()
  {
    return mZObject;
  }

  @Override
  public <T> T toZimbra(@NotNull Class<T> target)
  {
    return target.cast(mZObject);
  }
}
