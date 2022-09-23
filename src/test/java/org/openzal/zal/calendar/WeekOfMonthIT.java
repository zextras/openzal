package org.openzal.zal.calendar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WeekOfMonthIT
{
  @Test
  public void fromZimbra()
  {
    assertEquals(WeekOfMonth.First, WeekOfMonth.fromZimbra(1));
    assertEquals(WeekOfMonth.Second, WeekOfMonth.fromZimbra(2));
    assertEquals(WeekOfMonth.Third, WeekOfMonth.fromZimbra(3));
    assertEquals(WeekOfMonth.Fourth, WeekOfMonth.fromZimbra(4));
    assertEquals(WeekOfMonth.Last, WeekOfMonth.fromZimbra(-1));
  }

  @Test
  public void toZimbra()
  {
    assertEquals(1, WeekOfMonth.First.toZimbra());
    assertEquals(2, WeekOfMonth.Second.toZimbra());
    assertEquals(3, WeekOfMonth.Third.toZimbra());
    assertEquals(4, WeekOfMonth.Fourth.toZimbra());
    assertEquals(-1, WeekOfMonth.Last.toZimbra());
  }

  @Test
  public void fromEAS()
  {
    assertEquals(WeekOfMonth.First, WeekOfMonth.fromEAS(1));
    assertEquals(WeekOfMonth.Second, WeekOfMonth.fromEAS(2));
    assertEquals(WeekOfMonth.Third, WeekOfMonth.fromEAS(3));
    assertEquals(WeekOfMonth.Fourth, WeekOfMonth.fromEAS(4));
    assertEquals(WeekOfMonth.Last, WeekOfMonth.fromEAS(5));
  }

  @Test
  public void toEAS()
  {
    assertEquals(1, WeekOfMonth.First.toEAS());
    assertEquals(2, WeekOfMonth.Second.toEAS());
    assertEquals(3, WeekOfMonth.Third.toEAS());
    assertEquals(4, WeekOfMonth.Fourth.toEAS());
    assertEquals(5, WeekOfMonth.Last.toEAS());
  }
}
