package org.openzal.zal;

import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RedisClientHelper
{
  @Nullable
  String put(@NotNull String scope, @NotNull String key, String value);
  @Nullable
  String get(@NotNull String scope, @NotNull String key, String def);
  @Nullable
  String get(@NotNull String scope, @NotNull String key);
  Set<Map.Entry<String, String>> getByScope(@NotNull String scope);
  void remove(@NotNull String scope, @NotNull String key);
}
