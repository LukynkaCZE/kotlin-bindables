package cz.lukynka

class Bindable<T>(initialValue: T) {

    private val changeListeners = mutableListOf<ValueChangeListener<T>>()
    private var bindableValue = initialValue

    var value: T
        get() = bindableValue
        set(value) {
            val oldValue = bindableValue
            bindableValue = value
            changeListeners.forEach { it.unit.invoke(ValueChangedEvent<T>(oldValue, value)) }
        }

    fun setSilently(value: T) {
        this.bindableValue = value
    }

    class ValueChangeListener<T>(
        val unit: (event: ValueChangedEvent<T>) -> Unit
    )

    class ValueChangedEvent<T> (
        val oldValue: T,
        val newValue: T
    )

    fun valueChanged(unit: (event: ValueChangedEvent<T>) -> Unit): ValueChangeListener<T> {
        val listener = ValueChangeListener<T>(unit)
        changeListeners.add(listener)
        return listener
    }

    fun triggerUpdate() {
        changeListeners.forEach { it.unit.invoke(ValueChangedEvent<T>(value, value)) }
    }

    fun unregister(listener: ValueChangeListener<T>) {
        changeListeners.remove(listener)
    }

    fun dispose() {
        changeListeners.clear()
    }

    override fun toString(): String = value.toString()
}