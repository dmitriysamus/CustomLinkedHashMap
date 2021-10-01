import java.util.Objects;

public class CustomLinkedHashMapImpl<K, V> implements CustomLinkedHashMap {
    /**
     * Контейнер для объекта.
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        Node<K, V> after;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int DEFAULT_CAPACITY = 16;
    private Node<K, V> table[] = new Node[DEFAULT_CAPACITY];
    private int size;
    private Node<K, V> head, tail;
    private int capacity;

    /**
     * Пустой конструктор объекта с capacity по умолчания.
     */
    CustomLinkedHashMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
    }

    /**
     * Конструктор объекта, принимающий на вход capacity.
     */
    CustomLinkedHashMapImpl(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Возвращает размер словаря.
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет словарь на наличие объектов.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает значение по ключу.
     */
    public Object get(Object key) {
        if (key == null && table[0] != null) {
            return null;
        }

        Node<K, V> tmp = table[getBucketId((K) key)];
        while (tmp != null) {
            if (getItemHash(tmp.key) == getItemHash((K)key) && Objects.equals(tmp.key, key)) {
                return tmp.value;
            }
            tmp = tmp.next;
        }

        return null;
    }

    /**
     * Добавляет объект в словарь.
     */
    public Object put(Object key, Object value) {
        int bucketId = getBucketId((K) key);
        int keyHash = getItemHash((K) key);

        Node<K, V> tmp = table[bucketId];
        Node<K, V> prev = null;

        while (tmp != null) {
            if (getItemHash(tmp.key) == keyHash && Objects.equals(tmp.key, key)) {
                V oldValue = tmp.value;
                tmp.value = (V) value;

                return oldValue;
            }

            prev = tmp;
            tmp = tmp.next;
        }

        Node<K, V> insertedNode = new Node<>((K)key, (V) value);
        if (prev == null) {
            table[bucketId] = insertedNode;
        } else {
            prev.next = insertedNode;
        }
        if (tail == null) {
            head = tail = insertedNode;
        } else {
            tail.after = insertedNode;
            tail = insertedNode;
        }
        size++;

        return null;
    }

    /**
     * Возвращает ID бакета по ключу.
     */
    private int getBucketId(K key) {

        if (key == null) {
            return 0;
        }
        return key.hashCode() % (table.length - 1);

    }

    /**
     * Возвращает hash объекта.
     */
    private int getItemHash(K item) {
        if (item == null) {
            return 0;
        }
        return item.hashCode();
    }

    /**
     * Удаляет объект из словаря.
     */
    public Object remove(Object key) {
        if (size == 0) {
            return null;
        }

        if (key == null && table[0] != null) {
            table[0] = null;
            return null;
        }

        int bucketId = getBucketId((K) key);
        int keyHash = getItemHash((K) key);
        boolean sign = false;

        Node<K, V> tmp = head;
        Node<K, V> tmpPrev = null;

        while (tmp != null) {
            if (getItemHash(tmp.key) == keyHash && Objects.equals(tmp.key, key)) {
                sign = true;
                break;
            }
            tmpPrev = tmp;
            tmp = tmp.after;
        }

        if (!sign) {
            return null;
        }

        if (tmpPrev == null) {
            table[bucketId] = null;
        } else {
            tmpPrev.next = tmp.next;
        }

        if (head == tmp && tail == tmp) {
            head = tail = null;
        } else if (head == tmp) {
            head = tmp.after;
        } else if (tail == tmp) {
            tail = tmpPrev;
        } else {
            tmpPrev.after = tmp.after;
        }
        size--;

        return tmp.value;
    }

    /**
     * Проверяет наличие ключа объекта в словаре.
     */
    public boolean containsKey(Object key) {
        if (key == null && table[0] != null) {
            return true;
        }

        return get(key) != null;
    }

    /**
     * Проверяет наличия значения объекта в словаре.
     */
    public boolean containsValue(Object value) {
        if (value == null && table[0] != null) {
            return true;
        }

        for (int i = 0; i < table.length; ++i) {
            Node<K, V> tmp = table[i];
            while (tmp != null) {
                if (Objects.equals(tmp.value, value)) {
                    return true;
                }
                tmp = tmp.next;
            }
        }

        return false;
    }

    /**
     * Возврящает множество всех ключей словаря.
     */
    public Object[] keys() {
        Object[] keys = new Object[size];
        Node<K, V> tmp = head;

        int j = 0;
        while (tmp != null) {
            keys[j++] = tmp.key;
            tmp = tmp.after;
        }

        return keys;
    }

    /**
     * Возврящает множество всех значений словаря.
     */
    public Object[] values() {
        Object[] objects = new Object[size];
        Node<K, V> tmp = head;

        int j = 0;
        while (tmp != null) {
            objects[j++] = tmp.value;
            tmp = tmp.after;
        }

        return objects;
    }

    /**
     * Возвращает строковое представление дерева.
     */
    @Override
    public String toString() {

        StringBuilder cb = new StringBuilder();

        cb.append("[ ");

        Node<K, V> tmp = head;
        while (tmp != null) {
            cb.append("{ key=" + tmp.key + ";value=" + tmp.value + "} ");
            tmp = tmp.after;
        }
        cb.append("]");
        return cb.toString();
    }

}
