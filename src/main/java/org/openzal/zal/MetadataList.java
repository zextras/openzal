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

import com.zimbra.common.service.ServiceException;
import javax.annotation.Nonnull;
import org.openzal.zal.exceptions.ExceptionWrapper;

public class MetadataList
{
  private final com.zimbra.cs.mailbox.MetadataList mMetadataList;

  public MetadataList()
  {
    mMetadataList = new com.zimbra.cs.mailbox.MetadataList();
  }

  public MetadataList( Object metadataList)
  {
    mMetadataList = (com.zimbra.cs.mailbox.MetadataList)metadataList;
  }

  public MetadataList add(Object value) {

    if( value instanceof Metadata )
    {
      mMetadataList.add(((Metadata)value).toZimbra(com.zimbra.cs.mailbox.Metadata.class));
      return this;
    }
    if( value instanceof MetadataList )
    {
      mMetadataList.add(((MetadataList)value).toZimbra(com.zimbra.cs.mailbox.MetadataList.class));
      return this;
    }

    if (value != null) {
      mMetadataList.add(value);
    }
    return this;
  }

  public MetadataList remove(Object value) {
    mMetadataList.remove(value);
    return this;
  }

  public MetadataList remove(int idx) {
    mMetadataList.remove(idx);
    return this;
  }

  public <T> T toZimbra(@Nonnull Class<T> cls)
  {
    return cls.cast(mMetadataList);
  }

  public String toString()
  {
    return mMetadataList.toString();
  }

  public Metadata getMap(int index)
  {
    try
    {
      return new Metadata(mMetadataList.getMap(index));
    }
    catch (ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public int size()
  {
    return mMetadataList.size();
  }
}
