package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class UnitTests {
    private Map<Integer, String> map;

    @BeforeEach
    void upSet(){
        map = new BestMapImplementation<>();
    }

    @Test
    void TestPutAndGet(){
        assertNull(map.put(1, "one"));
        assertEquals("one", map.get(1));
        assertEquals(1, map.size());
    }

    @Test
    void TestSize(){
        assertEquals(0, map.size());
        map.put(1, "one");
        map.put(2, "two");
        assertEquals(2, map.size());
    }

    @Test
    void TestIsEmpty(){
        assertEquals(true, map.isEmpty());
        map.put(1, "one");
        assertEquals(false, map.isEmpty());
    }

    @Test
    void TestContainsKey(){
        map.put(1, "one");
        assertEquals(true, map.containsKey(1));
        assertEquals(false, map.containsKey(2));
    }

    @Test
    void TestContainsValue(){
        map.put(1, "one");
        assertEquals(true, map.containsValue("one"));
        assertEquals(false, map.containsValue("two"));
    }

    @Test
    void TestRemove(){
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        assertEquals("two", map.remove(2));
        assertNull(map.get(2));
        assertEquals(3, map.size());

        assertEquals("one", map.remove(1));
        assertEquals("three", map.remove(3));
        assertEquals("four", map.remove(4));
        assertTrue(map.isEmpty());
    }

    @Test
    void TestPutAll(){
        Map<Integer, String> map2 = new BestMapImplementation<>();
        map2.put(3, "three");
        map2.put(4, "four");

        map.putAll(map2);
        assertEquals(2, map.size());

        map.put(1, "one");
        map.put(2, "two");
        assertEquals(4, map.size());
    }

    @Test
    void TestClear(){
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        assertEquals(4, map.size());

        map.clear();

        assertTrue(map.isEmpty());
    }

    @Test
    void TestKeySet(){
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Set<Integer> set = map.keySet();
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
    }

    @Test
    void TestValues(){
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Collection<String> collection = map.values();
        assertEquals(4, collection.size());
        assertTrue(collection.contains("one"));
        assertTrue(collection.contains("two"));
        assertTrue(collection.contains("three"));
    }

    @Test
    void testEntrySet() {
        map.put(1, "one");
        map.put(2, "two");
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        assertEquals(2, entrySet.size());
    }
}
