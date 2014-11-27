/*
 * ZAL - The abstraction layer for Zimbra.
 * Copyright (C) 2014 ZeXtras S.r.l.
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

import java.util.*;

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.store.*;
import org.jetbrains.annotations.NotNull;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.store.file.*;
  $else$ */
import com.zimbra.cs.volume.*;
/* $endif$ */


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


  public StoreVolume(@NotNull Object vol)
  {
    if ( vol == null )
      throw new NullPointerException("Volume is null");

    mVolume = (Volume)vol;
  }

  public short getId() { return mVolume.getId(); }
  public short getType() { return mVolume.getType(); }
  public String getName() { return mVolume.getName(); }
  public String getLocator() { return mVolume.getLocator(); }
  public String getRootPath() { return mVolume.getRootPath(); }
  public String getMailboxDir(int id, short type) {
/* $if ZimbraVersion >= 8.5.0 $ */
    try {
/* $endif$ */
      return mVolume.getMailboxDir(id, type);
/* $if ZimbraVersion >= 8.5.0 $ */
    } catch (ServiceException e) {
      throw new RuntimeException(e);
    }
/* $endif$ */
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
/* $if MajorZimbraVersion <= 7 $
    return mVolume.getCompressBlobs();
   $else$ */
    return mVolume.isCompressBlobs();
/* $endif$ */
  }
  public long getCompressionThreshold() { return mVolume.getCompressionThreshold(); }

  public static List<StoreVolume> getAll()
  {
/* $if MajorZimbraVersion <= 7 $
    List<Volume> list = Volume.getAll();
   $else$ */
    List<Volume> list = VolumeManager.getInstance().getAllVolumes();
/* $endif$ */

    return ZimbraListWrapper.wrapVolumes(list);
  }

    public static StoreVolume update(short id, short type,
                                  String name, String path,
                                  boolean compressBlobs, long compressionThreshold)
    throws ZimbraException
    {

      StoreVolume volumeToUpdate = StoreVolume.getById(id);

      Volume vol;
      try
      {
/* $if MajorZimbraVersion <= 7 $
        vol = Volume.update(id, type, name, path,
                                   volumeToUpdate.getMboxGroupBits(),
                                   volumeToUpdate.getMboxBits(),
                                   volumeToUpdate.getFileGroupBits(),
                                   volumeToUpdate.getFileBits(),
                                   compressBlobs, compressionThreshold, false);
   $else$ */
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
        vol = VolumeManager.getInstance().update(vol);
  /* $endif$ */
      }
  /* $if MajorZimbraVersion <= 7 $
      catch (com.zimbra.cs.store.file.VolumeServiceException e)
    $else$ */
      catch (com.zimbra.cs.volume.VolumeServiceException e)
  /* $endif$ */
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
/* $endif$ */
      return new StoreVolume(vol);
    }

    public static StoreVolume create(short id, short type,
                                  String name, String path,
                                  boolean compressBlobs, long compressionThreshold)
      throws ZimbraException
    {
      Volume vol;
      try
      {
/* $if MajorZimbraVersion <= 7 $
        vol = Volume.create(id, type, name, path,
                                   sMboxGroupBits, sMboxBits, sFileGroupBits, sFileBits,
                                   compressBlobs, compressionThreshold, false);
   $else$ */
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
        vol = VolumeManager.getInstance().create(vol);
/* $endif$ */
      }
  /* $if MajorZimbraVersion <= 7 $
      catch (com.zimbra.cs.store.file.VolumeServiceException e)
    $else$ */
      catch (com.zimbra.cs.volume.VolumeServiceException e)
  /* $endif$ */
      {
        throw ExceptionWrapper.wrap(e);
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
      return new StoreVolume(vol);
    }

    public static void setCurrentVolume(short volType, short id)
      throws ZimbraException
    {
      try
      {
/* $if MajorZimbraVersion <= 7 $
        Volume.setCurrentVolume(volType, id);
   $else$ */
        VolumeManager.getInstance().setCurrentVolume(volType, id);
/* $endif$ */
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

  public static StoreVolume setDefaultBits(short id)
    throws ZimbraException
  {

    StoreVolume volumeToUpdate = StoreVolume.getById(id);

    Volume vol;
    try
    {
/* $if MajorZimbraVersion <= 7 $
      vol = Volume.update(volumeToUpdate.getId(),
                                 volumeToUpdate.getType(),
                                 volumeToUpdate.getName(),
                                 volumeToUpdate.getRootPath(),
                                 sMboxGroupBits,
                                 sMboxBits,
                                 sFileGroupBits,
                                 sFileBits,
                                 volumeToUpdate.getCompressBlobs(),
                                 volumeToUpdate.getCompressionThreshold(),
                                 false);
   $else$ */
      Volume.Builder builder = Volume.builder();
      builder.setId(volumeToUpdate.getId());
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
      vol = VolumeManager.getInstance().update(vol);
/* $endif$ */
    }
  /* $if MajorZimbraVersion <= 7 $
    catch (com.zimbra.cs.store.file.VolumeServiceException e)
    $else$ */
    catch (com.zimbra.cs.volume.VolumeServiceException e)
  /* $endif$ */
    {
      throw ExceptionWrapper.wrap(e);
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
    return new StoreVolume(vol);
  }

    public static boolean delete(short id) throws ZimbraException
    {
      try
      {
/* $if MajorZimbraVersion <= 7 $
        return Volume.delete(id);
   $else$ */
        return VolumeManager.getInstance().delete(id);
/* $endif$ */
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

    public static StoreVolume getById(short vid) throws org.openzal.zal.exceptions.ZimbraException
    {
      try
      {
/* $if MajorZimbraVersion <= 7 $
        return new StoreVolume(Volume.getById(vid));
   $else$ */
        return new StoreVolume(VolumeManager.getInstance().getVolume(vid));
/* $endif$ */
      }
      catch (com.zimbra.common.service.ServiceException e)
      {
        throw ExceptionWrapper.wrap(e);
      }
    }

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

    @Nullable
    public static StoreVolume getCurrentSecondaryMessageVolume()
    {
      Volume vol;
/* $if MajorZimbraVersion <= 7 $
      vol = Volume.getCurrentSecondaryMessageVolume();
   $else$ */
      vol = VolumeManager.getInstance().getCurrentSecondaryMessageVolume();
/* $endif$ */

      if( vol != null )
      {
        return new StoreVolume(vol);
      }
      else
      {
        return null;
      }
    }

    public static List<StoreVolume> getByType(short type)
    {
/*  $if MajorZimbraVersion <= 7 $
      List<Volume> list = Volume.getByType(type);
    $else$ */
      List<Volume> list = VolumeManager.getInstance().getAllVolumes();
/*  $endif$ */

      ArrayList<StoreVolume> newList = new ArrayList<StoreVolume>(list.size());
      for( Volume vol : list )
      {
        if( vol.getType() == type ) {
          newList.add(new StoreVolume(vol));
        }
      }
      return newList;
    }

    public static StoreVolume getCurrentMessageVolume()
    {
/* $if MajorZimbraVersion <= 7 $
      return new StoreVolume(Volume.getCurrentMessageVolume());
   $else$ */
      return new StoreVolume(VolumeManager.getInstance().getCurrentMessageVolume());
/* $endif$ */
    }

  public String getBlobDir(int mboxId, int itemId)
  {
/* $if ZimbraVersion >= 8.5.0 $ */
    try {
/* $endif$ */
      return mVolume.getBlobDir(mboxId, itemId);
/* $if ZimbraVersion >= 8.5.0 $ */
    } catch (ServiceException e) {
      throw new RuntimeException(e);
    }
/* $endif$ */
  }
}
