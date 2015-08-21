package org.openzal.zal.lib;

import java.util.Calendar;

public class FakeClock implements Clock
{
  private long mTimestamp;

  public FakeClock(long timestamp) {
    mTimestamp = timestamp;
  }

  @Override
  public long now() {
    return mTimestamp;
  }

  @Override
  public Calendar getCurrentTime() {
    return null;
  }

  @Override
  public Calendar getDaysFromNow(int numDays) {
    return null;
  }
}
