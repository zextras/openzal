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
import com.zimbra.cs.store.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openzal.zal.exceptions.ExceptionWrapper;
import org.openzal.zal.exceptions.ZimbraException;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.cs.volume.*;


public class StoreVolume
{
  private Volume mVolume;

  public static final short ID_AUTO_INCREMENT = Volume.ID_AUTO_INCREMENT;
  public static final short ID_NONE           = Volume.ID_NONE;
  public static final short ID_MAX            = Volume.ID_MAX;

  public static final short TYPE_MESSAGE           = Volume.TYPE_MESSAGE;
  public static final short TYPE_MESSAGE_SECONDARY = Volume.TYPE_MESSAGE_SECONDARY;
  public static final short TYPE_INDEX             = Volume.TYPE_INDEX;

  public static final String SUBDIR_MESSAGE = "msg";

  private static final short sMboxGroupBits = 8;
  private static final short sMboxBits = 12;
  private static final short sFileGroupBits = 8;
  private static final short sFileBits = 12;


  public StoreVolume(@Nonnull Object vol)
  {
    if ( vol == null )
      throw new NullPointerException("Volume is null");

    mVolume = (Volume)vol;
  }

  public String getId() { return String.valueOf(mVolume.getId()); }
  public short getType() { return mVolume.getType(); }
  public String getName() { return mVolume.getName(); }
  public String getLocator() { return mVolume.getLocator(); }
  public String getRootPath() { return mVolume.getRootPath(); }
  public String getMailboxDir(int id, short type) {
    try {
      return mVolume.getMailboxDir(id, type);
    } catch (ServiceException e) {
      throw new RuntimeException(e);
    }
  }
  public String getIncomingMsgDir() { return mVolume.getIncomingMsgDir(); }
  public IncomingDirectory getIncomingDirectory() {
    return mVolume.getIncomingDirectory();
  }
  public short getMboxGroupBits() { return mVolume.getMboxGroupBits(); }
  public short getMboxBits() { return mVolume.getMboxBits(); }
  public short getFileGroupBits() { return mVolume.getFileGroupBits(); }
  public short getFileBits() { return mVolume.getFileBits(); }
  public boolean getCompressBlobs() {
    return mVolume.isCompressBlobs();
  }
  public long getCompressionThreshold() { return mVolume.getCompressionThreshold(); }

  @Deprecated
  public static List<StoreVolume> getAll()
  {
    List<Volume> list = com.zimbra.cs.volume.VolumeManager.getInstance().getAllVolumes();

    return ZimbraListWrapper.wrapVolumes(list);
  }

