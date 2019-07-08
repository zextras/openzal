package org.openzal.zal;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class MailboxData
{
  private final Integer mId;
  private final int mSchemaGroupId;
  private final String mAccountId;
  private final Short mIndexVolumeId;

  public MailboxData(@Nonnull Integer mailboxid)
  {
    this(
      mailboxid,
      (short) (((mailboxid - 1) % 100) + 1),
      null,
      null
    );
  }

  public MailboxData(@Nonnull String accountId)
  {
    this(
      null,
      -1,
      accountId,
      null
    );
  }

  public MailboxData(
    Integer id,
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
    boolean equals = true;
    if((mId != null && !Objects.equals(mId, that.mId)) ||
      (that.mId != null && !Objects.equals(that.mId, mId)))
    {
      equals = false;
    }
    if((mAccountId != null && !Objects.equals(mAccountId, that.mAccountId)) ||
      (that.mAccountId != null && !Objects.equals(that.mAccountId, mAccountId)))
    {
      equals = false;
    }
    return equals;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mId, mAccountId);
  }

  @Nullable
  public Short getIndexVolumeId()
  {
    return mIndexVolumeId;
  }
}
