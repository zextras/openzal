package org.openzal.zal.lucene;

import com.zimbra.cs.index.LuceneIndex;
import com.zimbra.cs.index.ZimbraIndexSearcher;
import com.zimbra.cs.index.ZimbraTopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.openzal.zal.Mailbox;
import org.openzal.zal.Metadata;

public class LuceneDemo
{

  public int index(Mailbox mailbox, Metadata metadata)
  {
    LuceneIndex.Factory luceneFactory = new LuceneIndex.Factory();
    try
    {
      LuceneIndex index = luceneFactory.getIndexStore(mailbox.getMailbox());
      System.out.printf(
        "\n-----\n[START] %s index for %s\n",
        getClass().getSimpleName(),
        mailbox.getAccount().getName()
      );

      // Metadata metadata = new Metadata();
      // metadata.put(DriveItem.FN_TAGS, tags);
      // metadata.put("uuid", uuid);

      // Metadata data = new Metadata();
      // data.put("nm", fileName);
      // data.put("idx", MailItem.IndexStatus.STALE.id());
      // data.put("meta", BEncoding.encode(metadata.asMap()));
      // "d2:cr16:dev1@example.com2:ct39:application/x-zimbra-doc; charset=utf-83:dee4:true1:f6:HELLO!2:lti0e5:mdveri2e2:ua44:ZimbraWebClient - FF61 (Linux)/8.8.8_GA_20091:vi10ee"

      // DriveItem item = new DriveItem(mailbox, metadata);
      //
      // try( Indexer indexer = index.openIndexer() )
      // {
      //   List<IndexDocument> documentList = item.generateIndexData();
      //
      //   System.out.printf("Indexing documents: %s\n", documentList.size());
      //   for(int i = 0; i < documentList.size(); i++)
      //   {
      //     System.out.printf("[%s] %s\n", i, documentList.get(i).toDocument());
      //   }
      //
      //   indexer.addDocument(null, item, documentList);
      // }

      System.out.printf("[STOP] %s index for %s\n-----\n", getClass().getSimpleName(), mailbox.getAccount().getName());
    }
    catch( Exception e )
    {
      e.printStackTrace();
      return 1;
    }

    return 0;
  }

  public int search(Mailbox mailbox, String query)
  {
    LuceneIndex.Factory luceneFactory = new LuceneIndex.Factory();
    try
    {
      LuceneIndex index = luceneFactory.getIndexStore(mailbox.getMailbox());
      System.out.printf(
        "\n-----\n[START] %s search for %s\n",
        getClass().getSimpleName(),
        mailbox.getAccount().getName()
      );

      try( ZimbraIndexSearcher searcher = index.openSearcher() )
      {
        System.out.printf(
          "Document number: c:%s d:%s\n",
          searcher.getIndexReader().numDocs(),
          searcher.getIndexReader().numDeletedDocs()
        );

        System.out.printf("Searching content: %s\n", query);

        // ZimbraIndexReader.TermFieldEnumeration enumeration = searcher.getIndexReader()
        //   .getTermsForField(LuceneFields.L_CONTENT, contentQuery);
        // int i = 0;
        // while( enumeration.hasMoreElements() )
        // {
        //   BrowseTerm value = enumeration.nextElement();
        //   System.out.printf("[%s] value: %s %s\n", i++, value.getText(), value.getFreq());
        // }

        int max = 1000;

        String field = query.split(":")[0];
        String text = query.split(":")[1];

        ZimbraTopDocs docs1 = searcher.search(new WildcardQuery(new Term(field, text)), max);
        System.out.printf("Query results %s\n", docs1.getTotalHits());

        for( int i = 0; i < docs1.getTotalHits() && i < max; i++ )
        {
          Document document = searcher.doc(docs1.getScoreDoc(i).getDocumentID());
          System.out.printf("[%s] %s\n   %s\n", i, docs1.getScoreDoc(i), document);
        }
      }

      System.out.printf("[STOP] %s search for %s\n-----\n", getClass().getSimpleName(), mailbox.getAccount().getName());
    }
    catch( Exception e )
    {
      e.printStackTrace();
      return 1;
    }

    return 0;
  }
}
