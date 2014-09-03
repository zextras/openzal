package org.openzal.zal;

public abstract class StoreManagerConst
{
/* $if MajorZimbraVersion <= 7 $
  private final static String VOLUME_FIELD = "volume_id";
$else$ */
  private final static String VOLUME_FIELD = "locator";
/* $endif$ */

  public static String getVolumeField()
  {
    return VOLUME_FIELD;
  }
}
