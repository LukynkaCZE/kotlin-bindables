package cz.lukynka.bindables

class BindableList<T>(list: Collection<T>) {

    constructor(vararg list: T): this(list.toList())

    private var innerList: MutableList<T> = mutableListOf()
    private var addListeners = mutableListOf<BindableListItemAddListener<T>>()
    private var removeListeners = mutableListOf<BindableListItemRemoveListener<T>>()
    private var changeListeners = mutableListOf<BindableListItemChangeListener<T>>()
    private var updateListeners = mutableListOf<BindableListUpdateListener<T>>()

    init {
        list.forEach(innerList::add)
    }

    val values: Collection<T>
        get() = innerList.toList()

    val size: Int
        get() = innerList.size

    fun add(item: T) {
        innerList.add(item)
        addListeners.forEach { it.unit.invoke(BindableListItemAddEvent<T>(item)) }
        updateListeners.forEach { it.unit.invoke() }
    }

    fun addIfNotPresent(item: T) {
        if(!values.contains(item)) add(item)
    }

    fun removeIfPresent(item: T) {
        if(values.contains(item)) remove(item)
    }

    fun remove(item: T) {
        innerList.remove(item)
        updateListeners.forEach { it.unit.invoke() }
        removeListeners.forEach { it.unit.invoke(BindableListItemRemovedEvent<T>(item)) }
    }

    fun setIndex(index: Int, item: T) {
        innerList[index] = item
        updateListeners.forEach { it.unit.invoke() }
        changeListeners.forEach { it.unit.invoke(BindableListItemChangeEvent<T>(index, item)) }
    }

    fun addAll(list: Collection<T>, silent: Boolean) {
        if(silent) innerList.addAll(list) else list.forEach { add(it) }
    }

    operator fun contains(target: T): Boolean = values.contains(target)

    class BindableListUpdateEvent<T>(val item: T?)
    class BindableListItemChangeEvent<T>(val index: Int, val item: T)
    class BindableListItemAddEvent<T>(val item: T)
    class BindableListItemRemovedEvent<T>(val item: T)

    fun itemAdded(function: (event: BindableListItemAddEvent<T>) -> Unit): BindableListItemAddListener<T> {
        val listener = BindableListItemAddListener(function)
        addListeners.add(listener)
        return listener
    }

    fun itemRemoved(function: (event: BindableListItemRemovedEvent<T>) -> Unit): BindableListItemRemoveListener<T> {
        val listener = BindableListItemRemoveListener(function)
        removeListeners.add(listener)
        return listener
    }

    fun itemChanged(function: (event: BindableListItemChangeEvent<T>) -> Unit): BindableListItemChangeListener<T> {
        val listener = BindableListItemChangeListener<T>(function)
        changeListeners.add(listener)
        return listener
    }

    fun listUpdated(function: () -> Unit): BindableListUpdateListener<T> {
        val listener = BindableListUpdateListener<T>(function)
        updateListeners.add(listener)
        return listener
    }

    fun triggerUpdate() {
        updateListeners.forEach { it.unit.invoke() }
    }

    fun setValues(values: Collection<T>) {
        innerList = values.toMutableList()
        updateListeners.forEach { it.unit.invoke() }
    }

    fun clear(silent: Boolean = false) {
        if(silent) innerList.clear() else values.forEach(this::remove)
    }

    fun dispose() {
        addListeners.clear()
        removeListeners.clear()
        changeListeners.clear()
        updateListeners.clear()
    }

    fun unregister(listener: BindableListListener) {
        when(listener) {
            is BindableListItemAddListener<*> -> addListeners.remove(listener)
            is BindableListItemRemoveListener<*> -> removeListeners.remove(listener)
            is BindableListItemChangeListener<*> -> changeListeners.remove(listener)
            is BindableListUpdateListener<*> -> updateListeners.remove(listener)
        }
    }

    override fun toString(): String = values.toString()

    class BindableListItemAddListener<T>(val unit: (list: BindableListItemAddEvent<T>) -> Unit): BindableListListener
    class BindableListItemRemoveListener<T>(val unit: (list: BindableListItemRemovedEvent<T>) -> Unit): BindableListListener
    class BindableListItemChangeListener<T>(val unit: (list: BindableListItemChangeEvent<T>) -> Unit): BindableListListener
    class BindableListUpdateListener<T>(val unit: () -> Unit): BindableListListener

    interface BindableListListener
}
