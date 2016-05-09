package org.openzal.zal;

import java.util.*;

import org.jetbrains.annotations.Nullable;
import org.openzal.zal.exceptions.*;
import org.openzal.zal.exceptions.ZimbraException;

/* $if MajorZimbraVersion <= 7 $
import com.zimbra.cs.store.file.*;
  $else$ */
import com.zimbra.cs.volume.*;
/* $endif$ */
import com.google.inject.Singleton;

@Singleton
public class VolumeManager
{
  private static final short sMboxGroupBits = 8;
  private static final short sMboxBits = 12;
  private static final short sFileGroupBits = 8;
  private static final short sFileBits = 12;

  /* $if MajorZimbraVersion > 7 $ */
  private final com.zimbra.cs.volume.VolumeManager  mVolumeManager;

  public VolumeManager()
  {
    mVolumeManager = com.zimbra.cs.volume.VolumeManager.getInstance();
  }
  /* $endif $ */

  public List<StoreVolume> getAll()
  {
    /* $if MajorZimbraVersion <= 7 $
    List<Volume> list = Volume.getAll();
    /* $else$ */
    List<Volume> list = mVolumeManager.getAllVolumes();
    /* $endif$ */

    return ZimbraListWrapper.wrapVolumes(list);
  }

  public StoreVolume update(String id, short type,
                            String name, String path,
                            boolean compressBlobs, long compressionThreshold)
    throws ZimbraException
  {

    StoreVolume volumeToUpdate = getById(id);

    Volume vol;
    try
    {
      /* $if MajorZimbraVersion <= 7 $
      vol = Volume.update(Short.parseShort(id), type, name, path,
                                 volumeToUpdate.getMboxGroupBits(),
                                 volumeToUpdate.getMboxBits(),
                                 volumeToUpdate.getFileGroupBits(),
                                 volumeToUpdate.getFileBits(),
                                 compressBlobs, compressionThreshold, false);
      /* $else$ */
      Volume.Builder builder = Volume.builder();
      builder.setId(Short.parseShort(id));
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
      vol = mVolumeManager.update(vol);
      /* $endif$ */
    }
    /* $if MajorZimbraVersion <= 7 $
      catch (com.zimbra.cs.store.file.VolumeServiceException e)
    /* $else$ */
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

  public StoreVolume create(short id, short type,
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
      /* $else$ */
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
      vol = mVolumeManager.create(vol);
      /* $endif$ */
    }
    /* $if MajorZimbraVersion <= 7 $
    catch (com.zimbra.cs.store.file.VolumeServiceException e)
    /* $else$ */
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

  public void setCurrentVolume(short volType, short id)
    throws ZimbraException
  {
    try
    {
      /* $if MajorZimbraVersion <= 7 $
      Volume.setCurrentVolume(volType, id);
      /* $else$ */
      mVolumeManager.setCurrentVolume(volType, id);
      /* $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public StoreVolume setDefaultBits(String id)
    throws ZimbraException
  {

    StoreVolume volumeToUpdate = getById(id);

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
      /* $else$ */
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
      vol = mVolumeManager.update(vol);
      /* $endif$ */
    }
    /* $if MajorZimbraVersion <= 7 $
    catch (com.zimbra.cs.store.file.VolumeServiceException e)
    /* $else$ */
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

  public boolean delete(short id) throws ZimbraException
  {
    try
    {
      /* $if MajorZimbraVersion <= 7 $
      return Volume.delete(id);
      /* $else$ */
      return mVolumeManager.delete(id);
      /* $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  public StoreVolume getById(String vid) throws org.openzal.zal.exceptions.ZimbraException
  {
    try
    {
      /* $if MajorZimbraVersion <= 7 $
      return new StoreVolume(Volume.getById(vid));
      /* $else$ */
      return new StoreVolume(mVolumeManager.getVolume(vid));
      /* $endif$ */
    }
    catch (com.zimbra.common.service.ServiceException e)
    {
      throw ExceptionWrapper.wrap(e);
    }
  }

  @Nullable
  public StoreVolume getCurrentSecondaryMessageVolume()
  {
    Volume vol;
    /* $if MajorZimbraVersion <= 7 $
    vol = Volume.getCurrentSecondaryMessageVolume();
    /* $else$ */
    vol = mVolumeManager.getCurrentSecondaryMessageVolume();
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

  public boolean hasSecondaryMessageVolume()
  {
    return getCurrentSecondaryMessageVolume() != null;
  }

  public List<StoreVolume> getByType(short type)
  {
    /* $if MajorZimbraVersion <= 7 $
    List<Volume> list = Volume.getByType(type);
    /* $else$ */
    List<Volume> list = mVolumeManager.getAllVolumes();
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

  public StoreVolume getCurrentMessageVolume()
  {
    /* $if MajorZimbraVersion <= 7 $
    return new StoreVolume(Volume.getCurrentMessageVolume());
    /* $else$ */
    return new StoreVolume(mVolumeManager.getCurrentMessageVolume());
    /* $endif$ */
  }

  public StoreVolume getVolumeByName(String volumeName)
  {
    for (StoreVolume storeVolume : getAll())
    {
      if (storeVolume.getName().equals(volumeName))
      {
        return storeVolume;
      }
    }

    return null;
  }

  public boolean isValidVolume(String id)
  {
    boolean valid = false;
    List<StoreVolume> volumeList = getAll();

    for (StoreVolume v:volumeList){
      if (v.getId().equals(id)){
        valid = true;
        break;
      }
    }

    return valid;
  }
}
