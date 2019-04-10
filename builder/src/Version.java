package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manage versions, string only
 * Simplified version from the generic implementation
 */
@SuppressWarnings("Duplicates")
public class Version implements Comparable<Version> {
  private final int[] mVersionParts;

  public Version(String version) throws NumberFormatException {
    final List<Integer> versionParts = new ArrayList<Integer>(3);
    for (String part : version.split("\\.")) {
      versionParts.add(Integer.valueOf(part));
    }
    final int size = versionParts.size();
    mVersionParts = new int[versionParts.size()];
    for (int i = 0; i < size; i++) {
      mVersionParts[i] = versionParts.get(i);
    }
  }

  public int compareTo(Version version) {
    int maxSize = Math.max(mVersionParts.length, version.mVersionParts.length);
    for (int i = 0; i < maxSize; i++) {
      int thisPart = getPartValue(i);
      int otherPart = version.getPartValue(i);
      if (otherPart > thisPart) {
        return -1;
      }
      if (otherPart < thisPart) {
        return 1;
      }
    }
    return 0;
  }

  private int getPartValue(int partIndex) {
    return partIndex < mVersionParts.length ? mVersionParts[partIndex] : 0;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder(8);

    for(int i = 0 ; i < mVersionParts.length - 1 ; i++)
    {
      sb.append(mVersionParts[i]);
      sb.append('.');
    }

    sb.append(mVersionParts[mVersionParts.length-1]);

    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Version version = (Version) o;
    return Arrays.equals(mVersionParts, version.mVersionParts);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(mVersionParts);
  }

  public int getMicro() {
    return mVersionParts[2];
  }
}
