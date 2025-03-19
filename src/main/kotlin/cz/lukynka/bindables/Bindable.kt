package cz.lukynka.bindables

class Bindable<T>(var defaultValue: T) {

    private val changeListeners = mutableMapOf<ValueChangeListener<T>, Boolean>()
    private var bindableValue = defaultValue

    var value: T
        get() = bindableValue
        set(value) {
            val oldValue = bindableValue
            bindableValue = value
            changeListeners.forEach { (listener, disposeAfter) ->
                listener.unit.invoke(ValueChangedEvent<T>(oldValue, value))
                if(disposeAfter) unregister(listener)
            }
        }

    fun isValidListener(listener: ValueChangeListener<*>): Boolean {
        return changeListeners.contains(listener)
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
        changeListeners[listener] = false
        return listener
    }

    fun resetToDefaultValue(silent: Boolean = false) {
        if(silent) setSilently(defaultValue) else this.value = defaultValue
    }

    fun valueChangedThenSelfDispose(unit: (event: ValueChangedEvent<T>) -> Unit): ValueChangeListener<T> {
        val listener = ValueChangeListener<T>(unit)
        changeListeners[listener] = true
        return listener
    }

    fun triggerUpdate(disposes: Boolean = false) {
        changeListeners.forEach { (listener, dispose) ->
            listener.unit.invoke(ValueChangedEvent<T>(value, value))
            if(disposes && dispose) unregister(listener)
        }
    }

    @JvmName("unregisterType")
    fun unregister(listener: ValueChangeListener<T>) {
        changeListeners.remove(listener)
    }

    @JvmName("unregisterNotTyped")
    fun unregister(listener: ValueChangeListener<*>) {
        changeListeners.remove(listener)
    }

    fun dispose() {
        changeListeners.clear()
    }

    override fun toString(): String = value.toString()
}