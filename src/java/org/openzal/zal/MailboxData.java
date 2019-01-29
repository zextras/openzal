package org.openzal.zal;


public class MailboxData
{
  private final int            mId;
  private final int            mSchemaGroupId;
  private final String         mAccountId;
  private final short          mIndexVolumeId;

  public MailboxData(
    int id,
    int schemaGroupId,
    String accountId,
    short indexVolumeId
  )
  {
    mId = id;
    mSchemaGroupId = schemaGroupId;
    mAccountId = accountId;
    mIndexVolumeId = indexVolumeId;
  }

  public int getId()
  {
    return mId;
  }

  public int getSchemaGroupId()
  {
    return mSchemaGroupId;
  }

  public String getAccountId()
  {
    return mAccountId;
  }

  public short getIndexVolumeId()
  {
    return mIndexVolumeId;
  }
}
