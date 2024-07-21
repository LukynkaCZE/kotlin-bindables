package cz.lukynka

class BindableList<T>(list: Collection<T>) {

    constructor(vararg list: T): this(list.toList())

    private var innerList: MutableList<T> = mutableListOf()
    private var addListeners = mutableListOf<BindableListItemAddListener<T>>()
    private var removeListener = mutableListOf<BindableListItemRemoveListener<T>>()
    private var changeListener = mutableListOf<BindableListItemChangeListener<T>>()
    private var updateListener = mutableListOf<BindableListUpdateListener<T>>()

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
        updateListener.forEach { it.unit.invoke() }
    }

    fun addIfNotPresent(item: T) {
        if(!values.contains(item)) add(item)
    }

    fun removeIfPresent(item: T) {
        if(values.contains(item)) remove(item)
    }

    fun remove(item: T) {
        innerList.remove(item)
        updateListener.forEach { it.unit.invoke() }
        removeListener.forEach { it.unit.invoke(BindableListItemRemovedEvent<T>(item)) }
    }

    fun setIndex(index: Int, item: T) {
        innerList[index] = item
        updateListener.forEach { it.unit.invoke() }
        changeListener.forEach { it.unit.invoke(BindableListItemChangeEvent<T>(index, item)) }
    }

    operator fun contains(target: T): Boolean = values.contains(target)

    class BindableListUpdateEvent<T>(val item: T?)
    class BindableListItemChangeEvent<T>(val index: Int, val item: T)
    class BindableListItemAddEvent<T>(val item: T)
    class BindableListItemRemovedEvent<T>(val item: T)

    fun itemAdded(function: (event: BindableListItemAddEvent<T>) -> Unit) {
        addListeners.add(BindableListItemAddListener(function))
    }

    fun itemRemoved(function: (event: BindableListItemRemovedEvent<T>) -> Unit) {
        removeListener.add(BindableListItemRemoveListener(function))
    }

    fun itemChanged(function: (event: BindableListItemChangeEvent<T>) -> Unit) {
        changeListener.add(BindableListItemChangeListener(function))
    }

    fun listUpdated(function: () -> Unit) {
        updateListener.add(BindableListUpdateListener(function))
    }

    fun triggerUpdate() {
        updateListener.forEach { it.unit.invoke() }
    }

    fun setValues(values: Collection<T>) {
        innerList = values.toMutableList()
        updateListener.forEach { it.unit.invoke() }
    }

    override fun toString(): String = values.toString()

    class BindableListItemAddListener<T>(val unit: (list: BindableListItemAddEvent<T>) -> Unit)
    class BindableListItemRemoveListener<T>(val unit: (list: BindableListItemRemovedEvent<T>) -> Unit)
    class BindableListItemChangeListener<T>(val unit: (list: BindableListItemChangeEvent<T>) -> Unit)
    class BindableListUpdateListener<T>(val unit: () -> Unit)
}
