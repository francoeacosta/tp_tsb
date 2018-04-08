package oahashtable;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Clase TSB_OAHashtable (por TSB Open Addressing Hash Table). Representa
 * una Tabla Hash pero con estrategia de Direccionamiento Abierto para la
 * resolución de colisiones.
 *
 * @author Gonzalo
 * @param <K>
 * @param <V>
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
    private double load_factor;

    // Conteo de modificaciones. 
    protected transient int modCount;

    // Atributos para gestionar la vista. 
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

    /*
    ################################### CONSTRUCTORES
     */
    /**
     * Crea una tabla vacía, con capacidad para 11 y un factor de
     * 0.5.
     */
    public TSB_OAHashtable() {
        this(0, 0.5);
    }

    /**
     * Crea una tabla vacía, con la capacidad indicada y con factor
     * de carga igual a 0.5.
     *
     * @param initial_capacity - la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity) {
        this(initial_capacity, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado.
     * Si la capacidad inicial indicada por initial_capacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo o cero, se ajustará a 0.8f.
     *
     * @param initial_capacity - la capacidad inicial de la tabla.
     * @param load_factor - el factor de carga de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity, double load_factor) {
        if (load_factor <= 0) {
            load_factor = 0.5f;
        }
        if (initial_capacity <= 0) {
            initial_capacity = 11;
        } else {
            if (initial_capacity > TSB_OAHashtable.MAX_SIZE) {
                initial_capacity = TSB_OAHashtable.MAX_SIZE;
            } else if (!esPrimo(initial_capacity)) {
                initial_capacity = siguientePrimo(initial_capacity);
            }
        }

        this.table = new Map.Entry[initial_capacity];

        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * @param t el Map a partir del cual se creará la tabla.
     */     
    public TSB_OAHashtable(Map<? extends K,? extends V> t)
    {
        this(0, 0.5);
        this.putAll(t);
    }
    /*
    ################################### METODOS DE LA INTERFAZ MAP
     */
    /**
     * Indica la cantidad de elementos de la tabla.
     *
     * @return - la cantidad de elementos de la tabla.
     */
    @Override
    public int size() {
        return this.count;
    }

    /**
     * Indica si la tabla esta vacia.
     *
     * @return - true, si la tabla esta vacia.
     */
    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }

    /**
     * Indica si una clave dada esta presente. *
     *
     * @param key - La clave a analizar.
     * @return - true, si esta presente la clave.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     */
    @Override
    public boolean containsKey(Object key) throws ClassCastException, NullPointerException {

        return (this.get((K)key) != null);
    }

    /**
     * Indica si un valor esta presente en la tabla.
     *
     * @param o - el valor a ser analizado.
     * @return - true, si el valor esta presente.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     */
    @Override
    public boolean containsValue(Object o) throws ClassCastException, NullPointerException {
        return this.contains(o);
    }

    /**
     * Retorna el valor asociado a una clave.
     *
     * @param k - la clave.
     * @return - el valor asociado si existe, null si no.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     */
    @Override
    public V get(Object k) throws ClassCastException, NullPointerException {

        if (k == null) {
            throw new NullPointerException("Los parametros no pueden ser null.");
        }

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
         * Se define una variable de iteracion.
         */
        int i;

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

    /**
     * Incorpora un nuevo set (k,v) dentro del hashtable.
     *
     * a) Si la clave no existia, lo inserta y retorna null.
     * b) Si la clave existia, se reemplaza el valor asociado
     * con el valor pasado por parametro y se lo retorna.
     *
     * @param k - la clave a ser insertada.
     * @param v - el valor a ser asociado.
     * @return - null, si el valor ya existe o se inserta correctamente, el
     * valor anterior si la clave existia.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     *
     *
     */
    @Override
    public V put(K k, V v) throws ClassCastException, NullPointerException {

        if (k == null || v == null) {
            throw new NullPointerException("Los parametros no pueden ser null.");
        }

        /**
         * Se verifica el nivel de carga de la tabla.
         * Si es muy grande se hace reHash, antes de calcular el
         * hash del nuevo objeto a agregar.
         */
        if (tableOverloaded()) {
            rehash();
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
         * Se define una variable de iteracion.
         */
        int i;

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
                /**
                 * La posicion esta abierta.
                 */

                // Se inserta la entrada. 
                table[i] = toPut;

                // Se aumenta el contador y el contador de modificaciones. 
                count++;
                modCount++;
                break;

            } else {
                aux = (Entry) table[i];

                if (isTombstone(i) && aux.equals(toPut)) {
                    // es una tumba y es igual a la entrada a agregar.
                    aux.revive();
                    count++;
                    modCount++;
                    break;
                } else if (k.equals(aux.getKey())) {
                    // no es una tumba, y es el mismo key. 
                    old = aux.getValue();
                    aux.setValue(v);
                    break;
                }
            }
        }
        return old;
    }

    /**
     * Se elimina la entrada que pertenece a una clave.
     * Si encuentra elimina y retorna el valor viejo.
     * Si no la encuentra, retorna null.
     *
     * @param k - la clave.
     * @return - el valor viejo si lo encuentra, null si no.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     */
    @Override
    public V remove(Object k) throws ClassCastException, NullPointerException {

        if (k == null) {
            throw new NullPointerException("El parámetro no puede ser null");
        }

        /**
         * Se verifica que se contenga la clave.
         * Si no, devuelve null.
         */
        if (!containsKey((K) k)) {
            return null;
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
         * Se define una variable de iteracion.
         */
        int i;

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
                    count--;
                    modCount++;
                    break;
                }
            }
        }
        return old;
    }

    /**
     * Se insertan todas las entradas de otro mapa.
     *
     * @param map - el mapa que contiene las entradas a agregar.
     * @throws ClassCastException - si el tipo de la clave no es compatible con
     * el mapa.
     * @throws NullPointerException - si la clave es null.
     *
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) throws ClassCastException, NullPointerException {

        if (map == null) {
            throw new NullPointerException("El parámetro no puede ser null");
        }

        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Limpia todo el contenido del hashtable.
     */
    @Override
    public void clear() {
        this.table = new Map.Entry[this.initial_capacity];
        this.count = 0;
        this.modCount++;
    }

    /**
     * Maneja la instancia de la vista de claves.
     *
     * @return - una vista de claves.
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            // keySet = Collections.synchronizedSet(new KeySet()); 
            keySet = new KeySet();
        }
        return keySet;
    }

    /**
     * Maneja la instancia de la vista de valores.
     *
     * @return - una vista de valores.
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values;
    }

    /**
     * Maneja la instancia de la vista de entradas.
     *
     * @return - una vista de entradas.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            // entrySet = Collections.synchronizedSet(new EntrySet()); 
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    /*
    ################################### FUNCIONES HASH
     */
    /**
     * Funcion hash, que toma un numero int como parametro.
     *
     * @param k - clave int.
     * @return - valor hash.
     */
    private int h(int k) {
        return h(k, this.table.length);
    }

    /**
     * Funcion hash, que toma una clave (tipo K) por parametro.
     *
     * @param key - clave K.
     * @return - valor hash.
     */
    private int h(K key) {
        return h(key.hashCode(), this.table.length);
    }

    /**
     * Function hash, que toma una clave (tipo K) y otro valor (t) pensado
     * para el tamaño de la tabla, para calcular la funcion.
     *
     * @param key - clave K.
     * @param t - valor para realizar el modulo (%t).
     * @return - valor hash.
     */
    private int h(K key, int t) {
        return h(key.hashCode(), t);
    }

    /**
     * Function hash, que dos enteros, k y t, y realiza la operacion de modulo
     * entre k y t. Esta es la definicion base de la funcion hash.
     *
     * @param k - entoro que representa la clave.
     * @param t - entero que representa el largo de la tabla.
     * @return - valor hash.
     */
    private int h(int k, int t) {
        if (k < 0) {
            k *= -1;
        }
        return k % t;
    }

    /*
    ################################## REDEFINICION DE LOS METODOS DE OBJECT
     */
    /**
     * Realiza una copia de la hashtable.
     *
     * @return - una copia de la tabla.
     * @throws CloneNotSupportedException - si algun elemento de la jerarquia
     * no soporta la clonacion.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>) super.clone();
        t.table = new Map.Entry[table.length];
        t.putAll(this);
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        t.count = this.count;
        return t;
    }

    /**
     * Calcula el hashcode de la hashtable actual.
     *
     * @return - valor hash.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            hash += entry.hashCode();
        }
        return hash;
    }

    /**
     * Indica si un objeto pasado por parametro es igual
     * a la hashtable actual.
     *
     * @param obj - objeto a comparar.
     * @return - true, si es igual.
     */
    @Override
    public boolean equals(Object obj) {

        // Si apuntan a la misma intancia, retorna true. 
        if (this == obj) {
            return true;
        }

        // Si el objeto que entra por parametro, retorna false. 
        if (obj == null) {
            return false;
        }

        // Si el objeto no es de la misma clase, retorna false.
        if (getClass() != obj.getClass()) {
            return false;
        }

        // Es de la misma clse, se hace un casting explicito. 
        final TSB_OAHashtable<?, ?> other = (TSB_OAHashtable<?, ?>) obj;

        // Si no tienen la misma cantidad de elementos, retorna false.
        if (other.size() != this.size()) {
            return false;
        }

        /**
         * Se verifica si la hashtable que se pasa por parametro tiene todos
         * los elementos que tiene la actual.
         */
        try {
            Iterator<Map.Entry<K, V>> i = this.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (other.get(key) == null) {
                    return false;
                } else {
                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }

        return true;

    }

    /**
     * Muestra un string representando a toda la tabla
     * y sus entradas.
     *
     * @return - string de la tabla.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < this.table.length; i++) {
            str.append("\nLista ")
                    .append(i)
                    .append(":\n\t")
                    .append(this.table[i].toString());
        }
        return str.toString();
    }

    /*
    ################################## METODOS ADICIONALES 
     */
    /**
     * Indica si un valor se encuentra en la tabla.
     *
     * @param value - valor a ser analizado.
     * @return - true, si esta en la tabla.
     */
    public boolean contains(Object value) throws NullPointerException {

        if (value == null) {
            throw new NullPointerException("El parametro no puede ser null.");
        }

        for (V val : values()) {
            if (val.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Realiza el redimensionamiento de la tabla, insertando nuevamente
     * las entradas.
     */
    protected void rehash() {

        // Se encuentra el proximo tamaño primo. 
        int newSize = siguientePrimo(table.length);

        // Se crea la nueva tabla. 
        Map.Entry<K, V> newTable[] = new Map.Entry[newSize];

        /**
         * 1) Se crea un nuevo hashtable auxiliar para hacer
         * mas sencillo el procedimiento.
         * 2) Dicho hashtable auxiliar, tendra como tabla a la
         * tabla vieja.
         * 3) Se asigna la nueva tabla como la tabla
         * del hashtable actual.
         * 4) Se setea count en 0, para poder usar el metodo putall() y que
         * no se modifique erroneamente la cantidad.
         * 5) Se utiliza el metodo putAll() en el hashtable
         * actual, haciendo que se ingresen todos los
         * del auxiliar.
         */
        // Paso 1)
        TSB_OAHashtable<K, V> t = new TSB_OAHashtable();

        // Paso 2)
        t.table = table;
        t.count = this.count;

        // Paso 3)
        this.table = newTable;

        // Paso 4)
        this.count = 0;

        // Paso 5)
        putAll(t);
        System.out.println("rehashing.....");

    }

    /**
     * Calcula el siguiente primo de un numero dado.
     * Se utiliza para manejar los tamaños de las tablas
     * y asi lograr que la exploracion cuadratica sea efectiva.
     *
     * @param n - numero a calcular el primo.
     * @return - el siguiente numero primo.
     */
    @SuppressWarnings("empty-statement")
    private int siguientePrimo(int n) {
        n++;
        for (; !esPrimo(n); n++) ;
        return n;
    }

    /**
     * Indica si un numero es primo.
     *
     * @param n - el numero a analizar.
     * @return - true, si es primo.
     */
    private boolean esPrimo(int n) {
        if (n < 2) {
            return false;
        }
        for (int p = 2; p <= Math.sqrt(n); p++) {
            if (n % p == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indica si la tabla sobrepasa el factor de
     * carga indicado.
     *
     * @return - true, si la tabla sobrepasa el valor de carga.
     */
    private boolean tableOverloaded() {
        return ((double) count / (double) table.length) > load_factor;
    }

    /**
     * Indica si un determinado indice de la tabla esta abierto (open).
     *
     * @param index - indice a analizar.
     * @return - true, si esta abierto.
     */
    private boolean isOpen(int index) {
        return (table[index] == null);

    }

    /**
     * Indica si un determinado indice de la tabla es una tumba (tombstone).
     *
     * @param index - indice a analizar.
     * @return - true, si es una tumba.
     */
    private boolean isTombstone(int index) {
        if (isOpen(index)) {
            return false;
        }
        return !((Entry) table[index]).isActive();
    }

    /**
     * Indica si un determinado indice de la tabla esta cerrado (closed)
     *
     * @param index - indice a analizar.
     * @return - true, si esta cerrado.
     */
    private boolean isClose(int index) {
        if (isOpen(index)) {
            return false;
        }
        return ((Entry) table[index]).isActive();
    }

    /*
    ################################## CLASES PRIVADAS
     */
    private class Entry<K, V> implements Map.Entry<K, V>, Serializable {

        private K key;
        private V value;
        private boolean active; // Este atributo es el que señala si hay o no una tumba. 

        /**
         * Crea una entrada de la tabla hashtable.
         *
         * @param key - clave de la entrada.
         * @param value - valor de la entrada.
         */
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
        public V setValue(V value) throws IllegalArgumentException {
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

        /**
         * Indica si la entrada esta activa, es decir que no es una tumba.
         *
         * @return - true, si no es una tumba.
         */
        public boolean isActive() {
            return this.active;
        }

        /**
         * Manda a una tumba a la entrada.
         */
        public void kill() {
            this.active = false;
        }

        /**
         * Revive a una entrada desde la tumba.
         */
        public void revive() {
            this.active = true;
        }
    }

    private class KeySet extends AbstractSet<K> {

        private class KeySetIterator implements Iterator<K> {

            private int previo;
            private int actual;
            private int expectedModCount;
            private boolean next_ok;
            Map.Entry<K, V> t[];

            public KeySetIterator() {
                actual = -1;
                previo = -1;
                expectedModCount = TSB_OAHashtable.this.modCount;
                next_ok = false;
                t = TSB_OAHashtable.this.table;
            }

            @Override
            public boolean hasNext() {

                if (count == 0) {
                    return false;
                }

                /**
                 * Se crea variable auxiliar para la iteracion.
                 */
                int i = actual;

                /**
                 * Se incrementa la variable hasta en contrar
                 * una casilla cerrada o llegar a la ultima casilla.
                 *
                 * Finalmente se retorna true si no se llego al fin del
                 * arreglo, false si ya se llego, lo que quiere decir que no
                 * quedan entradas activas.
                 */
                do {
                    i++;

                } while (i < t.length && !isClose(i));

                return (i < t.length);

            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan mas elementos.");
                }

                if (TSB_OAHashtable.this.modCount != expectedModCount) {
                    throw new ConcurrentModificationException("Modificación inesperada de tabla.");
                }

                /**
                 * Se setea el previo igual al actual, para utilizarlo en
                 * la opcion de borrado.
                 */
                previo = actual;

                /**
                 * Se busca la proxima casilla que este cerrada, es
                 * decir que tenga la entrada activa.
                 * Si el metodo hasNext() fue true, se garantiza que
                 * se encuentra una.
                 * Una vez encontrada, se devuelve la KEY.
                 */
                do {
                    actual++;

                } while (actual < t.length && !isClose(actual));
                next_ok = true;
                return t[actual].getKey();
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("Debe invocar a next() antes de remove().");
                }

                // Se "mata" a la entrada actual.
                ((Entry) t[actual]).kill();

                // Se setea el actual como el previo. 
                actual = previo;

                // Se setea el control de next en falso. 
                next_ok = false;

                // Se modifica la cantidad. 
                TSB_OAHashtable.this.count--;

                // Se modifica el contador de modificaicones. 
                TSB_OAHashtable.this.modCount++;
                expectedModCount++;

            }

        }

        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {

            private int previo;
            private int actual;
            private int expectedModCount;
            private boolean next_ok;
            Map.Entry<K, V> t[];

            public EntrySetIterator() {
                actual = -1;
                previo = -1;
                expectedModCount = TSB_OAHashtable.this.modCount;
                next_ok = false;
                t = TSB_OAHashtable.this.table;
            }

            @Override
            public boolean hasNext() {

                if (count == 0) {
                    return false;
                }

                /**
                 * Se crea variable auxiliar para la iteracion.
                 */
                int i = actual;

                /**
                 * Se incrementa la variable hasta en contrar
                 * una casilla cerrada o llegar a la ultima casilla.
                 *
                 * Finalmente se retorna true si no se llego al fin del
                 * arreglo, false si ya se llego, lo que quiere decir que no
                 * quedan entradas activas.
                 */
                do {
                    i++;

                } while (i < t.length && !isClose(i));

                return (i < t.length);

            }

            @Override
            public Map.Entry<K, V> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan mas elementos.");
                }

                if (TSB_OAHashtable.this.modCount != expectedModCount) {
                    throw new ConcurrentModificationException("Modificación inesperada de tabla.");
                }

                /**
                 * Se setea el previo igual al actual, para utilizarlo en
                 * la opcion de borrado.
                 */
                previo = actual;

                /**
                 * Se busca la proxima casilla que este cerrada, es
                 * decir que tenga la entrada activa.
                 * Si el metodo hasNext() fue true, se garantiza que
                 * se encuentra una.
                 * Una vez encontrada, se devuelve la ENTRY.
                 */
                do {
                    actual++;

                } while (actual < t.length && !isClose(actual));
                next_ok = true;
                return t[actual];
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("Debe invocar a next() antes de remove().");
                }

                // Se "mata" a la entrada actual.
                ((Entry) t[actual]).kill();

                // Se setea el actual como el previo. 
                actual = previo;

                // Se setea el control de next en falso. 
                next_ok = false;

                // Se modifica la cantidad. 
                TSB_OAHashtable.this.count--;

                // Se modifica el contador de modificaicones. 
                TSB_OAHashtable.this.modCount++;
                expectedModCount++;

            }

        }

    }

    private class ValueCollection extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private class ValueCollectionIterator implements Iterator<V> {

            private int previo;
            private int actual;
            private int expectedModCount;
            private boolean next_ok;
            Map.Entry<K, V> t[];

            public ValueCollectionIterator() {
                actual = -1;
                previo = -1;
                expectedModCount = TSB_OAHashtable.this.modCount;
                next_ok = false;
                t = TSB_OAHashtable.this.table;
            }

            @Override
            public boolean hasNext() {

                if (count == 0) {
                    return false;
                }

                /**
                 * Se crea variable auxiliar para la iteracion.
                 */
                int i = actual;

                /**
                 * Se incrementa la variable hasta en contrar
                 * una casilla cerrada o llegar a la ultima casilla.
                 *
                 * Finalmente se retorna true si no se llego al fin del
                 * arreglo, false si ya se llego, lo que quiere decir que no
                 * quedan entradas activas.
                 */
                do {
                    i++;

                } while (i < t.length && !isClose(i));

                return (i < t.length);
            }

            @Override
            public V next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No quedan mas elementos.");
                }

                if (TSB_OAHashtable.this.modCount != expectedModCount) {
                    throw new ConcurrentModificationException("Modificación inesperada de tabla.");
                }

                /**
                 * Se setea el previo igual al actual, para utilizarlo en
                 * la opcion de borrado.
                 */
                previo = actual;

                /**
                 * Se busca la proxima casilla que este cerrada, es
                 * decir que tenga la entrada activa.
                 * Si el metodo hasNext() fue true, se garantiza que
                 * se encuentra una.
                 * Una vez encontrada, se devuelve el VALUE.
                 */
                do {
                    actual++;

                } while (actual < t.length && !isClose(actual));
                next_ok = true;
                return t[actual].getValue();
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("Debe invocar a next() antes de remove().");
                }

                // Se "mata" a la entrada actual.
                ((Entry) t[actual]).kill();

                // Se setea el actual como el previo. 
                actual = previo;

                // Se setea el control de next en falso. 
                next_ok = false;

                // Se modifica la cantidad. 
                TSB_OAHashtable.this.count--;

                // Se modifica el contador de modificaicones. 
                TSB_OAHashtable.this.modCount++;
                expectedModCount++;

            }

        }

    }
}
