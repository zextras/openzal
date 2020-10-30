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

public class AlterItemTag extends RedoableOp
{
  private final RedoableOp mOp;

  public AlterItemTag(RedoableOp op)
  {
    mOp = op;
  }

  public int getMailboxId() {
    return mOp.getMailboxId();
  }

  public Data extractData() throws IOException {
    DataInputStream inputStream = new DataInputStream(mOp.getProxiedObject().getInputStream());
    try {
      inputStream.readInt();
      byte type = inputStream.readByte();
      String tagName = inputStream.readUTF();
      boolean tagged = inputStream.readBoolean();
      boolean hasConstraint = inputStream.readBoolean();
      String constraint = null;
      if (hasConstraint) {
        constraint = inputStream.readUTF();
      }
      int numberOfIds = inputStream.readInt();
      List<Integer> ids = new ArrayList<>(numberOfIds);
      for (int i = 0; i < numberOfIds; i++) {
        ids.add(inputStream.readInt());
      }

      return new Data(
          type,
          tagName,
          tagged,
          Optional.ofNullable(constraint),
          ids
        );
    }  finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public static class Data implements RedoableOp.Data {

    private final byte type;
    private final String tagName;
    private final boolean tagged;
    private final Optional<String> constraint;
    private final List<Integer> ids;

    Data(
        byte type,
        String tagName,
        boolean tagged,
        Optional<String> constraint,
        List<Integer> ids
    ) {
      this.type = type;
      this.tagName = tagName;
      this.tagged = tagged;
      this.constraint = constraint;
      this.ids = ids;
    }

    public byte getType() {
      return type;
    }

    public String getTagName() {
      return tagName;
    }

    public boolean isTagged() {
      return tagged;
    }

    public Optional<String> getConstraint() {
      return constraint;
    }

    public List<Integer> getIds() {
      return ids;
    }
  }
}
