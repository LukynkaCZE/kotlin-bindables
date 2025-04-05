package cz.lukynka.bindables

class BindableDispatcher<T> {
    private var listeners: MutableList<(T) -> Unit> = mutableListOf()

    val subscriberSize: Int get() = listeners.size

    fun subscribe(unit: (T) -> Unit): (T) -> Unit {
        listeners.add(unit)
        return unit
    }

    fun unsubscribe(unit: (T) -> Unit) {
        listeners.remove(unit)
    }

    fun dispatch(value: T) {
        listeners.toList().forEach { listener -> listener.invoke(value) }
    }

    fun dispose() {
        listeners.clear()
    }

}