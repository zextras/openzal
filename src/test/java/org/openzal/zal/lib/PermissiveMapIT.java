package org.openzal.zal.lib;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.junit.Assert.*;

/*
  Copied from google guice testing (MapInterfaceTest.java).
  Converted from <K,V> to <String,String> for the sake of simplicity.
  No particular customizations were made.
*/

public class PermissiveMapIT
{
    protected final boolean supportsPut = true;
    protected final boolean supportsRemove = true;
    protected final boolean supportsClear = true;
    protected final boolean allowsNullKeys = true;
    protected final boolean allowsNullValues = true;
    protected final boolean supportsIteratorRemove = true;

    /**
     * Creates a new, empty instance of the class under test.
     *
     * @return a new, empty map instance.
     * @throws UnsupportedOperationException if it's not possible to make an
     * empty instance of the class under test.
     */
    protected Map<String, String> makeEmptyMap()
      throws UnsupportedOperationException
    {
      return new PermissiveMap<String, String>();
    }

    /**
     * Creates a new, non-empty instance of the class under test.
     *
     * @return a new, non-empty map instance.
     * @throws UnsupportedOperationException if it's not possible to make a
     * non-empty instance of the class under test.
     */
    protected Map<String, String> makePopulatedMap()
      throws UnsupportedOperationException
    {
      PermissiveMap map = new PermissiveMap<String, String>();
      map.put("key","value");
      return map;
    }

    /**
     * Creates a new key that is not expected to be found
     * in {@link #makePopulatedMap()}.
     *
     * @return a key.
     * @throws UnsupportedOperationException if it's not possible to make a key
     * that will not be found in the map.
     */
    protected String getKeyNotInPopulatedMap()
      throws UnsupportedOperationException
    {
      return "YouCantFindMe";
    }

    /**
     * Creates a new value that is not expected to be found
     * in {@link #makePopulatedMap()}.
     *
     * @return a value.
     * @throws UnsupportedOperationException if it's not possible to make a value
     * that will not be found in the map.
     */
    protected String getValueNotInPopulatedMap()
      throws UnsupportedOperationException
    {
      return "YouWontFindMe";
    }
  

    /**
     * Used by tests that require a map, but don't care whether it's
     * populated or not.
     *
     * @return a new map instance.
     */
    protected Map<String, String> makeEitherMap() {
      try {
        return makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return makeEmptyMap();
      }
    }

    protected final boolean supportsValuesHashCode(Map<String, String> map) {
      // get the first non-null value
      Collection<String> values = map.values();
      for (String value : values) {
        if (value != null) {
          try {
            value.hashCode();
          } catch (Exception e) {
            return false;
          }
          return true;
        }
      }
      return true;
    }

