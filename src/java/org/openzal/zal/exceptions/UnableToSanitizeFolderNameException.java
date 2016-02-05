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

package org.openzal.zal.exceptions;

public class UnableToSanitizeFolderNameException extends InternalServerException
{
  private final String mFolderName;

  protected UnableToSanitizeFolderNameException(String folderName, ZimbraException ex)
  {
    super(ex);
    mFolderName = folderName;
  }

  public UnableToSanitizeFolderNameException(String folderName)
  {
    super(new RuntimeException("Cannot sanitize folder name: " + folderName));
    mFolderName = folderName;
  }

  @Override
  public String getMessage()
  {
    return "Cannot sanitize folder name: " + mFolderName;
  }
}
