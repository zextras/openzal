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

package org.openzal.zal.lib;

import com.zimbra.znative.IO;

import java.io.File;
import java.io.IOException;


public class ZalIOUtils
{
  public static int linkCount(String path)
    throws IOException
  {
    return IO.linkCount(path);
  }

  public static void link(String oldPath, String newPath)
    throws IOException
  {
    IO.link(oldPath, newPath);
  }

  public static long getInodeId(String path)
    throws IOException
  {
    /* $if ZimbraVersion >= 8.0.2 $ */
      return IO.fileInfo(path).getInodeNum();
    /* $else$
      return 0;
    /* $endif# */
  }
}
