package org.openzal.zal.lib;

import java.util.Objects;
import java.util.Optional;

public class Version implements Comparable<Version> {

  private final int major;
  private final Optional<Integer> minor;
  private final Optional<String> patch;

  public static Version of(int major) {
    return new Version(major, Optional.empty(), Optional.empty());
  }

  public static Version of(int major, int minor) {
    return new Version(major, Optional.of(minor), Optional.empty());
  }

  public static Version of(int major, int minor, int patch) {
    return new Version(major, Optional.of(minor), Optional.of(String.valueOf(patch)));
  }

  public static Version of(int major, int minor, int patch, String releaseType) {
    return new Version(major, Optional.of(minor), Optional.of(String.format("%s-%s", patch, releaseType)));
  }

  public static Version parse(String v) {
    int i0 = v.indexOf('.');
    if (i0 == -1) {
      return new Version(Integer.parseInt(v), Optional.empty(), Optional.empty());
    } else {
      int i1 = v.indexOf('.', i0+1);
      int major = Integer.parseInt(v.substring(0, i0));
      if (i1 == -1) {
        return new Version(major, Optional.of(Integer.parseInt(v.substring(i0+1))), Optional.empty());
      } else {
        String patch = v.substring(i1 + 1);
        return new Version(major, Optional.of(Integer.parseInt(v.substring(i0+1, i1))), patch.length() > 0 ? Optional.of(patch) : Optional.empty());
      }
    }
  }

  protected Version(int major, Optional<Integer> minor, Optional<String> patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  public int getMajor() {
    return major;
  }

  public int getMinor() {
    return minor.orElse(0);
  }

  public Optional<String> getPatch() {
    return patch;
  }

  public int getPatchAsNumber() {
    if (!patch.isPresent()) return 0;
    String ps = patch.get();
    StringBuilder digits = new StringBuilder();
    for (int i = 0; i < ps.length(); i++) {
      char ch = ps.charAt(i);
      if (Character.isDigit(ch)) {
        digits.append(ch);
      }
    }
    if (digits.length() == 0) {
      return 0;
    } else {
      return Integer.valueOf(digits.toString());
    }
  }

  public Version withPatch(Optional<String> p) {
    return new Version(major, minor, p);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Version)) {
      return false;
    }
    return compareTo((Version) o) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMajor(), getMinor(), getPatch());
  }

  static <A extends Comparable<A>> int compareOpt(Optional<A> a1, Optional<A> a2) {
    if (a1.isPresent() && a2.isPresent()) {
      return a1.get().compareTo(a2.get());
    } else if (!a1.isPresent() && !a2.isPresent()) {
      return 0;
    } else if (a1.isPresent()) {
      return 1;
    } else {
      return -1;
    }
  }

  @Override
  public int compareTo(Version o) {
    int r = Integer.compare(major, o.getMajor());
    if (r == 0) {
      r = Integer.compare(getMinor(), o.getMinor());
      if (r == 0) {
        r = getPatch().orElse("0").compareTo(o.getPatch().orElse("0"));
      }
    }
    return r;
  }

  public boolean isAtLeast(Version version)
  {
    return compareTo(version) >= 0;
  }

  public boolean isAtMost(Version version)
  {
    return compareTo(version) <= 0;
  }

  public boolean lessThan(Version version) {
    return compareTo(version) < 0;
  }

  @Override
  public String toString() {
    if (!minor.isPresent()) {
      return String.valueOf(major);
    } else {
      if (patch.isPresent()) {
        return String.format("%s.%s.%s", getMajor(), getMinor(), getPatch().get());
      } else {
        return String.format("%s.%s", getMajor(), getMinor());
      }
    }
  }
}
