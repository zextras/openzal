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

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.file.VolumeStagedBlob;

import java.io.IOException;

public class InternalOverrideStagedBlob extends com.zimbra.cs.store.StagedBlob
{
  private final StagedBlob mBlob;

  protected InternalOverrideStagedBlob(StagedBlob blob)
  {
    super(null, null, 0);
    mBlob = blob;
  }

  @Override
  public Mailbox getMailbox()
  {
    return mBlob.getMailbox().toZimbra(Mailbox.class);
  }

  @Override
  public long getSize()
  {
    try
    {
      return mBlob.getSize();
    }
    catch (IOException e)
    {
      return -1;
    }
  }

  @Override
  public String getDigest()
  {
    try
    {
      return mBlob.getDigest();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getLocator()
  {
    return mBlob.getVolumeId();
  }

  public static Object wrap(StagedBlob stagedBlob)
  {
    if (stagedBlob instanceof StagedBlobWrap)
    {
      return ((StagedBlobWrap) stagedBlob).getWrappedObject();
    }
    return new InternalOverrideStagedBlob(stagedBlob);
  }

  public StagedBlob getWrappedObject()
  {
    return mBlob;
  }
}
