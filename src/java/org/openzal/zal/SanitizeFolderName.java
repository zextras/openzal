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

import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.*;

import java.util.Arrays;
import java.util.List;


public class SanitizeFolderName
{
  private final Mailbox mMbox;
  private final int     mParentId;
  private final String  mName;
  private final String       DEFAULT_FOLDER_NAME = "New Folder";
  private final List<String> RESERVED_NAMES      = Arrays.asList(".", "..");
  private final String       INVALID_CHARS       = "[:/\"]";
  private final int          MAX_LENGHT          = 255;

  public SanitizeFolderName(Mailbox mbox, String name, int parentId)
  {
    mName = name;
    mMbox = mbox;
    mParentId = parentId;
  }

  public String getOriginalName()
  {
    return mName;
  }

  public String sanitizeName(@Nonnull OperationContext zcontext)
    throws InternalServerException
  {
    String sanitize = trimControlChars(mName);
    sanitize = sanitize.replaceAll(INVALID_CHARS, "");

    // Check length after sanitization
    if (sanitize.length() > MAX_LENGHT)
    {
      sanitize = sanitize.substring(0, MAX_LENGHT);
    }

    // Check if is empty or is a reserved name
    if (sanitize.isEmpty() || RESERVED_NAMES.contains(sanitize))
    {
      sanitize = DEFAULT_FOLDER_NAME;
    }

    sanitize = checkExistanceInMailbox(zcontext, sanitize, 0);

    return sanitize;
  }

  private String checkExistanceInMailbox(@Nonnull OperationContext zcontext, String folderName, int start)
    throws UnableToSanitizeFolderNameException
  {
    String tmpFolderName = folderName;
    if (start != 0) {
      tmpFolderName += " " + start;
    }

    if (start > 999) {
      throw new UnableToSanitizeFolderNameException(folderName);
    }

    try
    {
      mMbox.getFolderByName(zcontext, tmpFolderName, mParentId);
      return checkExistanceInMailbox(zcontext,
                                     folderName, start + 1);
    }
    catch(NoSuchItemException ex) {
      return tmpFolderName;
    } catch (UnableToSanitizeFolderNameException ex) {
      throw ex;
    } catch (org.openzal.zal.exceptions.ZimbraException ex)
    {
      throw ExceptionWrapper.createUnableToSanitizeFolder(folderName, ex);
    }
  }

  @Nonnull
  private String trimControlChars(@Nonnull String str) {
    StringBuilder sb = new StringBuilder();
    for (char c : str.toCharArray()) {
      if( c > 31 ) sb.append(c);
    }
    return sb.toString().trim();
  }
}
