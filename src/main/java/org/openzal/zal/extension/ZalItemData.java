package org.openzal.zal.extension;

/**
 * Item information wrapper only used by {@link InternalOverrideStoreManager) class for blobs deletion when
 * {@link org.openzal.zal.StoreFeature#BULK_DELETE} is not supported
 */
public class ZalItemData {

  public final int itemId;
  public final int revision;

  public ZalItemData(int itemId, int revision) {
    this.itemId = itemId;
    this.revision = revision;
  }
}
