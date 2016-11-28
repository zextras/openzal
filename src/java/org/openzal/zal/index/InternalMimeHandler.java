package org.openzal.zal.index;

import com.zimbra.cs.convert.AttachmentInfo;
import com.zimbra.cs.convert.ConversionException;
import com.zimbra.cs.mime.MimeHandler;
import com.zimbra.cs.mime.MimeHandlerException;
import org.apache.lucene.document.Document;
import org.jetbrains.annotations.NotNull;

import javax.activation.DataSource;
import java.io.IOException;

public class InternalMimeHandler extends MimeHandler {
  private String content;

  @Override
  protected boolean runsExternally()
  {
    return false;
  }


  @Override
  protected void addFields(Document doc) throws MimeHandlerException
  {
  }

  private Indexer getIndexer()
  {
    Indexer indexer = IndexerManager.getBestIndexer(getContentType(), getExtension());
    return indexer == null ? new EmptyIndexer() : indexer;
  }

  @Override
  protected String getContentImpl() throws MimeHandlerException
  {
    if (content == null)
    {
      content = getIndexer().extractPlainText(
              getDataSource(),
              getContentType(),
              getExtension(),
              getFilename()
      );
    }

    return content;
  }

  @NotNull
  private String getExtension()
  {
    String extension = "";
    if (getFilename() != null)
    {
      int extensionIndex = getFilename().lastIndexOf('.');

      if (extensionIndex != -1)
      {
        extension = getFilename().substring(extensionIndex, getFilename().length());
      }
    }
    return extension;
  }

  @Override
  public String convert(AttachmentInfo doc, String urlPart) throws IOException, ConversionException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean doConversion()
  {
    return false;
  }

  public Object getInstance()
  {
    return this;
  }

  private class EmptyIndexer implements Indexer {

    @Override
    public void init()
    {
    }

    @Override
    public boolean canHandle(String contentType, String fileExtension)
    {
      return false;
    }

    @Override
    public String extractPlainText(DataSource dataSource,
                                   String contentType,
                                   String fileExtension,
                                   String fileName)
    {
      return "";
    }
  }
}
