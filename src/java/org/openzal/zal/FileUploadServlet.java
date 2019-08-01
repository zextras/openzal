package org.openzal.zal;

import com.zimbra.common.service.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;

public class FileUploadServlet
{
  public Upload saveUpload(InputStream is, String filename, String contentType, String accountId)
  {
    try
    {
      return new Upload(com.zimbra.cs.service.FileUploadServlet.saveUpload(is,filename,contentType,accountId,true));
    }
    catch( ServiceException | IOException e )
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public static class Upload
  {
    private final com.zimbra.cs.service.FileUploadServlet.Upload mUpload;

    public Upload( @Nonnull Object upload) {mUpload = (com.zimbra.cs.service.FileUploadServlet.Upload)upload;}

    public String getId()
    {
      return mUpload.getId();
    }

    public InputStream getInputStream()
      throws IOException
    {
      return mUpload.getInputStream();
    }

    public String getName() {
      return mUpload.getName();
    }

    public String getContentType() {
      return mUpload.getContentType();
    }

    public long getSize() {
      return mUpload.getSize();
    }
  }
}
