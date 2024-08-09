package org.example;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class  BestMapImplementation<K, V> implements Map<K, V> {
    private static class Note<K, V>{
        final K key;
        V value;
        Note(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private static final int START_SIZE = 16;
    private List<Note<K, V>>[] particles;
    private int size;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    BestMapImplementation(){
        particles = new List[START_SIZE];
        for (int i = 0; i < particles.length; i++){
            particles[i] = new LinkedList<>();
        }
        size = 0;
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return size;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return size == 0;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        lock.readLock().lock();
        try{
            int index = hash(key);
            for(Note<K, V> note : particles[index]){
                if(Objects.equals(note.key, key)){
                    return true;
                }
            }
            return false;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        lock.readLock().lock();
        try{
            for(List<Note<K, V>> list : particles){
                for (Note<K, V> note : list){
                    if(Objects.equals(note.value, value))
                        return true;
                }
            }
            return false;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        lock.readLock().lock();
        try{
            int hashIndex = hash(key);
            for(Note<K, V> note : particles[hashIndex]){
                if (Objects.equals(note.key, key)){
                    return note.value;
                }
            }
            return null;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    private void resize(){ // увеличиваем размерность таблицы
        lock.writeLock().lock();
        try {
            List<Note<K, V>>[] newParticles = new List[particles.length * 2];

            for(int i = 0; i < newParticles.length; i++){
                newParticles[i] = new LinkedList<>();
            }

            for (List<Note<K, V>> list : particles){
                for (Note<K, V> note : list){
                    int newIndex = (note.key == null) ? 0 : Math.abs(note.key.hashCode() % newParticles.length);
                    newParticles[newIndex].add(note);
                }
            }

            particles = newParticles;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (size > particles.length * 0.75){
                resize();
            }

            int index = hash(key);
            for (Note<K, V> note : particles[index]){
                if (Objects.equals(note.key, key)){
                    V oldValue = note.value;
                    note.value = value;
                    return oldValue;
                }
            }
            particles[index].add(new Note<>(key, value));
            size++;
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {
        lock.writeLock().lock();
        try {
            int hashIndex = hash(key);
            Iterator<Note<K, V>> iterator = particles[hashIndex].iterator();

            while (iterator.hasNext()){
                Note<K, V> note = iterator.next();
                if (Objects.equals(note.key, key)){
                    V value = note.value;
                    iterator.remove();
                    size--;
                    return  value;
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        lock.writeLock().lock();
        try{
            for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()){
                put(entry.getKey(), entry.getValue());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            for(List<Note<K, V>> list : particles){
                list.clear();
            }
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        lock.readLock().lock();
        try {
            Set<K> keySet = new LightweightSet<>();
            for (List<Note<K, V>> list : particles){
                for (Note<K, V> note : list){
                    keySet.add(note.key);
                }
            }
            return keySet;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<V> values() {
        lock.readLock().lock();
        try {
            List<V> values = new ArrayList<>();
            for (List<Note<K, V>> list : particles){
                for (Note<K, V> note : list){
                    values.add(note.value);
                }
            }
            return values;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        lock.readLock().lock();
        try {
            Set<Map.Entry<K, V>> entrySet = new LightweightEntrySet<>();
            for (List<Note<K, V>> list : particles){
                for (Note<K, V> note : list){
                    entrySet.add(new AbstractMap.SimpleEntry<>(note.key, note.value));
                }
            }
            return entrySet;
        } finally {
            lock.readLock().unlock();
        }
    }


    private int hash(Object key){
        return (key == null) ? 0 : Math.abs(key.hashCode() % particles.length);
    }
}

class LightweightSet<E> extends AbstractSet<E> { // мини версия HashSet для хранения ключей
    protected  final List<E> elements = new ArrayList<>();

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public boolean add(E e) {
        if (!contains(e)) {
            elements.add(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public void clear() {
        elements.clear();
    }
}

class LightweightEntrySet<K, V> extends LightweightSet<Map.Entry<K, V>> { // мини версия HashSet для хранения пар ключ-значение

    @Override
    public boolean contains(Object o) {
        if (o instanceof Map.Entry<?, ?> entry) {
            for (Map.Entry<K, V> e : elements) {
                if (Objects.equals(e.getKey(), entry.getKey()) &&
                        Objects.equals(e.getValue(), entry.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(Map.Entry<K, V> e) {
        for (Map.Entry<K, V> existingEntry : elements) {
            if (Objects.equals(existingEntry.getKey(), e.getKey())) {
                existingEntry.setValue(e.getValue());
                return true;
            }
        }
        return super.add(e);
    }
}