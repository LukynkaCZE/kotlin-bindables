package cz.lukynka.bindables

class BindableDispatcher<T> {
    private var listeners: MutableList<(T) -> Unit> = mutableListOf()

    fun register(unit: (T) -> Unit): (T) -> Unit {
        listeners.add(unit)
        return unit
    }

    fun unregister(unit: (T) -> Unit) {
        listeners.remove(unit)
    }

    fun dispatch(value: T) {
        listeners.forEach { listener -> listener.invoke(value) }
    }

    fun dispose() {
        listeners.clear()
    }

}