    /**
     * Checks all the properties that should always hold of a map. Also calls
     * {@link #assertMoreInvariants} to check invariants that are peculiar to
     * specific implementations.
     *
     * @see #assertMoreInvariants
     * @param map the map to check.
     */
    protected final void assertInvariants(Map<String, String> map) {
      Set<String> keySet = map.keySet();
      Collection<String> valueCollection = map.values();
      Set<Map.Entry<String, String>> entrySet = map.entrySet();

      assertEquals(map.size() == 0, map.isEmpty());
      assertEquals(map.size(), keySet.size());
      assertEquals(keySet.size() == 0, keySet.isEmpty());
      assertEquals(!keySet.isEmpty(), keySet.iterator().hasNext());

      int expectedKeySetHash = 0;
      for (String key : keySet) {
        String value = map.get(key);
        expectedKeySetHash += key != null ? key.hashCode() : 0;
        assertTrue(map.containsKey(key));
        assertTrue(map.containsValue(value));
        assertTrue(valueCollection.contains(value));
        assertTrue(valueCollection.containsAll(singleton(value)));
        assertTrue(entrySet.contains(mapEntry(key, value)));
        assertTrue(allowsNullKeys || (key != null));
      }
      assertEquals(expectedKeySetHash, keySet.hashCode());

      assertEquals(map.size(), valueCollection.size());
      assertEquals(valueCollection.size() == 0, valueCollection.isEmpty());
      assertEquals(
        !valueCollection.isEmpty(), valueCollection.iterator().hasNext());
      for (String value : valueCollection) {
        assertTrue(map.containsValue(value));
        assertTrue(allowsNullValues || (value != null));
      }

      assertEquals(map.size(), entrySet.size());
      assertEquals(entrySet.size() == 0, entrySet.isEmpty());
      assertEquals(!entrySet.isEmpty(), entrySet.iterator().hasNext());
      assertFalse(entrySet.contains("foo"));

      boolean supportsValuesHashCode = supportsValuesHashCode(map);
      if (supportsValuesHashCode) {
        int expectedEntrySetHash = 0;
        for (Map.Entry<String, String> entry : entrySet) {
          assertTrue(map.containsKey(entry.getKey()));
          assertTrue(map.containsValue(entry.getValue()));
          int expectedHash =
            (entry.getKey() == null ? 0 : entry.getKey().hashCode()) ^
              (entry.getValue() == null ? 0 : entry.getValue().hashCode());
          assertEquals(expectedHash, entry.hashCode());
          expectedEntrySetHash += expectedHash;
        }
        assertEquals(expectedEntrySetHash, entrySet.hashCode());
        assertTrue(entrySet.containsAll(new HashSet<Map.Entry<String, String>>(entrySet)));
        assertTrue(entrySet.equals(new HashSet<Map.Entry<String, String>>(entrySet)));
      }

      Object[] entrySetToArray1 = entrySet.toArray();
      assertEquals(map.size(), entrySetToArray1.length);
      assertTrue(Arrays.asList(entrySetToArray1).containsAll(entrySet));

      Map.Entry<?, ?>[] entrySetToArray2 = new Map.Entry<?, ?>[map.size() + 2];
      entrySetToArray2[map.size()] = mapEntry("foo", "1");
      assertSame(entrySetToArray2, entrySet.toArray(entrySetToArray2));
      assertNull(entrySetToArray2[map.size()]);
      assertTrue(Arrays.asList(entrySetToArray2).containsAll(entrySet));

      Object[] valuesToArray1 = valueCollection.toArray();
      assertEquals(map.size(), valuesToArray1.length);
      assertTrue(Arrays.asList(valuesToArray1).containsAll(valueCollection));

      Object[] valuesToArray2 = new Object[map.size() + 2];
      valuesToArray2[map.size()] = "foo";
      assertSame(valuesToArray2, valueCollection.toArray(valuesToArray2));
      assertNull(valuesToArray2[map.size()]);
      assertTrue(Arrays.asList(valuesToArray2).containsAll(valueCollection));

      if (supportsValuesHashCode) {
        int expectedHash = 0;
        for (Map.Entry<String, String> entry : entrySet) {
          expectedHash += entry.hashCode();
        }
        assertEquals(expectedHash, map.hashCode());
      }

      assertMoreInvariants(map);
    }

    /**
     * Override this to check invariants which should hold true for a particular
     * implementation, but which are not generally applicable to every instance
     * of Map.
     *
     * @param map the map whose additional invariants to check.
     */
    protected void assertMoreInvariants(Map<String, String> map) {
    }

