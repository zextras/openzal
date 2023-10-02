package org.openzal.zal.lib;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionTest {

  @Test
  public void testToString() throws Exception
  {
    Version version_1 = Version.parse("1");
    assertEquals("1",version_1.toString());

    Version version_2 = Version.parse("1.2");
    assertEquals("1.2",version_2.toString());

    Version version_3 = Version.parse("1.2.3");
    assertEquals("1.2.3",version_3.toString());

    Version version_4 = Version.parse("1.2.3.4");
    assertEquals("1.2.3.4",version_4.toString());

    // Chat version style
    Version version_5 = Version.parse("1.23");
    assertEquals("1.23",version_5.toString());

    Version version_6 = Version.parse("0.90");
    assertEquals("0.90",version_6.toString());
  }

  @Test
  public void testCompareTo() throws Exception
  {
    Version version1 = Version.of(1);
    Version version10 = Version.of(1, 0);
    Version version11 = Version.of(1, 1);
    Version version111 = Version.of(1, 1, 1);
    Version version112 = Version.of(1, 1, 2);
    Version version20 = Version.of(2, 0);
    Version version72 = Version.of(7, 2);

    assertEquals(-1, version1.compareTo(version11));
    assertEquals(-1, version11.compareTo(version111));
    assertEquals(-1, version112.compareTo(version20));
    assertEquals(-1, version20.compareTo(version72));
    assertEquals(-1, version1.compareTo(version72));
    assertEquals(-1, version111.compareTo(version72));

    assertEquals(0, version1.compareTo(version10));
    assertEquals(0, version10.compareTo(version1));
    assertEquals(0, version1.compareTo(version1));
    assertEquals(0, version10.compareTo(version10));
    assertEquals(0, version11.compareTo(version11));
    assertEquals(0, version111.compareTo(version111));
    assertEquals(0, version112.compareTo(version112));
    assertEquals(0, version20.compareTo(version20));
    assertEquals(0, version72.compareTo(version72));

    assertEquals(1, version11.compareTo(version1));
    assertEquals(1, version111.compareTo(version11));
    assertEquals(1, version20.compareTo(version112));
    assertEquals(1, version72.compareTo(version20));
    assertEquals(1, version72.compareTo(version1));
    assertEquals(1, version72.compareTo(version11));
  }

  @Test
  public void testEquals() throws Exception
  {
    Version version1 = Version.of(1);
    Version version10 = Version.of(1, 0);
    Version version100 = Version.of(1, 0, 0);
    Version version1000 = Version.of(1, 0, 0, "0");
    Version version801 = Version.of(8, 0, 1);
    Version version8010 = Version.of(8, 0, 1, "0");
    Version version801_reprise = Version.of(8, 0, 1);

    assertTrue(version1.equals(version1));
    assertTrue(version1.equals(version10));
    assertTrue(version1.equals(version100));
    assertFalse(version1.equals(version1000));

    assertTrue(version10.equals(version1));
    assertTrue(version100.equals(version1));
    assertFalse(version1000.equals(version1));

    assertFalse(version1000.equals(version100));
    assertTrue(version1000.equals(version1000));

    assertTrue(version801.equals(version801));
    assertTrue(version801_reprise.equals(version801));
    assertFalse(version8010.equals(version801));
    assertFalse(version801.equals(version8010));
    assertTrue(version801.equals(version801_reprise));

    assertFalse(version1.equals(version801));
    assertFalse(version801.equals(version1));

    //noinspection ObjectEqualsNull
    assertFalse(version1.equals(null));
    //noinspection EqualsBetweenInconvertibleTypes
    assertFalse(version1.equals(""));
  }

  @Test
  public void testGetMajor() throws Exception
  {
    Version version = Version.of(8, 0, 1);
    assertEquals(8, version.getMajor());

    version = Version.of(5);
    assertEquals(5, version.getMajor());
  }

  @Test
  public void testGetMinor() throws Exception
  {
    Version version = Version.of(8, 0, 1);
    assertEquals(0, version.getMinor());

    version = Version.of(8, 2);
    assertEquals(2, version.getMinor());

    version = Version.of(8);
    assertEquals(0, version.getMinor());
  }

  @Test
  public void testGetMicro() throws Exception
  {
    Version version = Version.of(8, 0, 1);
    assertEquals(1, version.getPatchAsNumber());

    version = Version.of(8, 2);
    assertEquals(0, version.getPatchAsNumber());

    version = Version.of(8);
    assertEquals(0, version.getPatchAsNumber());
  }

  @Test
  public void testIsAtLeast() throws Exception
  {
    Version version1 = Version.of(1);
    Version version10 = Version.of(1, 0);
    Version version11 = Version.of(1, 1);
    Version version111 = Version.of(1, 1, 1);
    Version version112 = Version.of(1, 1, 2);
    Version version20 = Version.of(2, 0);

    assertTrue(version20.isAtLeast(Version.of(1)));
    assertTrue(version20.isAtLeast(Version.of(1, 0)));
    assertTrue(version20.isAtLeast(Version.of(1, 2)));
    assertTrue(version20.isAtLeast(Version.of(1, 2, 1)));
    assertTrue(version20.isAtLeast(Version.of(2)));
    assertTrue(version20.isAtLeast(Version.of(2, 0)));
    assertFalse(version20.isAtLeast(Version.of(2, 1)));

    assertTrue(version112.isAtLeast(Version.of(1, 1, 2)));
    assertTrue(version112.isAtLeast(Version.of(1, 1)));
    assertTrue(version112.isAtLeast(version112));
    assertTrue(version112.isAtLeast(version111));
    assertTrue(version112.isAtLeast(version11));
    assertTrue(version112.isAtLeast(version10));

    assertTrue(version1.isAtLeast(version10));
    assertTrue(version10.isAtLeast(version1));

    assertFalse(version1.isAtLeast(version11));
    assertTrue(version11.isAtLeast(version1));
  }

  @Test
  public void testLessThan() throws Exception
  {
    Version version1 = Version.of(1);
    Version version10 = Version.of(1, 0);
    Version version11 = Version.of(1, 1);
    Version version111 = Version.of(1, 1, 1);
    Version version112 = Version.of(1, 1, 2);
    Version version20 = Version.of(2, 0);

    assertFalse(version20.lessThan(Version.of(1)));
    assertFalse(version20.lessThan(Version.of(1, 0)));
    assertFalse(version20.lessThan(Version.of(1, 2)));
    assertFalse(version20.lessThan(Version.of(1, 2, 1)));
    assertFalse(version20.lessThan(Version.of(2)));
    assertFalse(version20.lessThan(Version.of(2, 0)));
    assertTrue(version20.lessThan(Version.of(2, 1)));
    assertTrue(version10.lessThan(Version.of(2, 1)));
    assertTrue(version10.lessThan(Version.of(1, 1)));

    assertFalse(version112.lessThan(Version.of(1, 1, 2)));
    assertFalse(version112.lessThan(Version.of(1, 1)));
    assertFalse(version112.lessThan(version112));
    assertFalse(version112.lessThan(version111));
    assertFalse(version112.lessThan(version11));
    assertFalse(version112.lessThan(version10));

    assertFalse(version1.lessThan(version10));
    assertFalse(version10.lessThan(version1));

    assertTrue(version1.lessThan(version11));
    assertFalse(version11.lessThan(version1));
  }

  @Test
  public void whenPatchIsMissingItIsDefaultedTo0() {
    Version version1 = Version.parse("2.16.0");
    Version version2 = Version.of(2, 16);
    assertTrue(version1.equals(version2));
    assertTrue(version1.isAtMost(version2));
  }
}