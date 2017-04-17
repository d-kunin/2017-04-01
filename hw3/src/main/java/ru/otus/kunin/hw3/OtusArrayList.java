package ru.otus.kunin.hw3;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;


public class OtusArrayList<T> implements List<T>, RandomAccess {

    private final static int DEFAULT_CAPACITY = 8;
    private final static int GROWTH_FACTOR = 2;

    private Object[] data;
    private int size;
    private int revision;

    public OtusArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public OtusArrayList(int capacity) {
        data = new Object[capacity];
        size = 0;
    }

    /**
     * Increases capacity if needed
     */
    private void grow(int expectedSize) {
        if (expectedSize < data.length) {
            return;
        }
        data = Arrays.copyOf(data, Math.max(expectedSize, DEFAULT_CAPACITY) * GROWTH_FACTOR);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new OtusListIterator(0);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new NotImplementedException();
    }

    @Override
    public boolean add(T t) {
        grow(size + 1);
        data[size++] = t;
        revision++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        final int index = indexOf(o);
        if (index == -1) {
            return false;
        }
        remove(index);
        revision++;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c.isEmpty()) {
            return false;
        }

        grow(size + c.size());
        final Object[] newData = c.toArray();
        final int newDataLength = newData.length;
        System.arraycopy(newData, 0, data, size, newDataLength);
        size += newDataLength;
        revision++;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        indexCheck(index);

        if (c.isEmpty()) {
            return false;
        }

        grow(size + c.size());
        final Object[] newData = c.toArray();
        final int newDataLength = newData.length;

        // Shift old data
        System.arraycopy(data, index, data, index + newDataLength, size - index);
        // Insert new data
        System.arraycopy(newData, 0, data, index, newDataLength);
        size += newDataLength;
        revision++;
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        int heaven = 0;
        for (int i = 0; i < size; i++) {
            final Object e = data[i];
            if (!c.contains(e)) {
                data[heaven] = e;
                heaven++;
            }
        }
        for (int i = heaven; i < size; i++) {
            data[i] = null;
        }
        final int newSize = heaven;
        final boolean modified = size != newSize;
        size = newSize;
        if (modified) {
            revision++;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.isEmpty()) {
            if (isEmpty()) {
                return false;
            } else {
                clear();
                return true;
            }
        }

        int heaven = 0;
        for (int i = 0; i < size; i++) {
            final Object e = data[i];
            if (c.contains(e)) {
                data[heaven] = e;
                heaven++;
            }
        }
        for (int i = heaven; i < size; i++) {
            data[i] = null;
        }
        final int newSize = heaven;
        final boolean modified = size != newSize;
        size = newSize;
        if (modified) {
            revision++;
        }
        return modified;
    }

    @Override
    public void clear() {
        Arrays.fill(data, null);
        size = 0;
        revision++;
    }

    @Override
    public T get(int index) {
        indexCheck(index);
        return (T) data[index];
    }

    @Override
    public T set(int index, T element) {
        indexCheck(index);
        final T tmp = (T) data[index];
        data[index] = element;
        revision++;
        return tmp;
    }

    @Override
    public void add(int index, T element) {
        indexCheck(index);
        grow(size + 1);

        // Shift old data
        System.arraycopy(data, index, data, index + 1, size - index);
        // Insert new data
        System.arraycopy(new Object[]{element}, 0, data, index, 1);
        size++;
        revision++;
    }

    @Override
    public T remove(int index) {
        indexCheck(index);
        final T tmp = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        revision++;
        return tmp;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(o, data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(o, data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new OtusListIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new OtusListIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new NotImplementedException();
    }

    private void indexCheck(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("Can't address index " + index + " when size is " + size);
        }
    }

    private class OtusListIterator implements ListIterator<T> {

        int expectedRevision = revision;
        int position = 0;

        // next +1
        // prev -1
        // else  0
        int lastMove = 0;

        public OtusListIterator(int index) {
            this.position = index;
        }

        @Override
        public boolean hasNext() {
            checkRevision();
            return position < size;
        }

        @Override
        public T next() {
            checkRevision();
            checkHasNext();
            lastMove = 1;
            return (T) data[position++];
        }

        @Override
        public boolean hasPrevious() {
            checkRevision();
            return position >= 1;
        }

        @Override
        public T previous() {
            checkRevision();
            checkHasPrev();
            lastMove = -1;
            return (T) data[--position];
        }

        @Override
        public int nextIndex() {
            checkRevision();
            checkHasNext();
            return position;
        }

        @Override
        public int previousIndex() {
            checkRevision();
            checkHasPrev();
            return position - 1;
        }

        @Override
        public void remove() {
            checkRevision();
            if (lastMove == 0) {
                throw new IllegalStateException();
            }

            OtusArrayList.this.remove(position - lastMove);
            position--;
            expectedRevision = revision;
            lastMove = 0;
        }

        @Override
        public void set(T t) {
            checkRevision();
            if (lastMove == 0) {
                throw new IllegalStateException();
            }
            OtusArrayList.this.set(position - lastMove, t);
            expectedRevision = revision;
        }

        @Override
        public void add(T t) {
            checkRevision();
            lastMove = 0;
            throw new NotImplementedException();
        }

        private void checkRevision() {
            if (expectedRevision != revision) {
                throw new ConcurrentModificationException("Expected " + expectedRevision
                        + " was " + revision);
            }
        }

        private void checkHasNext() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
        }

        private void checkHasPrev() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
        }
     }
}
