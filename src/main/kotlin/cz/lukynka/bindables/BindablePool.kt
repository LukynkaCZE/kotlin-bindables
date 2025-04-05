package cz.lukynka.bindables

class BindablePool {

    private val bindables: MutableList<Bindable<*>> = mutableListOf()
    private val bindableLists: MutableList<BindableList<*>> = mutableListOf()
    private val bindableMaps: MutableList<BindableMap<*, *>> = mutableListOf()
    private val bindableListeners: MutableList<BindableDispatcher<*>> = mutableListOf()

    fun <T> provideBindableDispatcher(): BindableDispatcher<T> {
        val listener = BindableDispatcher<T>()
        bindableListeners.add(listener)
        return listener
    }

    fun <T> provideBindable(defaultValue: T): Bindable<T> {
        val bindable = Bindable<T>(defaultValue)
        bindables.add(bindable)
        return bindable
    }

    fun <T> provideBindableList(vararg default: T): BindableList<T> {
        return provideBindableList<T>(default.toList())
    }

    fun <T> provideBindableList(default: Collection<T>?): BindableList<T> {
        val bindableList = BindableList<T>()
        if(default != null) bindableList.addAll(default, true)
        bindableLists.add(bindableList)
        return bindableList
    }

    fun <K, V> provideBindableMap(vararg pair: Pair<K, V>): BindableMap<K, V> {
        return provideBindableMap<K, V>(pair.toMap())
    }

    fun <K, V> provideBindableMap(map: Map<K, V>? = null): BindableMap<K, V> {
        val bindableMap = BindableMap<K, V>()
        if(map != null) bindableMap.addAll(map, true)
        bindableMaps.add(bindableMap)
        return bindableMap
    }

    fun unregister(bindableListener: BindableDispatcher<*>) {
        bindableListeners.remove(bindableListener)
        bindableListener.dispose()
    }

    fun unregister(bindable: Bindable<*>) {
        bindables.remove(bindable)
        bindable.dispose()
    }

    fun unregister(bindableList: BindableList<*>) {
        bindableLists.remove(bindableList)
        bindableList.dispose()
    }

    fun unregister(bindableMap: BindableMap<*, *>) {
        bindableMaps.remove(bindableMap)
        bindableMap.dispose()
    }

    fun dispose() {
        bindables.forEach { bindable -> bindable.dispose() }
        bindableLists.forEach { list -> list.dispose() }
        bindableMaps.forEach { map -> map.dispose() }
        bindableListeners.forEach { listener -> listener.dispose() }
    }
}