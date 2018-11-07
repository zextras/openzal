package org.openzal.zal;

/* $if ZimbraVersion >= 8.8.10 $ */
import com.zimbra.cs.mailbox.AdditionalQuotaProvider;
/* $endif $ */
import com.zimbra.cs.mailbox.Mailbox;

import java.util.Objects;

/* $if ZimbraVersion >= 8.8.10 $ */
public class ZALAdditionalQuotaProvider implements AdditionalQuotaProvider
/* $else $
public class ZALAdditionalQuotaProvider
/* $endif $ */
{
  private final org.openzal.zal.AdditionalQuotaProvider mAdditionalQuotaProvider;

  public ZALAdditionalQuotaProvider(Object additionalQuotaProvider)
  {
    /* $if ZimbraVersion >= 8.8.10 $ */
    mAdditionalQuotaProvider = (org.openzal.zal.AdditionalQuotaProvider) additionalQuotaProvider;
    /* $else $
    mAdditionalQuotaProvider = null;
    /* $endif $ */
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

  public long getAdditionalQuota(Mailbox mailbox)
  {
    /* $if ZimbraVersion >= 8.8.10 $ */
    return mAdditionalQuotaProvider.getAdditionalQuota(new org.openzal.zal.Mailbox(mailbox));
    /* $else $
    return 0L;
    /* $endif $ */
  }
}
