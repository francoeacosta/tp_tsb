/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oahashtable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Gonzalo
 */
public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable {

    // Arreglo de soporte.
    private Map.Entry<K, V> table[];

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

    // Atributos para gestionar la vista. 
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

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

        this.table = new Map.Entry[initial_capacity];

        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }

    @Override
    public boolean containsKey(Object o) {
        return (this.get((K) o) != null);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.contains(o);
    }

    @Override
    public V get(Object k) {
        /**
         * Se opera la funcion hash sobre la clave k, y se obtiene
         * la direccion madre.
         */
        int mi = this.h((K) k);

        /**
         * Se define aux, como la entrada auxiliar en la exploracion.
         */
        Entry<K, V> aux;

        /**
         * Se define una variable de iteracion que comienza
         * desde la direccion madre.
         */
        int i = mi;

        /**
         * Comienza la exploracion cuadratica.
         */
        for (int j = 0;; j++) {

            /**
             * Se incrementa el puntero segun la formula
             * i + j^2, si el puntero se pasa del largo del
             * array, se sigue de manera circular.
             */
            i += Math.pow(j, 2);
            if (i >= table.length) {
                i -= table.length;
            }

            /**
             * 1) Si la casilla esta abierta, se retorna null.
             * 2) Si no es una tumba y es la key que se paso por
             * parametro, se devuelve el valor v.
             */
            if (isOpen(i)) {
                return null;
            } else {
                aux = (Entry) table[i];

                if (!isTombstone(i) && k.equals(aux.getKey())) {
                    return aux.getValue();
                }
            }
        }

    }

    @Override
    public V put(K k, V v) {
        if (k == null || v == null) {
            throw new NullPointerException("Los parametros no pueden ser null.");
        }

        /**
         * Si la tabla hash ya contiene el valor se retorna null.
         */
        if (contains(v)) {
            return null;
        }

        /**
         * Se opera la funcion hash sobre la clave k, y se obtiene
         * la direccion madre.
         */
        int mi = this.h(k);

        /**
         * Se define el valor old, para el valor a retornar.
         * Se define aux, como la entrada auxiliar en la exploracion.
         * Se define toPut, como la entrada a ser agregada.
         */
        V old = null;
        Entry<K, V> aux;
        Entry<K, V> toPut = new Entry(k, v);

        /**
         * Se define una variable de iteracion que comienza
         * desde la direccion madre.
         */
        int i = mi;

        /**
         * Comienza la exploracion cuadratica.
         */
        for (int j = 0;; j++) {

            /**
             * Se incrementa el puntero segun la formula
             * i + j^2, si el puntero se pasa del largo del
             * array, se sigue de manera circular.
             */
            i = h(mi + (int) Math.pow(j, 2));

            /**
             * 1) Si la posicion analizada esta abierta, se la inserta ahi.
             * 2) Si la posicion analizada es una tumba y es igual a la
             * insertada, se la vuelve a activar.
             * 3) Si no es una tumba, se analiza si es la misma key, y en ese
             * caso se cambia el valor.
             */
            if (isOpen(i)) {
                // esta abierta.
                table[i] = new Entry(k, v);
                break;

            } else {
                aux = (Entry) table[i];

                if (isTombstone(i) && aux.equals(toPut)) {
                    // es una tumba y es igual a la entrada a agregar.
                    aux.revive();
                    break;
                } else if (k.equals(aux.getKey())) {
                    // no es una tumba, y es el mismo key. 
                    old = aux.getValue();
                    aux.setValue(v);
                }
            }
        }
        return old;
    }

    @Override
    public V remove(Object k) {

        if (k == null) {
            throw new NullPointerException("El parámetro no puede ser null");
        }

        /**
         * Se opera la funcion hash sobre la clave k, y se obtiene
         * la direccion madre.
         */
        int mi = this.h((K) k);

        /**
         * Se define el valor old, para el valor a retornar.
         * Se define aux, como la entrada auxiliar en la exploracion.
         */
        V old = null;
        Entry<K, V> aux;

        /**
         * Se define una variable de iteracion que comienza
         * desde la direccion madre.
         */
        int i = mi;

        /**
         * Comienza la exploracion cuadratica.
         */
        for (int j = 0;; j++) {

            /**
             * Se incrementa el puntero segun la formula
             * i + j^2, si el puntero se pasa del largo del
             * array, se sigue de manera circular.
             */
            i = h(mi + (int) Math.pow(j, 2));

            /**
             * 1) Si la casilla esta abierta, se retorna null.
             * 2) Si no es una tumba y es la key que se paso por
             * parametro, se marca a la entrada como tumba.
             */
            if (isOpen(i)) {
                return null;
            } else {
                aux = (Entry) table[i];

                if (!isTombstone(i) && k.equals(aux.getKey())) {
                    // La key es igual a la pasada por parametro.
                    old = aux.getValue();
                    aux.kill();
                    break;
                }
            }
        }
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        this.table = new Map.Entry[this.initial_capacity];
        this.count = 0;
        this.modCount++;
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            // keySet = Collections.synchronizedSet(new KeySet()); 
            keySet = new KeySet();
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            // entrySet = Collections.synchronizedSet(new EntrySet()); 
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    // ################################## FUNCIONES HASH
    private int h(int k) {
        return h(k, this.table.length);
    }

    private int h(K key) {
        return h(key.hashCode(), this.table.length);
    }

    private int h(K key, int t) {
        return h(key.hashCode(), t);
    }

    private int h(int k, int t) {
        if (k < 0) {
            k *= -1;
        }
        return k % t;
    }

    // ################################## REDEFINICION DE LOS METODOS DE OBJECT
    @Override
    public Object clone() throws CloneNotSupportedException{
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>)super.clone();
        t.table = new Map.Entry[table.length];
        t.putAll(this);
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }
    // ################################## METODOS ADICIONALES 
    public boolean contains(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.     
    }

    protected void rehash() {

    }

    private boolean isOpen(int index) {
        return (table[index] == null);

    }

    private boolean isTombstone(int index) {
        if (isOpen(index)) {
            return false;
        }
        return !((Entry) table[index]).isActive();
    }

    private boolean isClose(int index) {
        if (isOpen(index)) {
            return false;
        }
        return ((Entry) table[index]).isActive();
    }

    // ################################## CLASES PRIVADAS
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

    private class KeySet extends AbstractSet<K> {

        private class KeySetIterator implements Iterator<K> {

            private int actual;
            private boolean next_ok;
            Map.Entry<K, V> t[];

            public KeySetIterator() {
                actual = -1;
                next_ok = false;
                t = TSB_OAHashtable.this.table;
            }

            @Override
            public boolean hasNext() {

                if (count == 0) {
                    return false;
                }
                int i = actual;
                do {
                    i++;

                } while (i < t.length && !isClose(i));

                return !(t.length == i);

            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan mas elementos.");
                }
                do {
                    actual++;

                } while (actual < t.length && !isClose(actual));
                next_ok = true;
                return t[actual].getKey();
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()");
                }
                ((Entry) t[actual]).kill();
                next_ok = false;
                TSB_OAHashtable.this.count--;
            }

        }

        @Override
        public Iterator<K> iterator() {
            return null;
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class ValueCollection extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