  @Deprecated
    public static StoreVolume update(short id, short type,
                                  String name, String path,
                                  boolean compressBlobs, long compressionThreshold)
    throws ZimbraException
    {

      StoreVolume volumeToUpdate = StoreVolume.getById(id);

      Volume vol;
      try
      {
        Volume.Builder builder = Volume.builder();
        builder.setId(id);
        builder.setName(name);
        builder.setType(type);
        builder.setPath(path, true);
        builder.setMboxGroupBits(volumeToUpdate.getMboxGroupBits());
        builder.setMboxBit(volumeToUpdate.getMboxBits());
        builder.setFileGroupBits(volumeToUpdate.getFileGroupBits());
        builder.setFileBits(volumeToUpdate.getFileBits());
        builder.setCompressBlobs(compressBlobs);
        builder.setCompressionThreshold(compressionThreshold);
        vol = builder.build();
        vol = com.zimbra.cs.volume.VolumeManager.getInstance().update(vol);
      }
      catch (com.zimbra.cs.volume.VolumeServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
      return new StoreVolume(vol);
    }

  @Deprecated
    public static StoreVolume create(short id, short type,
                                  String name, String path,
                                  boolean compressBlobs, long compressionThreshold)
      throws ZimbraException
    {
      Volume vol;
      try
      {
        Volume.Builder builder = Volume.builder();
        builder.setId(id);
        builder.setType(type);
        builder.setName(name);
        builder.setPath(path, true);
        builder.setMboxGroupBits(sMboxGroupBits);
        builder.setMboxBit(sMboxBits);
        builder.setFileGroupBits(sFileGroupBits);
        builder.setFileBits(sFileBits);
        builder.setCompressBlobs(compressBlobs);
        builder.setCompressionThreshold(compressionThreshold);

        vol = builder.build();
        vol = com.zimbra.cs.volume.VolumeManager.getInstance().create(vol);
      }
      catch (com.zimbra.cs.volume.VolumeServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
      return new StoreVolume(vol);
    }

  @Deprecated
    public static void setCurrentVolume(short volType, short id)
      throws ZimbraException
    {
      try
      {
        com.zimbra.cs.volume.VolumeManager.getInstance().setCurrentVolume(volType, id);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

  @Deprecated
  public static StoreVolume setDefaultBits(short id)
    throws ZimbraException
  {

    StoreVolume volumeToUpdate = StoreVolume.getById(id);

    Volume vol;
    try
    {
      Volume.Builder builder = Volume.builder();
      builder.setId(Short.parseShort(volumeToUpdate.getId()));
      builder.setName(volumeToUpdate.getName());
      builder.setType(volumeToUpdate.getType());
      builder.setPath(volumeToUpdate.getRootPath(), true);
      builder.setMboxGroupBits(sMboxGroupBits);
      builder.setMboxBit(sMboxBits);
      builder.setFileGroupBits(sFileGroupBits);
      builder.setFileBits(sFileBits);
      builder.setCompressBlobs(volumeToUpdate.getCompressBlobs());
      builder.setCompressionThreshold(volumeToUpdate.getCompressionThreshold());
      vol = builder.build();
      vol = com.zimbra.cs.volume.VolumeManager.getInstance().update(vol);
    }
    catch (com.zimbra.cs.volume.VolumeServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new StoreVolume(vol);
  }

  @Deprecated
    public static boolean delete(short id) throws ZimbraException
    {
      try
      {
        return com.zimbra.cs.volume.VolumeManager.getInstance().delete(id);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

  @Deprecated
    public static StoreVolume getById(short vid) throws org.openzal.zal.exceptions.ZimbraException
    {
      try
      {
        return new StoreVolume(com.zimbra.cs.volume.VolumeManager.getInstance().getVolume(vid));
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

    @Deprecated
    @Nullable
    public static StoreVolume getVolumeByName(String volumeName)
    {
      for (StoreVolume storeVolume : StoreVolume.getAll())
      {
        if (storeVolume.getName().equals(volumeName))
        {
          return storeVolume;
        }
      }
      return null;
    }

  @Deprecated
    @Nullable
    public static StoreVolume getCurrentSecondaryMessageVolume()
    {
      Volume vol = com.zimbra.cs.volume.VolumeManager.getInstance().getCurrentSecondaryMessageVolume();

      if( vol != null )
      {
        return new StoreVolume(vol);
      }
      else
      {
        return null;
      }
    }


  @Deprecated
    public static boolean hasSecondaryMessageVolume()
    {
      return getCurrentSecondaryMessageVolume() != null;
    }

  @Deprecated
    public static List<StoreVolume> getByType(short type)
    {
      List<Volume> list = com.zimbra.cs.volume.VolumeManager.getInstance().getAllVolumes();

      ArrayList<StoreVolume> newList = new ArrayList<StoreVolume>(list.size());
      for( Volume vol : list )
      {
        if( vol.getType() == type ) {
          newList.add(new StoreVolume(vol));
        }
      }
      return newList;
    }

  @Deprecated
    public static StoreVolume getCurrentMessageVolume()
    {
      return new StoreVolume(com.zimbra.cs.volume.VolumeManager.getInstance().getCurrentMessageVolume());
    }

  public String getBlobDir(int mboxId, int itemId)
  {
    try {
      return mVolume.getBlobDir(mboxId, itemId);
    } catch (ServiceException e) {
      throw new RuntimeException(e);
    }
  }
}
