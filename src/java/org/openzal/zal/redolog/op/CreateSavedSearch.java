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

package org.openzal.zal.redolog.op;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;

public class CreateSavedSearch //extends RedoableOp
{
  /*public CreateSavedSearch(com.zimbra.cs.redolog.op.RedoableOp op)
  {
    super(op);
  }

  public Data extractData() throws IOException {
    DataInputStream inputStream = getDataInputStream();
    try {
      int searchId = inputStream.readInt();
      String uuid = inputStream.readUTF();
      String name = inputStream.readUTF();
      String query = inputStream.readUTF();
      String types = inputStream.readUTF();
      String sort = inputStream.readUTF();
      int folderId = inputStream.readInt();
      int flags = inputStream.readInt();
      long color = inputStream.readLong();

      return new Data(
        searchId,
        uuid,
        name,
        query,
        types,
        sort,
        folderId,
        flags,
        color
      );
    }  finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public static class Data implements RedoableOp.Data {

    private final int searchId;
    private final String uuid;
    private final String name;
    private final String query;
    private final String types;
    private final String sort;
    private final int folderId;
    private final int flags;
    private final long color;

    Data(
        int searchId,
        String uuid,
        String name,
        String query,
        String types,
        String sort,
        int folderId,
        int flags,
        long color
    ) {
        this.searchId = searchId;
        this.uuid = uuid;
        this.name = name;
        this.query = query;
        this.types = types;
        this.sort = sort;
        this.folderId = folderId;
        this.flags = flags;
        this.color = color;
    }

    public int getSearchId() {
      return searchId;
    }

    public String getUuid() {
      return uuid;
    }

    public String getName() {
      return name;
    }

    public String getQuery() {
      return query;
    }

    public String getTypes() {
      return types;
    }

    public String getSort() {
      return sort;
    }

    public int getFolderId() {
      return folderId;
    }

    public int getFlags() {
      return flags;
    }

    public long getColor() {
      return color;
    }
  }*/
}
