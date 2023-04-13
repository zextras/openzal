/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2016 ZeXtras S.r.l.
 *
 * This file is part of ZAL.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZAL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openzal.zal.index;

import com.zimbra.cs.convert.AttachmentInfo;
import com.zimbra.cs.convert.ConversionException;
import com.zimbra.cs.mime.*;
import org.apache.lucene.document.Document;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

import javax.activation.DataSource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndexerManager
{
  private static final Field                     sMimeHandlerManagerHandlersField;
  private static final Class<MimeHandlerManager> sMimeHandlerManager;

  private static final Class<?>                  sHandlerInfo;
  private static final Constructor<?>            sHandlerInfoConstructor;
  private static final Field                     sHandlerInfoClazzField;
  private static final Field                     sHandlerInfoContentTypeField;
  private static final Field                     sHandlerInfoRealMimeTypeField;

  private static final List<Indexer>             sIndexerList;

  @Nullable
  private static       Map<String, Object>       sOriginalMap;


  public void register(Indexer indexer)
  {
    sIndexerList.add(indexer);
  }

  public void unregister(Indexer indexer)
  {
    sIndexerList.remove(indexer);
  }


  static
  {
    try
    {
      sOriginalMap = null;
      sIndexerList = Collections.synchronizedList(new LinkedList<Indexer>());

      sMimeHandlerManager = com.zimbra.cs.mime.MimeHandlerManager.class;
      sHandlerInfo = sMimeHandlerManager.getClassLoader().loadClass("com.zimbra.cs.mime.MimeHandlerManager$HandlerInfo");

      sHandlerInfoConstructor = sHandlerInfo.getDeclaredConstructors()[0];
      sHandlerInfoConstructor.setAccessible(true);

      sMimeHandlerManagerHandlersField = sMimeHandlerManager.getDeclaredField("sHandlers");
      sMimeHandlerManagerHandlersField.setAccessible(true);

      sHandlerInfoClazzField = sHandlerInfo.getDeclaredField("clazz");
      sHandlerInfoClazzField.setAccessible(true);

      sHandlerInfoContentTypeField = sHandlerInfo.getDeclaredField("realMimeType");

      sHandlerInfoContentTypeField.setAccessible(true);

      sHandlerInfoRealMimeTypeField = sHandlerInfo.getDeclaredField("mimeType");
      sHandlerInfoRealMimeTypeField.setAccessible(true);
    }
    catch (Throwable ex)
    {
      ZimbraLog.extensions.fatal("ZAL Reflection Initialization Exception: " + Utils.exceptionToString(ex));
      throw new RuntimeException(ex);
    }
  }

  public void attachToZimbra()
  {
    try
    {
      sOriginalMap = (Map<String,Object>) sMimeHandlerManagerHandlersField.get(null);
      sMimeHandlerManagerHandlersField.set(
              null,
              new IndexerProxyMap(
                      sOriginalMap,
                      new MimeHandlerProviderImpl()
              )
      );
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }

  public void detach()
  {
    try
    {
      if( sOriginalMap != null )
      {
        sMimeHandlerManagerHandlersField.set(null, sOriginalMap);
      }
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  public static Indexer getBestIndexer(String contentType, String fileExtension)
  {
    for (Indexer indexer : sIndexerList)
    {
      if (indexer.canHandle(contentType, fileExtension))
      {
        return indexer;
      }
    }
    return null;
  }

  class MimeHandlerProviderImpl implements MimeHandlerProvider
  {
    @Nullable
    @Override
    public Object getMimeHandlerFor(String contentType, String fileExtension)
    {
      Indexer indexer = getBestIndexer(contentType, fileExtension);
      if (indexer != null)
      {
        return createHandlerInfoProxy(
          InternalMimeHandler.class,
          contentType
        );
      }

      return null;
    }
  }

  private Object createHandlerInfoProxy(Class<?> cls, String contentType)
  {
    try
    {
      Object info = sHandlerInfoConstructor.newInstance();

      sHandlerInfoContentTypeField.set(info, contentType);
      sHandlerInfoClazzField.set(info, cls);

      sHandlerInfoRealMimeTypeField.set(info, new MimeTypeInfo() {
        @Override
        public String[] getMimeTypes() {
          return new String[0];
        }

        @Override
        public String getExtension() {
          return null;
        }

        @Override
        public String getHandlerClass() {
          return null;
        }

        @Override
        public boolean isIndexingEnabled() {
          return true;
        }

        @Override
        public String getDescription() {
          return null;
        }

        @Override
        public Set<String> getFileExtensions() {
          return null;
        }

        @Override
        public int getPriority() {
          return 0;
        }
      });

      return info;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public static class InternalMimeHandler extends MimeHandler
  {
    private String content;

    @Override
    protected boolean runsExternally()
    {
      return false;
    }

    //this methods breaks binary compatibility for ZAL given two different parameters types
    @Override
    protected void addFields(Document doc) throws MimeHandlerException
    {

    }

    private Indexer getIndexer()
    {
      Indexer indexer = getBestIndexer(getContentType(), getExtension());
      return indexer == null ? new EmptyIndexer() : indexer;
    }

    @Override
    protected String getContentImpl() throws MimeHandlerException
    {
      if (content == null)
      {
        try {
          content = getIndexer().extractPlainText(
              getDataSource(),
              getContentType(),
              getExtension(),
              getFilename()
          );
        } catch (Exception e) {
          throw new MimeHandlerException(e);
        }
      }

      return content;
    }

    @Nonnull
    private String getExtension()
    {
      String extension = "";
      String filename = getFilename();
      if( filename != null)
      {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex != -1)
        {
          extension = filename.substring(extensionIndex + 1);
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

    private static class EmptyIndexer implements Indexer
    {
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
}
