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
