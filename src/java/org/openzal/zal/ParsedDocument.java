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

import org.jetbrains.annotations.NotNull;
import org.openzal.zal.exceptions.ExceptionWrapper;
import com.zimbra.common.service.ServiceException;

import java.io.IOException;
import java.io.InputStream;


public class ParsedDocument
{
  @NotNull private final com.zimbra.cs.mime.ParsedDocument mParsedDocument;

  public ParsedDocument(
    InputStream stream,
    String filename,
    String ctype,
    long createdDate,
    String creator,
    String description
  )
    throws IOException
  {
    try
    {
      /* $if MajorZimbraVersion >= 7 $ */
      mParsedDocument = new com.zimbra.cs.mime.ParsedDocument(stream, filename, ctype, createdDate, creator, description);
    /* $else$
      mParsedDocument = new com.zimbra.cs.mime.ParsedDocument(stream, filename, ctype, createdDate, creator);
    /* $endif$*/
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  protected <T> T toZimbra(@NotNull Class<T> cls)
  {
    return cls.cast(mParsedDocument);
  }
}