   @Test
   public void testClear() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      if (supportsClear) {
        map.clear();
        assertTrue(map.isEmpty());
      } else {
        try {
          map.clear();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testContainsKey() {
      final Map<String, String> map;
      final String unmappedKey;
      try {
        map = makePopulatedMap();
        unmappedKey = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertFalse(map.containsKey(unmappedKey));
      assertTrue(map.containsKey(map.keySet().iterator().next()));
      if (allowsNullKeys) {
        map.containsKey(null);
      } else {
        try {
          map.containsKey(null);
        } catch (NullPointerException optional) {
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testContainsValue() {
      final Map<String, String> map;
      final String unmappedValue;
      try {
        map = makePopulatedMap();
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertFalse(map.containsValue(unmappedValue));
      assertTrue(map.containsValue(map.values().iterator().next()));
      if (allowsNullValues) {
        map.containsValue(null);
      } else {
        try {
          map.containsKey(null);
        } catch (NullPointerException optional) {
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySet() {
      final Map<String, String> map;
      final Set<Map.Entry<String, String>> entrySet;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);

      entrySet = map.entrySet();
      final String unmappedKey;
      final String unmappedValue;
      try {
        unmappedKey = getKeyNotInPopulatedMap();
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      for (Map.Entry<String, String> entry : entrySet) {
        assertFalse(unmappedKey.equals(entry.getKey()));
        assertFalse(unmappedValue.equals(entry.getValue()));
      }
    }

   @Test 
   public void testEntrySetForEmptyMap() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetContainsEntryNullKeyPresent() {
      if (!allowsNullKeys || !supportsPut) {
        return;
      }
      final Map<String, String> map;
      final Set<Map.Entry<String, String>> entrySet;
      try {
        map = makeEitherMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);

      entrySet = map.entrySet();
      final String unmappedValue;
      try {
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      map.put(null, unmappedValue);
      Map.Entry<String, String> entry = mapEntry(null, unmappedValue);
      assertTrue(entrySet.contains(entry));
      assertFalse(entrySet.contains(mapEntry(null, null)));
    }

   @Test 
   public void testEntrySetContainsEntryNullKeyMissing() {
      final Map<String, String> map;
      final Set<Map.Entry<String, String>> entrySet;
      try {
        map = makeEitherMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);

      entrySet = map.entrySet();
      final String unmappedValue;
      try {
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      Map.Entry<String, String> entry = mapEntry(null, unmappedValue);
      assertFalse(entrySet.contains(entry));
      assertFalse(entrySet.contains(mapEntry(null, null)));
    }

   @Test 
   public void testEntrySetIteratorRemove() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
      if (supportsIteratorRemove) {
        int initialSize = map.size();
        Map.Entry<String, String> entry = iterator.next();
        iterator.remove();
        assertEquals(initialSize - 1, map.size());
        assertFalse(entrySet.contains(entry));
        assertInvariants(map);
        try {
          iterator.remove();
          fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
          // Expected.
        }
      } else {
        try {
          iterator.next();
          iterator.remove();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemove() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      if (supportsRemove) {
        int initialSize = map.size();
        boolean didRemove = entrySet.remove(entrySet.iterator().next());
        assertTrue(didRemove);
        assertEquals(initialSize - 1, map.size());
      } else {
        try {
          entrySet.remove(entrySet.iterator().next());
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemoveMissingKey() {
      final Map<String, String> map;
      final String key;
      try {
        map = makeEitherMap();
        key = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Map.Entry<String, String> entry
        = mapEntry(key, getValueNotInPopulatedMap());
      int initialSize = map.size();
      if (supportsRemove) {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } else {
        try {
          boolean didRemove = entrySet.remove(entry);
          assertFalse(didRemove);
        } catch (UnsupportedOperationException optional) {}
      }
      assertEquals(initialSize, map.size());
      assertFalse(map.containsKey(key));
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemoveDifferentValue() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      String key = map.keySet().iterator().next();
      Map.Entry<String, String> entry
        = mapEntry(key, getValueNotInPopulatedMap());
      int initialSize = map.size();
      if (supportsRemove) {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } else {
        try {
          boolean didRemove = entrySet.remove(entry);
          assertFalse(didRemove);
        } catch (UnsupportedOperationException optional) {}
      }
      assertEquals(initialSize, map.size());
      assertTrue(map.containsKey(key));
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemoveNullKeyPresent() {
      if (!allowsNullKeys || !supportsPut || !supportsRemove) {
        return;
      }
      final Map<String, String> map;
      final Set<Map.Entry<String, String>> entrySet;
      try {
        map = makeEitherMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);

      entrySet = map.entrySet();
      final String unmappedValue;
      try {
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      map.put(null, unmappedValue);
      assertEquals(unmappedValue, map.get(null));
      assertTrue(map.containsKey(null));
      Map.Entry<String, String> entry = mapEntry(null, unmappedValue);
      assertTrue(entrySet.remove(entry));
      assertNull(map.get(null));
      assertFalse(map.containsKey(null));
    }

   @Test 
   public void testEntrySetRemoveNullKeyMissing() {
      final Map<String, String> map;
      try {
        map = makeEitherMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Map.Entry<String, String> entry
        = mapEntry(null, getValueNotInPopulatedMap());
      int initialSize = map.size();
      if (supportsRemove) {
        boolean didRemove = entrySet.remove(entry);
        assertFalse(didRemove);
      } else {
        try {
          boolean didRemove = entrySet.remove(entry);
          assertFalse(didRemove);
        } catch (UnsupportedOperationException optional) {}
      }
      assertEquals(initialSize, map.size());
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemoveAll() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Set<Map.Entry<String, String>> entriesToRemove =
        singleton(entrySet.iterator().next());
      if (supportsRemove) {
        int initialSize = map.size();
        boolean didRemove = entrySet.removeAll(entriesToRemove);
        assertTrue(didRemove);
        assertEquals(initialSize - entriesToRemove.size(), map.size());
        for (Map.Entry<String, String> entry : entriesToRemove) {
          assertFalse(entrySet.contains(entry));
        }
      } else {
        try {
          entrySet.removeAll(entriesToRemove);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRemoveAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      if (supportsRemove) {
        try {
          entrySet.removeAll(null);
          fail("Expected NullPointerException.");
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          entrySet.removeAll(null);
          fail("Expected UnsupportedOperationException or NullPointerException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRetainAll() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Set<Map.Entry<String, String>> entriesToRetain =
        singleton(entrySet.iterator().next());
      if (supportsRemove) {
        boolean shouldRemove = (entrySet.size() > entriesToRetain.size());
        boolean didRemove = entrySet.retainAll(entriesToRetain);
        assertEquals(shouldRemove, didRemove);
        assertEquals(entriesToRetain.size(), map.size());
        for (Map.Entry<String, String> entry : entriesToRetain) {
          assertTrue(entrySet.contains(entry));
        }
      } else {
        try {
          entrySet.retainAll(entriesToRetain);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetRetainAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      if (supportsRemove) {
        try {
          entrySet.retainAll(null);
          // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          entrySet.retainAll(null);
          // We have to tolerate a successful return (Sun bug 4802647)
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetClear() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      if (supportsClear) {
        entrySet.clear();
        assertTrue(entrySet.isEmpty());
      } else {
        try {
          entrySet.clear();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetAddAndAddAll() {
      final Map<String, String> map = makeEitherMap();

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      final Map.Entry<String, String> entryToAdd = mapEntry(null, null);
      try {
        entrySet.add(entryToAdd);
        fail("Expected UnsupportedOperationException or NullPointerException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      } catch (NullPointerException e) {
        // Expected.
      }
      assertInvariants(map);

      try {
        entrySet.addAll(singleton(entryToAdd));
        fail("Expected UnsupportedOperationException or NullPointerException.");
      } catch (UnsupportedOperationException e) {
        // Expected.
      } catch (NullPointerException e) {
        // Expected.
      }
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetSetValue() {
      // TODO: Investigate the extent to which, in practice, maps that support
      // put() also support Entry.setValue().
      if (!supportsPut) {
        return;
      }

      final Map<String, String> map;
      final String valueToSet;
      try {
        map = makePopulatedMap();
        valueToSet = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Map.Entry<String, String> entry = entrySet.iterator().next();
      final String oldValue = entry.getValue();
      final String returnedValue = entry.setValue(valueToSet);
      assertEquals(oldValue, returnedValue);
      assertTrue(entrySet.contains(
                   mapEntry(entry.getKey(), valueToSet)));
      assertEquals(valueToSet, map.get(entry.getKey()));
      assertInvariants(map);
    }

   @Test 
   public void testEntrySetSetValueSameValue() {
      // TODO: Investigate the extent to which, in practice, maps that support
      // put() also support Entry.setValue().
      if (!supportsPut) {
        return;
      }

      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<Map.Entry<String, String>> entrySet = map.entrySet();
      Map.Entry<String, String> entry = entrySet.iterator().next();
      final String oldValue = entry.getValue();
      final String returnedValue = entry.setValue(oldValue);
      assertEquals(oldValue, returnedValue);
      assertTrue(entrySet.contains(
                   mapEntry(entry.getKey(), oldValue)));
      assertEquals(oldValue, map.get(entry.getKey()));
      assertInvariants(map);
    }

   @Test 
   public void testEqualsForEqualMap() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      assertEquals(map, map);
      assertEquals(makePopulatedMap(), map);
      assertFalse(map.equals(Collections.emptyMap()));
      //no-inspection ObjectEqualsNull
      assertFalse(map.equals(null));
    }

   @Test 
   public void testEqualsForLargerMap() {
      if (!supportsPut) {
        return;
      }

      final Map<String, String> map;
      final Map<String, String> largerMap;
      try {
        map = makePopulatedMap();
        largerMap = makePopulatedMap();
        largerMap.put(getKeyNotInPopulatedMap(), getValueNotInPopulatedMap());
      } catch (UnsupportedOperationException e) {
        return;
      }

      assertFalse(map.equals(largerMap));
    }

   @Test 
   public void testEqualsForSmallerMap() {
      if (!supportsRemove) {
        return;
      }

      final Map<String, String> map;
      final Map<String, String> smallerMap;
      try {
        map = makePopulatedMap();
        smallerMap = makePopulatedMap();
        smallerMap.remove(smallerMap.keySet().iterator().next());
      } catch (UnsupportedOperationException e) {
        return;
      }

      assertFalse(map.equals(smallerMap));
    }

   @Test 
   public void testEqualsForEmptyMap() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      assertEquals(map, map);
      assertEquals(makeEmptyMap(), map);
      assertEquals(Collections.emptyMap(), map);
      assertFalse(map.equals(Collections.emptySet()));
      //noinspection ObjectEqualsNull
      assertFalse(map.equals(null));
    }

   @Test 
   public void testGet() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      for (Map.Entry<String, String> entry : map.entrySet()) {
        assertEquals(entry.getValue(), map.get(entry.getKey()));
      }

      String unmappedKey = null;
      try {
        unmappedKey = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertNull(map.get(unmappedKey));
    }

   @Test 
   public void testGetForEmptyMap() {
      final Map<String, String> map;
      String unmappedKey = null;
      try {
        map = makeEmptyMap();
        unmappedKey = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertNull(map.get(unmappedKey));
    }

   @Test 
   public void testGetNull() {
      Map<String, String> map = makeEitherMap();
      if (allowsNullKeys) {
        if (allowsNullValues) {
          // TODO: decide what to test here.
        } else {
          assertEquals(map.containsKey(null), map.get(null) != null);
        }
      } else {
        try {
          map.get(null);
        } catch (NullPointerException optional) {
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testHashCode() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);
    }

   @Test 
   public void testHashCodeForEmptyMap() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutNewKey() {
      final Map<String, String> map = makeEitherMap();
      final String keyToPut;
      final String valueToPut;
      try {
        keyToPut = getKeyNotInPopulatedMap();
        valueToPut = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      if (supportsPut) {
        int initialSize = map.size();
        String oldValue = map.put(keyToPut, valueToPut);
        assertEquals(valueToPut, map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(valueToPut));
        assertEquals(initialSize + 1, map.size());
        assertNull(oldValue);
      } else {
        try {
          map.put(keyToPut, valueToPut);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutExistingKey() {
      final Map<String, String> map;
      final String keyToPut;
      final String valueToPut;
      try {
        map = makePopulatedMap();
        valueToPut = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      keyToPut = map.keySet().iterator().next();
      if (supportsPut) {
        int initialSize = map.size();
        map.put(keyToPut, valueToPut);
        assertEquals(valueToPut, map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(valueToPut));
        assertEquals(initialSize, map.size());
      } else {
        try {
          map.put(keyToPut, valueToPut);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutNullKey() {
      if (!supportsPut) {
        return;
      }
      final Map<String, String> map = makeEitherMap();
      final String valueToPut;
      try {
        valueToPut = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      if (allowsNullKeys) {
        final String oldValue = map.get(null);
        final String returnedValue = map.put(null, valueToPut);
        assertEquals(oldValue, returnedValue);
        assertEquals(valueToPut, map.get(null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(valueToPut));
      } else {
        try {
          map.put(null, valueToPut);
          fail("Expected RuntimeException");
        } catch (RuntimeException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutNullValue() {
      if (!supportsPut) {
        return;
      }
      final Map<String, String> map = makeEitherMap();
      final String keyToPut;
      try {
        keyToPut = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      if (allowsNullValues) {
        int initialSize = map.size();
        final String oldValue = map.get(keyToPut);
        final String returnedValue = map.put(keyToPut, null);
        assertEquals(oldValue, returnedValue);
        assertNull(map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(null));
        assertEquals(initialSize + 1, map.size());
      } else {
        try {
          map.put(keyToPut, null);
          fail("Expected RuntimeException");
        } catch (RuntimeException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutNullValueForExistingKey() {
      if (!supportsPut) {
        return;
      }
      final Map<String, String> map;
      final String keyToPut;
      try {
        map = makePopulatedMap();
        keyToPut = map.keySet().iterator().next();
      } catch (UnsupportedOperationException e) {
        return;
      }
      if (allowsNullValues) {
        int initialSize = map.size();
        final String oldValue = map.get(keyToPut);
        final String returnedValue = map.put(keyToPut, null);
        assertEquals(oldValue, returnedValue);
        assertNull(map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(null));
        assertEquals(initialSize, map.size());
      } else {
        try {
          map.put(keyToPut, null);
          fail("Expected RuntimeException");
        } catch (RuntimeException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutAllNewKey() {
      final Map<String, String> map = makeEitherMap();
      final String keyToPut;
      final String valueToPut;
      try {
        keyToPut = getKeyNotInPopulatedMap();
        valueToPut = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      final Map<String, String> mapToPut = Collections.singletonMap(keyToPut, valueToPut);
      if (supportsPut) {
        int initialSize = map.size();
        map.putAll(mapToPut);
        assertEquals(valueToPut, map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(valueToPut));
        assertEquals(initialSize + 1, map.size());
      } else {
        try {
          map.putAll(mapToPut);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testPutAllExistingKey() {
      final Map<String, String> map;
      final String keyToPut;
      final String valueToPut;
      try {
        map = makePopulatedMap();
        valueToPut = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      keyToPut = map.keySet().iterator().next();
      final Map<String, String> mapToPut = Collections.singletonMap(keyToPut, valueToPut);
      int initialSize = map.size();
      if (supportsPut) {
        map.putAll(mapToPut);
        assertEquals(valueToPut, map.get(keyToPut));
        assertTrue(map.containsKey(keyToPut));
        assertTrue(map.containsValue(valueToPut));
      } else {
        try {
          map.putAll(mapToPut);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertEquals(initialSize, map.size());
      assertInvariants(map);
    }

   @Test 
   public void testRemove() {
      final Map<String, String> map;
      final String keyToRemove;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      keyToRemove = map.keySet().iterator().next();
      if (supportsRemove) {
        int initialSize = map.size();
        String expectedValue = map.get(keyToRemove);
        String oldValue = map.remove(keyToRemove);
        assertEquals(expectedValue, oldValue);
        assertFalse(map.containsKey(keyToRemove));
        assertEquals(initialSize - 1, map.size());
      } else {
        try {
          map.remove(keyToRemove);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testRemoveMissingKey() {
      final Map<String, String> map;
      final String keyToRemove;
      try {
        map = makePopulatedMap();
        keyToRemove = getKeyNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      if (supportsRemove) {
        int initialSize = map.size();
        assertNull(map.remove(keyToRemove));
        assertEquals(initialSize, map.size());
      } else {
        try {
          map.remove(keyToRemove);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testSize() {
      assertInvariants(makeEitherMap());
    }

   @Test 
   public void testKeySetClear() {
      final Map<String, String> map;
      try {
        map = makeEitherMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<String> keySet = map.keySet();
      if (supportsClear) {
        keySet.clear();
        assertTrue(keySet.isEmpty());
      } else {
        try {
          keySet.clear();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testKeySetRemoveAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<String> keySet = map.keySet();
      if (supportsRemove) {
        try {
          keySet.removeAll(null);
          fail("Expected NullPointerException.");
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          keySet.removeAll(null);
          fail("Expected UnsupportedOperationException or NullPointerException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testKeySetRetainAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Set<String> keySet = map.keySet();
      if (supportsRemove) {
        try {
          keySet.retainAll(null);
          // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          keySet.retainAll(null);
          // We have to tolerate a successful return (Sun bug 4802647)
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValues() {
      final Map<String, String> map;
      final Collection<String> valueCollection;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      assertInvariants(map);

      valueCollection = map.values();
      final String unmappedValue;
      try {
        unmappedValue = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }
      for (String value : valueCollection) {
        assertFalse(unmappedValue.equals(value));
      }
    }

   @Test 
   public void testValuesIteratorRemove() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      Iterator<String> iterator = valueCollection.iterator();
      if (supportsIteratorRemove) {
        int initialSize = map.size();
        iterator.next();
        iterator.remove();
        assertEquals(initialSize - 1, map.size());
        // (We can't assert that the values collection no longer contains the
        // removed value, because the underlying map can have multiple mappings
        // to the same value.)
        assertInvariants(map);
        try {
          iterator.remove();
          fail("Expected IllegalStateException.");
        } catch (IllegalStateException e) {
          // Expected.
        }
      } else {
        try {
          iterator.next();
          iterator.remove();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesRemove() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      if (supportsRemove) {
        int initialSize = map.size();
        valueCollection.remove(valueCollection.iterator().next());
        assertEquals(initialSize - 1, map.size());
        // (We can't assert that the values collection no longer contains the
        // removed value, because the underlying map can have multiple mappings
        // to the same value.)
      } else {
        try {
          valueCollection.remove(valueCollection.iterator().next());
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesRemoveMissing() {
      final Map<String, String> map;
      final String valueToRemove;
      try {
        map = makeEitherMap();
        valueToRemove = getValueNotInPopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      int initialSize = map.size();
      if (supportsRemove) {
        assertFalse(valueCollection.remove(valueToRemove));
      } else {
        try {
          assertFalse(valueCollection.remove(valueToRemove));
        } catch (UnsupportedOperationException e) {
          // Tolerated.
        }
      }
      assertEquals(initialSize, map.size());
      assertInvariants(map);
    }

   @Test 
   public void testValuesRemoveAll() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      Set<String> valuesToRemove = singleton(valueCollection.iterator().next());
      if (supportsRemove) {
        valueCollection.removeAll(valuesToRemove);
        for (String value : valuesToRemove) {
          assertFalse(valueCollection.contains(value));
        }
        for (String value : valueCollection) {
          assertFalse(valuesToRemove.contains(value));
        }
      } else {
        try {
          valueCollection.removeAll(valuesToRemove);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesRemoveAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> values = map.values();
      if (supportsRemove) {
        try {
          values.removeAll(null);
          // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          values.removeAll(null);
          // We have to tolerate a successful return (Sun bug 4802647)
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesRetainAll() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      Set<String> valuesToRetain = singleton(valueCollection.iterator().next());
      if (supportsRemove) {
        valueCollection.retainAll(valuesToRetain);
        for (String value : valuesToRetain) {
          assertTrue(valueCollection.contains(value));
        }
        for (String value : valueCollection) {
          assertTrue(valuesToRetain.contains(value));
        }
      } else {
        try {
          valueCollection.retainAll(valuesToRetain);
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesRetainAllNullFromEmpty() {
      final Map<String, String> map;
      try {
        map = makeEmptyMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> values = map.values();
      if (supportsRemove) {
        try {
          values.retainAll(null);
          // Returning successfully is not ideal, but tolerated.
        } catch (NullPointerException e) {
          // Expected.
        }
      } else {
        try {
          values.retainAll(null);
          // We have to tolerate a successful return (Sun bug 4802647)
        } catch (UnsupportedOperationException e) {
          // Expected.
        } catch (NullPointerException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

   @Test 
   public void testValuesClear() {
      final Map<String, String> map;
      try {
        map = makePopulatedMap();
      } catch (UnsupportedOperationException e) {
        return;
      }

      Collection<String> valueCollection = map.values();
      if (supportsClear) {
        valueCollection.clear();
        assertTrue(valueCollection.isEmpty());
      } else {
        try {
          valueCollection.clear();
          fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
          // Expected.
        }
      }
      assertInvariants(map);
    }

    private static Map.Entry<String, String> mapEntry(String key, String value) {
      return Collections.singletonMap(key, value).entrySet().iterator().next();
    }
}