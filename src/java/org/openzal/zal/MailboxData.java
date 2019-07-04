package org.openzal.zal;


import javax.annotation.Nullable;
import java.util.Objects;

public class MailboxData
{
  private final int mId;
  private final int mSchemaGroupId;
  private final String mAccountId;
  private final Short mIndexVolumeId;

  public MailboxData(int mailboxid)
  {
    this(
      mailboxid,
      (short) (((mailboxid - 1) % 100) + 1),
      null,
      null
    );
  }

  public MailboxData(
    int id,
    int schemaGroupId,
    String accountId,
    Short indexVolumeId
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

  @Nullable
  public String getAccountId()
  {
    return mAccountId;
  }

  @Override
  public boolean equals(Object o)
  {
    if( this == o )
    {
      return true;
    }
    if( o == null || getClass() != o.getClass() )
    {
      return false;
    }
    MailboxData that = (MailboxData) o;
    return mId == that.mId;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mId);
  }

  @Nullable
  public Short getIndexVolumeId()
  {
    return mIndexVolumeId;
  }
}
