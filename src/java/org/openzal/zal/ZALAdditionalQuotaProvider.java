package org.openzal.zal;

import com.zimbra.cs.mailbox.AdditionalQuotaProvider;
import com.zimbra.cs.mailbox.Mailbox;

import java.util.Objects;

public class ZALAdditionalQuotaProvider implements AdditionalQuotaProvider
{
  private final org.openzal.zal.AdditionalQuotaProvider mAdditionalQuotaProvider;

  public ZALAdditionalQuotaProvider(org.openzal.zal.AdditionalQuotaProvider additionalQuotaProvider)
  {
    mAdditionalQuotaProvider = additionalQuotaProvider;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    ZALAdditionalQuotaProvider that = (ZALAdditionalQuotaProvider) o;
    return Objects.equals(mAdditionalQuotaProvider, that.mAdditionalQuotaProvider);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mAdditionalQuotaProvider);
  }

  @Override
  public long getAdditionalQuota(Mailbox mailbox)
  {
    return mAdditionalQuotaProvider.getAdditionalQuota(new org.openzal.zal.Mailbox(mailbox));
  }
}
