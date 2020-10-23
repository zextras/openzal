import java.util.Objects;

public class Zimbra implements Comparable<Zimbra>
{
  public Type getType()
  {
    return mType;
  }

  public Version getVersion()
  {
    return mVersion;
  }

  @Override
  public int compareTo(Zimbra zimbra)
  {
    if( mType == Type.x && zimbra.mType == Type.standard )
    {
      return 1;
    }
    if( mType == Type.standard && zimbra.mType == Type.x )
    {
      return -1;
    }
    return mVersion.compareTo(zimbra.mVersion);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Zimbra zimbra = (Zimbra) o;
    return mType == zimbra.mType &&
      mVersion.equals(zimbra.mVersion);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(mType, mVersion);
  }


  private final Type mType;
  private final Version mVersion;

  enum Type
  {
    standard,
    x
  }

  @Override
  public String toString()
  {
    if( mType == Type.x )
    {
      return "x";
    }
    else
    {
      return mVersion.toString();
    }
  }

  public Zimbra(Type type, Version version)
  {
    mType = type;
    mVersion = version;
  }
}
