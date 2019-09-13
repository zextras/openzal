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

package org.openzal.zal;

import com.zimbra.cs.zimlet.ZimletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.openzal.zal.exceptions.ExceptionWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZimletFile
{
  public interface CompressionLevel {
    String GZIP     = "gzip";
    String BROTLI   = "br";
    String IDENTITY = "identity";
  }

  @Nonnull private final com.zimbra.cs.zimlet.ZimletFile mZimletFile;

  protected ZimletFile(@Nonnull Object zimletFile)
  {
    if (zimletFile == null)
    {
      throw new NullPointerException();
    }
    mZimletFile = (com.zimbra.cs.zimlet.ZimletFile) zimletFile;
  }

  public ZimletFile(String zimlet) throws IOException
  {
    try
    {
      mZimletFile = new com.zimbra.cs.zimlet.ZimletFile(zimlet);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimletFile(String name, InputStream is) throws IOException
  {
    try
    {
      mZimletFile = new com.zimbra.cs.zimlet.ZimletFile(name, is);
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public ZimletDescription getZimletDescription() throws IOException
  {
    try
    {
      return new ZimletDescription(mZimletFile.getZimletDescription());
    }
    catch (ZimletException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public String getZimletPath()
  {
    return mZimletFile.getFile().getAbsolutePath();
  }

  protected <T> T toZimbra(Class<T> cls)
  {
    return cls.cast(mZimletFile);
  }

  public String getName()
  {

    return mZimletFile.getName();
  }

  public byte[] getZimletContent()
  {
    return mZimletFile.toByteArray();
  }

  /**
   * This method returns the content stream a particular Zimlet file. This method uses an fallback approach: if a
   * specified content is requested but its not available, the method recursively call itself with a secondary
   * compression level and so on.
   * @param name The Zimlet file
   * @param compressionLevel A list of possible acceptable compressions
   * @return A pair with the string containing the name of the compression selected and the InputStream itself
   * @throws IOException
   */
  @Nullable
  public Pair<String, InputStream> getContentStream(final String name, List<String> compressionLevel) throws IOException
  {
    if (compressionLevel.isEmpty())
    {
      return null;
    }
    final String compressCoded = compressionLevel.get(0);

    com.zimbra.cs.zimlet.ZimletFile.ZimletEntry entry;
    InputStream res = null;
    switch( compressCoded )
    {
      case CompressionLevel.IDENTITY:
      {
        entry = mZimletFile.getEntry(name);
        if( entry != null )
        {
          res = entry.getContentStream();
        }
        break;
      }
      case CompressionLevel.GZIP:
      {
        String compressedEntryName = name + "." + compressCoded;
        entry = mZimletFile.getEntry(compressedEntryName);
        String compressedFilePath = new File(mZimletFile.getFile(), compressedEntryName).getPath();
        if( entry == null )
        {
          // No cached file, create a new one and then return the InputStream
          GzipCompressorOutputStream gzipCompressorOutputStream = new GzipCompressorOutputStream(
            new FileOutputStream(compressedFilePath)
          );
          gzipCompressorOutputStream.write(mZimletFile.getEntry(name).getContents());
          gzipCompressorOutputStream.flush();
          gzipCompressorOutputStream.close();
        }
        res = new FileInputStream(compressedFilePath);
        break;
      }
      case CompressionLevel.BROTLI:
      {
        String compressedEntryName = name + "." + compressCoded;
        entry = mZimletFile.getEntry(compressedEntryName);
        String compressedFilePath = new File(mZimletFile.getFile(), compressedEntryName).getPath();
        if( entry != null )
        {
          res = new FileInputStream(compressedFilePath);
        }
        break;
      }
      default:
        return null;
    }

    if (res != null)
    {
      return new Pair<>(compressCoded, res);
    }
    return getContentStream(name, compressionLevel.subList(1, compressionLevel.size()));
  }

  @Nullable
  public InputStream getContentStream(String name) throws IOException
  {
    final Pair<String, InputStream> contentStream = getContentStream(
      name,
      Collections.singletonList(CompressionLevel.IDENTITY)
    );
    return contentStream == null? null : contentStream.getSecond();
  }

  @Nullable
  public byte[] getContent(String name) throws IOException
  {
    com.zimbra.cs.zimlet.ZimletFile.ZimletEntry entry = mZimletFile.getEntry(name);
    if (entry == null)
    {
      return null;
    }
    return entry.getContents();
  }
}
