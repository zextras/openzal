package org.openzal.zal;

public class InternalOverrideFactory
{
  public static Object wrapBlob(Blob blob)
  {
    return InternalOverrideVolumeBlob.wrap(blob);
  }

  public static Object wrapMailboxBlob(MailboxBlob blob)
  {
    return InternalOverrideMailboxBlob.wrap(blob);
  }

  public static Object wrapStagedBlob(StagedBlob blob)
  {
    return InternalOverrideStagedBlob.wrap(blob);
  }
}
