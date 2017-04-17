package ru.otus.kunin.hw3;

import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class BlackBoxListTest {

    private List<List<Long>> blackBoxOfLists = Arrays.asList(
            new ArrayList<>(1),
            new LinkedList<>(),
            new OtusArrayList<>(1)
    );

    /*
        Otus tests for addAll(), copy(), sort()
     */

    @Test
    public void otusAddAll() throws Exception {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            Collections.addAll(list, 4l, 5l, 6l);

            assertEquals(6, list.size());
            for (int i = 1; i <= 6; i++) {
                assertEquals(i, (long) list.get(i - 1));
            }
        });
    }

    @Test
    public void otusCopyTo() throws Exception {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            Collections.copy(list, Lists.newArrayList(100l, 200l));

            assertEquals(3, list.size());
            assertEquals(100l, (long)list.get(0));
            assertEquals(200l, (long)list.get(1));
            assertEquals(3l, (long)list.get(2));
        });
    }

    @Test
    public void otusCopyFrom() throws Exception {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            final ArrayList<Long> dest = Lists.newArrayList(100l, 200l, 300l, 400l);
            Collections.copy(dest, list);

            assertEquals(4, dest.size());
            assertEquals(1l, (long)dest.get(0));
            assertEquals(2l, (long)dest.get(1));
            assertEquals(3l, (long)dest.get(2));
            assertEquals(400l, (long)dest.get(3));
        });
    }

    @Test
    public void otusSort() throws Exception {
        final Random random = new Random(100);
        for (int arraySize = 1; arraySize < 100; arraySize += 10) {
            final long[] randomArray = new long[arraySize];
            for (int i = 0; i < randomArray.length; i++) {
                randomArray[i] = random.nextLong();
            }
            final long[] sortedArray = Arrays.copyOf(randomArray, randomArray.length);
            Arrays.sort(sortedArray);

            blackBoxOfLists.forEach(list -> {
                list.clear();
                for (long l : randomArray) {
                    list.add(l);
                }
                for (int i = 0; i < randomArray.length; i++) {
                    assertEquals(randomArray[i], (long)list.get(i));
                }

                Collections.sort(list);

                for (int i = 0; i < sortedArray.length; i++) {
                    assertEquals(sortedArray[i], (long)list.get(i));
                }
            });
        }
    }

    /*
        General List tests
     */

    @Test
    public void size() {
        blackBoxOfLists.forEach(list -> {
            assertEquals(0, list.size());
            list.add(1l);
            assertEquals(1, list.size());
            list.add(2l);
            assertEquals(2, list.size());
        });
    }

    @Test
    public void isEmpty() {
        blackBoxOfLists.forEach(list -> {
            assertTrue(list.isEmpty());
            list.add(1l);
            assertFalse(list.isEmpty());
            list.remove(1l);
            assertTrue(list.isEmpty());
            list.add(1l);
            assertFalse(list.isEmpty());
            list.clear();
            assertTrue(list.isEmpty());
        });
    }

    @Test
    public void contains() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);

            assertTrue(list.contains(1l));
            assertTrue(list.contains(2l));
            assertTrue(list.contains(3l));

            assertFalse(list.contains(0l));
            assertFalse(list.contains(Long.MIN_VALUE));
            assertFalse(list.contains(Long.MAX_VALUE));
        });
    }

    @Test
    public void iterator() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            assertEquals(3, list.size());
            final Iterator<Long> iterator = list.iterator();
            for (long i = 1; i <= 3; ++i) {
                assertTrue(iterator.hasNext());
                assertEquals(i, (long) iterator.next());
                if (i == 2) {
                    iterator.remove();
                }
            }
            assertEquals(2, list.size());
            assertTrue(list.contains(1l));
            assertFalse(list.contains(2l));
            assertTrue(list.contains(3l));
        });
    }

    @Test
    public void toArray() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            final Object[] array = list.toArray();
            assertEquals(3, array.length);
            for (int i = 0; i < list.size(); i++) {
                assertEquals(list.get(i), array[i]);
            }
            // Verify array is a copy
            array[0] = 99l;
            assertNotEquals(99l, (long) list.get(0));
        });
    }

    @Test
    public void add() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);

            assertEquals(1l, (long) list.get(0));
            assertEquals(2l, (long) list.get(1));
            assertEquals(3l, (long) list.get(2));
        });
    }

    @Test
    public void removeByReference() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.remove(2l);

            assertEquals(2l, list.size());
            assertEquals(1l, (long) list.get(0));
            assertEquals(3l, (long) list.get(1));
        });
    }

    @Test
    public void containsAll() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);

            assertTrue(list.containsAll(Lists.newArrayList(3l, 2l, 1l)));
            assertTrue(list.containsAll(Lists.newArrayList(3l, 2l)));
            assertTrue(list.containsAll(Lists.newArrayList(3l)));
            assertFalse(list.containsAll(Lists.newArrayList(4l, 3l, 2l, 1l)));
        });
    }

    @Test
    public void addAll() {
        blackBoxOfLists.forEach(list -> {
            list.addAll(Lists.newArrayList(1l, 2l));

            assertEquals(2, list.size());

            assertEquals(1l, (long) list.get(0));
            assertEquals(2l, (long) list.get(1));

            assertTrue(list.contains(1l));
            assertTrue(list.contains(2l));
        });
    }

    @Test
    public void addAllWithIndex() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.addAll(1, Lists.newArrayList(100l, 200l));

            assertEquals(5, list.size());
            assertEquals(1l, (long) list.get(0));
            assertEquals(100l, (long) list.get(1));
            assertEquals(200l, (long) list.get(2));
            assertEquals(2l, (long) list.get(3));
            assertEquals(3l, (long) list.get(4));
        });
    }

    @Test
    public void removeAll() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.removeAll(Lists.newArrayList(1l, 3l));

            assertEquals(1, list.size());
            assertEquals(2l, (long) list.get(0));
        });
    }

    @Test
    public void retainAll() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.retainAll(Lists.newArrayList(2l, 3l, 4l));

            assertEquals(2, list.size());
            assertEquals(2l, (long) list.get(0));
            assertEquals(3l, (long) list.get(1));
        });
    }

    @Test
    public void clear() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.clear();

            assertTrue(list.isEmpty());
        });
    }

    @Test
    public void get() {
        blackBoxOfLists.forEach(list -> {
            addRange(-3, -1, list);

            assertEquals(-3l, (long) list.get(0));
            assertEquals(-2l, (long) list.get(1));
            assertEquals(-1l, (long) list.get(2));
        });
    }

    @Test
    public void set() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            list.set(0, 100l);
            list.set(2, 200l);

            assertEquals(100l, (long) list.get(0));
            assertEquals(2l, (long) list.get(1));
            assertEquals(200l, (long) list.get(2));
        });
    }

    @Test
    public void addWithIndex() {
        blackBoxOfLists.forEach(list -> {
            addRange(-3, -1, list);
            list.add(1, 300l);

            assertEquals(-3l, (long) list.get(0));
            assertEquals(300l, (long) list.get(1));
            assertEquals(-2l, (long) list.get(2));
            assertEquals(-1l, (long) list.get(3));

        });
    }

    @Test
    public void removeByIndex() {
        blackBoxOfLists.forEach(list -> {
            addRange(-3l, -1l, list);
            list.remove(1);

            assertEquals(2, list.size());
            assertEquals(-3l, (long) list.get(0));
            assertEquals(-1l, (long) list.get(1));
        });
    }

    @Test
    public void indexOf() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            addRange(1, 3, list);

            for (long i = 1l; i <= 3; i++) {
                assertEquals(i - 1, list.indexOf(i));
            }
            assertEquals(-1, list.indexOf(42l));
        });
    }

    @Test
    public void lastIndexOf() {
        blackBoxOfLists.forEach(list -> {
            addRange(1, 3, list);
            addRange(1, 3, list);

            for (long i = 1l; i <= 3; i++) {
                /* +3 since we expect the last index */
                assertEquals(i - 1 + 3, list.lastIndexOf(i));
            }
            assertEquals(-1, list.indexOf(42l));
        });
    }

    @Test
    @Ignore
    public void toArrayWithInputArray() {
        fail("not implemented");
    }

    @Test
    @Ignore
    public void listIterator() {
        fail("not implemented");
    }

    @Test
    @Ignore
    public void listIterator1() {
        fail("not implemented");
    }

    @Test
    @Ignore
    public void subList() {
        fail("not implemented");
    }

    void addRange(long fromInclusive, long toInclusive, List<Long> list) {
        for (long i = fromInclusive; i <= toInclusive; ++i) {
            list.add(i);
        }
    }


}