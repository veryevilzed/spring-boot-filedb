package ru.veryevilzed.tools.tests;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ru.veryevilzed.tools.exceptions.KeyNotFoundException;
import ru.veryevilzed.tools.utils.SortedComparableList;
import ru.veryevilzed.tools.utils.SortedComparableTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class SortedComparableListTest {

    private SortedComparableList<Integer> list;

    @Before
    public void create() {
        list = new SortedComparableList<>();
        list.add(10);
        list.add(100);
        list.add(50);
        list.add(75);
        list.add(20);
    }

    @Test
    public void testException()  {

        try {
            list.get(99, SortedComparableTypes.EqualTo);
            assertTrue(false);
        }catch (KeyNotFoundException ignored) {
            assertTrue(true);
        }

    }

    @Test
    public void testComparatorEquals() throws KeyNotFoundException {

        assertEquals((int) list.get(10, SortedComparableTypes.EqualTo), 10);
        assertNull(list.get(11, SortedComparableTypes.EqualTo, null));
    }

    @Test
    public void testComparatorGreaterThan() throws KeyNotFoundException {

        assertEquals((int) list.get(19, SortedComparableTypes.GreaterThan), 20);
        assertEquals((int) list.get(20, SortedComparableTypes.GreaterThan), 50);
        assertNull(list.get(100, SortedComparableTypes.GreaterThan, null));

    }

    @Test
    public void testComparatorGreaterThanEqual() throws KeyNotFoundException {
        assertEquals((int) list.get(20, SortedComparableTypes.GreaterThanOrEqualTo, null), 20);
        assertEquals((int) list.get(19, SortedComparableTypes.GreaterThanOrEqualTo, null), 20);
        assertNull(list.get(101, SortedComparableTypes.GreaterThanOrEqualTo, null));

    }

    @Test
    public void testComparatorLessThan() {
        assertEquals((int) list.get(75, SortedComparableTypes.LessThan, null), 50);
        assertEquals((int) list.get(76, SortedComparableTypes.LessThan, null), 75);
    }

    @Test
    public void testComparatorLessThanEqual() {
        assertEquals((int)list.get(75, SortedComparableTypes.LessThanOrEqualTo, null), 75);
        assertEquals((int)list.get(76, SortedComparableTypes.LessThanOrEqualTo, null), 75);
        assertNull(list.get(9, SortedComparableTypes.LessThanOrEqualTo, null));
        assertNull(list.get(5, SortedComparableTypes.LessThanOrEqualTo, null));
    }

    @Test
    public void testDelete() {
        list.add(33);
        assertEquals((int)list.get(33, SortedComparableTypes.EqualTo, null), 33);
        list.remove((Object)33);
        assertEquals((int)list.get(33, SortedComparableTypes.EqualTo, 0), 0);
    }


    @Test
    public void testMany() {

        assertEquals(list.getAll(50, SortedComparableTypes.GreaterThan, null).size(), 2);
        assertEquals(list.getAll(100, SortedComparableTypes.GreaterThan, null).size(), 1);
        assertNull(list.getAll(100, SortedComparableTypes.GreaterThan, null).iterator().next());
    }


    @Test
    public void testMaps() {
        Map<Integer, String> map = new HashMap<Integer, String>() {
            @Override
            public String put(Integer key, String value) {
                log.info("Put!");
                return super.put(key, value);
            }
        };

        assertNull(map.putIfAbsent(2, "Hello"));
        assertEquals("Hello", map.putIfAbsent(2, "Bellow"));
        assertEquals("Hello", map.putIfAbsent(2, "Bellow"));

    }


}
