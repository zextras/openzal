package org.openzal.zal.calendar;

public enum AttendeeType
{
  Required, Optional, Resource;

  public static AttendeeType fromString(String role)
  {
    if ("NON".equals(role))
    {
      return Resource;
    }
    else if ("OPT".equals(role))
    {
      return Optional;
    }
    else if ("REQ".equals(role))
    {
      return Required;
    }
    else if ("CHA".equals(role))
    {
      return Resource;
    }

    throw new RuntimeException();
  }
}
