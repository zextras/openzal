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

import com.google.inject.Singleton;
import com.zimbra.cs.mime.*;
import org.jetbrains.annotations.Nullable;
import org.openzal.zal.Utils;
import org.openzal.zal.log.ZimbraLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
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

      /* $if ZimbraVersion >= 7.1.1 $ */
      sHandlerInfoClazzField = sHandlerInfo.getDeclaredField("clazz");
      /* $else $
      sHandlerInfoClazzField = sHandlerInfo.getDeclaredField("mClass");
      /* $endif $ */
      sHandlerInfoClazzField.setAccessible(true);

      /* $if ZimbraVersion >= 7.1.1 $ */
      sHandlerInfoContentTypeField = sHandlerInfo.getDeclaredField("realMimeType");
      /* $else $
      sHandlerInfoContentTypeField = sHandlerInfo.getDeclaredField("mRealMimeType");
      /* $endif $ */

      sHandlerInfoContentTypeField.setAccessible(true);

      /* $if ZimbraVersion >= 7.1.1 $ */
      sHandlerInfoRealMimeTypeField = sHandlerInfo.getDeclaredField("mimeType");
      /* $else $
      sHandlerInfoRealMimeTypeField = sHandlerInfo.getDeclaredField("mMimeType");
      /* $endif $ */
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

}
