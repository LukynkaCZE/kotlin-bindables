package cz.lukynka.bindables

class BindableMap<T, V>(map: Map<T, V>) {

    constructor(vararg list: Pair<T, V>): this(list.toMap())

    private var innerMap: MutableMap<T, V> = mutableMapOf()
    private var removeListeners = mutableListOf<BindableMapItemRemoveListener<T, V>>()
    private var changeListeners = mutableListOf<BindableMapItemChangeListener<T, V>>()
    private var updateListeners = mutableListOf<BindableMapUpdateListener<T, V>>()

    init {
        map.forEach(innerMap::put)
    }

    val values: Map<T, V>
        get() = innerMap.toMap()

    val size: Int
        get() = innerMap.size

    fun addIfNotPresent(key: T, value: V) {
        if(!values.containsKey(key)) set(key, value)
    }

    fun removeIfPresent(item: T) {
        if(values.contains(item)) remove(item)
    }

    fun remove(key: T) {
        val item = innerMap[key] ?: return
        innerMap.remove(key)
        removeListeners.forEach { it.unit.invoke(BindableMapItemRemovedEvent<T, V>(key, item)) }
        updateListeners.forEach { it.unit.invoke() }
    }

    operator fun set(key: T, value: V) {
        innerMap[key] = value
        changeListeners.forEach { it.unit.invoke(BindableMapItemSetEvent<T, V>(key, value)) }
        updateListeners.forEach { it.unit.invoke() }
    }

    fun addAll(map: Map<T, V>, silent: Boolean) {
        if(silent) innerMap.putAll(map) else map.forEach { set(it.key, it.value) }
    }

    operator fun contains(target: T): Boolean = values.contains(target)

    class BindableMapItemSetEvent<T, V>(val key: T, val value: V)
    class BindableMapItemRemovedEvent<T, V>(val key: T, val value: V)

    fun itemRemoved(function: (event: BindableMapItemRemovedEvent<T, V>) -> Unit): BindableMapItemRemoveListener<T, V> {
        val listener = BindableMapItemRemoveListener(function)
        removeListeners.add(listener)
        return listener
    }

    fun itemSet(function: (event: BindableMapItemSetEvent<T, V>) -> Unit): BindableMapItemChangeListener<T, V> {
        val listener = BindableMapItemChangeListener(function)
        changeListeners.add(listener)
        return listener
    }

    fun mapUpdated(function: () -> Unit): BindableMapUpdateListener<T, V> {
        val listener = BindableMapUpdateListener<T, V>(function)
        updateListeners.add(listener)
        return listener
    }

    fun setSilently(key: T, value: V) {
        innerMap[key] = value
    }

    fun removeSilently(key: T) {
        innerMap.remove(key)
    }

    fun triggerUpdate() {
        updateListeners.forEach { it.unit.invoke() }
    }

    operator fun get(slot: T): V? = innerMap[slot]

    fun clear(silent: Boolean = false) {
        if(silent) innerMap.clear() else values.forEach { remove(it.key) }
    }

    override fun toString(): String = values.toString()

    fun dispose() {
        removeListeners.clear()
        changeListeners.clear()
        updateListeners.clear()
    }

    fun unregister(listener: BindableMapListener) {
        when(listener) {
            is BindableMapItemChangeListener<*, *> -> changeListeners.remove(listener)
            is BindableMapItemRemoveListener<*, *> -> removeListeners.remove(listener)
            is BindableMapUpdateListener<*, *> -> updateListeners.remove(listener)
        }
    }

    class BindableMapItemRemoveListener<T, V>(val unit: (list: BindableMapItemRemovedEvent<T, V>) -> Unit): BindableMapListener
    class BindableMapItemChangeListener<T, V>(val unit: (list: BindableMapItemSetEvent<T, V>) -> Unit): BindableMapListener
    class BindableMapUpdateListener<T, V>(val unit: () -> Unit): BindableMapListener

    interface BindableMapListener
}