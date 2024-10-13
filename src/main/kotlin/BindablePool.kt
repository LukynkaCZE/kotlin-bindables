package cz.lukynka

class BindablePool {

    private val bindables: MutableList<Bindable<*>> = mutableListOf()
    private val bindableLists: MutableList<BindableList<*>> = mutableListOf()
    private val bindableMaps: MutableList<BindableMap<*, *>> = mutableListOf()

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
        bindables.forEach { it.dispose() }
        bindableLists.forEach { it.dispose() }
        bindableMaps.forEach { it.dispose() }
    }
}