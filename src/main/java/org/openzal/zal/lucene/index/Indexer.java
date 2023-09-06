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

import javax.annotation.Nonnull;

public class Indexer
  implements Closeable
{
  private final IndexStore                  mIndexStore;
  private final com.zimbra.cs.index.Indexer mIndexer;

  private Object mIndexWriterRef;
  private Method mIndexWriterRefGet;

  private IndexWriter mIndexWriter;

  public Indexer(@Nonnull Object zObject)
  {
    this(null, zObject);
  }

  public Indexer(IndexStore indexStore, @Nonnull Object zObject)
  {
    mIndexStore = indexStore;
    mIndexer = (com.zimbra.cs.index.Indexer) zObject;

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
  }

  public void addDocument(Document document, Object... idParts)
    throws IOException, ZimbraException
  {
    {
      StringJoiner joiner = new StringJoiner("_");

      for( Object idPart : idParts )
      {
        joiner.add(idPart.toString());
      }

      document.add("id", joiner.toString());
      addDocument(document);
    }
  }

  public void addDocument(List<Document> documentList)
    throws IOException
  {
    for( Document document : documentList )
    {
      addDocument(document);
    }
  }

  public void addDocument(Document document)
    throws IOException
  {
    mIndexWriter.addDocument(document);
  }

  public void addDocument(Folder folder, Item item, List<Document> documentList)
    throws IOException
  {
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
  }

  public void deleteDocument(Object... idParts)
    throws IOException
  {
    StringJoiner joiner = new StringJoiner("_");

    for( Object idPart : idParts )
    {
      joiner.add(idPart.toString());
    }

    deleteDocuments(new Term("id", joiner.toString()));
  }

  public void deleteDocuments(Term... terms)
    throws IOException
  {
    for( Term term : terms )
    {
      mIndexWriter.deleteDocuments(term);
    }
  }

  public void deleteDocuments(Query... queries)
    throws IOException
  {
    mIndexWriter.deleteDocuments(queries);
  }

  public void deleteAll()
    throws IOException
  {
    mIndexWriter.deleteAll();
  }

  public void deleteUnusuedFiles()
    throws IOException
  {
    mIndexWriter.deleteUnusuedFiles();
  }

  public void compact()
  {
    mIndexer.compact();
  }

  public int maxDocs()
  {
    return mIndexer.maxDocs();
  }

  private IndexWriter getIndexWriter()
  {
    try
    {
      return new IndexWriter(mIndexWriterRefGet.invoke(mIndexWriterRef));
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
    mIndexer.close();
  }

  @Override
  public String toString()
  {
    return mIndexer.toString();
  }

  public <T> T toZimbra(@Nonnull Class<T> target)
  {
    return target.cast(mIndexer);
  }
}
