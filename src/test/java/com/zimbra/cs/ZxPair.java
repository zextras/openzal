package com.zimbra.cs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ZxPair<F,S>
{
  private final F mFirst;
  private final S mSecond;

  @JsonCreator
  public ZxPair(
    @JsonProperty("first") F first,
    @JsonProperty("second") S second)
  {
    mFirst = first;
    mSecond = second;
  }

  @JsonGetter("first")
  public F first() {
    return mFirst;
  }

  @JsonGetter("second")
  public S second() {
    return mSecond;
  }

  public String toString() {
    return "(" + mFirst + ", " + mSecond + ")";
  }

  public boolean equals(Object object) {
    if (object instanceof ZxPair) {
      ZxPair obj = (ZxPair) object;

      if (mFirst == null || !mFirst.equals(obj.mFirst))
        return false;

      if (mSecond == null || !mSecond.equals(obj.mSecond))
        return false;

      return true;
    }

    return false;

  }

  public int hashCode() {
    return (mFirst!=null?mFirst.hashCode():0) + (mSecond!=null?mSecond.hashCode():0);
  }

}
