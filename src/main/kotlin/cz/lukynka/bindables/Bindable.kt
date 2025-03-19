package cz.lukynka.bindables

import java.lang.IllegalStateException

class Bindable<T>(var defaultValue: T) {

    private val changeListeners = mutableMapOf<ValueChangeListener<T>, Boolean>()
    private var bindableValue = defaultValue
    private var boundListener: ValueChangeListener<T>? = null
    private var boundBindable: Bindable<T>? = null

    val isBound: Boolean get() = boundBindable != null
    val boundTarget: Bindable<T>? get() = boundBindable

    var value: T
        get() = bindableValue
        set(value) {
            val oldValue = bindableValue
            updateInternal(value, oldValue)
            boundBindable?.value = value
        }

    val isDefaultValue: Boolean get() = value == defaultValue

    fun getBoundCopy(): Bindable<T> {
        val newBindable = Bindable<T>(this.defaultValue)
        newBindable.bindTo(this)
        return newBindable
    }

    private fun updateInternal(value: T, oldValue: T) {
        bindableValue = value
        changeListeners.forEach { (listener, disposeAfter) ->
            listener.unit.invoke(ValueChangedEvent<T>(oldValue, value))
            if(disposeAfter) unregister(listener)
        }
    }

    fun bindTo(bindable: Bindable<T>) {
        if(bindable == this) throw IllegalStateException("Cannot bind to self")
        if(this.boundListener != null || this.boundBindable != null) throw IllegalStateException("An already bound bindable cannot be bound again")
        if(bindable.boundTarget != null && bindable.boundTarget!! == this) throw IllegalStateException("Cannot bind to bindable that is bound to this bindable")

        value = bindable.value

        boundBindable = bindable
        boundListener = bindable.valueChanged { event ->
            updateInternal(event.newValue, value)
        }
    }

    fun withBindTo(bindable: Bindable<T>): Bindable<T> {
        bindTo(bindable)
        return this
    }

    fun unbind() {
        boundListener?.let { listener -> boundBindable?.unregister(listener) }
        boundListener = null
        boundBindable = null
    }

    fun isValidListener(listener: ValueChangeListener<*>): Boolean {
        return changeListeners.contains(listener)
    }

    fun setSilently(value: T) {
        this.bindableValue = value
        boundBindable?.setSilently(value)
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
        unbind()
    }

    override fun toString(): String = value.toString()
}