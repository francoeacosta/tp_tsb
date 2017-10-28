/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oahashtable;

import hashtable.TSBArrayList;
import hashtable.TSBHashtable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Gonzalo
 */
public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable {

    // ################################## ATRIBUTOS
    // Arreglo de soporte.
    private ArrayList<Map.Entry<K, V>> table;

    // Tamaño máximo.
    private final static int MAX_SIZE = Integer.MAX_VALUE;

    // Tamaño inicial.
    private int initial_capacity;

    // Cantidad de objetos.
    private int count;

    // Factor de carga.
    private float load_factor;

    // Conteo de modificaciones. 
    protected transient int modCount;

    // ################################## CONSTRUCTORES
    /**
     * Crea una tabla vacía, con capacidad para 11 y un factor de
     * 0.8.
     */
    public TSB_OAHashtable() {
        this(0, 0.8f);
    }

    /**
     * Crea una tabla vacía, con la capacidad indicada y con factor
     * de carga igual a 0.8.
     *
     * @param initial_capacity - la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity) {
        this(initial_capacity, 0.8f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado.
     * Si la capacidad inicial indicada por initial_capacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo o cero, se ajustará a 0.8f.
     *
     * @param initial_capacity la capacidad inicial de la tabla.
     * @param load_factor el factor de carga de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity, float load_factor) {
        if (load_factor <= 0) {
            load_factor = 0.8f;
        }
        if (initial_capacity <= 0) {
            initial_capacity = 11;
        } else {
            if (initial_capacity > TSB_OAHashtable.MAX_SIZE) {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            }
        }

        this.table = new ArrayList(initial_capacity);

        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsKey(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsValue(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public V get(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public V put(K k, V v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public V remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Clase privada que maneja una entrada.
     *
     * @param <K>
     * @param <V>
     */
    private class Entry<K, V> implements Map.Entry<K, V>, Serializable {

        private K key;
        private V value;
        private boolean active; // Este atributo es el que señala si hay o no una tumba. 

        public Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
            this.active = true;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }

        public boolean isActive() {
            return this.active;
        }

        public void kill() {
            this.active = false;
        }

        public void revive() {
            this.active = true;
        }
    }

}
