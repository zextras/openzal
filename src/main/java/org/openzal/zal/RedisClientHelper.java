package org.openzal.zal;

import javax.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class RedisClientHelper
{
  public RedisClientHelper()
  {
  }

  @Nullable
  public Set<String> putInSet(@Nonnull String key, String value)
  {
    throw new UnsupportedOperationException();
  }

  public Set<String> getSet(@Nonnull String key)
  {
    throw new UnsupportedOperationException();
  }

  public void removeFromSet(@Nonnull String key, @Nonnull String value)
  {
    throw new UnsupportedOperationException();
  }

  @Nullable
  public String put(@Nonnull String key, String value)
  {
    throw new UnsupportedOperationException();
  }

  @Nullable
  public String get(@Nonnull String key)
  {
    throw new UnsupportedOperationException();
  }

  public boolean contains(@Nonnull String key)
  {
    throw new UnsupportedOperationException();
  }

  public void remove(@Nonnull String key)
  {
    throw new UnsupportedOperationException();
  }

  public Collection<String> getByPattern(@Nonnull String keyStart, @Nonnull String keyEnd)
  {
    return Collections.emptyList();
  }

  private void publishOnTopic(@Nonnull String key)
  {
    throw new UnsupportedOperationException();
  }
}