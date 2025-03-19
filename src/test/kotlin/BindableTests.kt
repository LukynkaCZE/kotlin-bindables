import cz.lukynka.bindables.Bindable
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTests {

    @Test
    fun testRecursionBindThrows() {
        val x = Bindable<Int>(1)
        val y = Bindable<Int>(2)

        x.bindTo(y)
        assertThrows<IllegalStateException> { y.bindTo(x) }
    }

    @Test
    fun testBindAgainThrows() {
        val x = Bindable<Int>(1)
        val y = Bindable<Int>(2)
        val z = Bindable<Int>(3)

        x.bindTo(y)
        assertThrows<IllegalStateException> { x.bindTo(z) }
    }

    @Test
    fun testSelfBindingThrows() {
        val x = Bindable<Int>(1)

        assertThrows<IllegalStateException> { x.bindTo(x) }
    }

    @Test
    fun testBindingWithCopy() {
        val x = Bindable<Int>(1)
        val y = Bindable<Int>(2).withBindTo(x)

        assertEquals(x.value, y.value)

        y.value = 10
        assertEquals(10, x.value)

        x.value = 5
        assertEquals(5, y.value)
    }

    @Test
    fun testBindingCopy() {
        val x = Bindable<Int>(1)
        val y = x.getBoundCopy()

        assertEquals(x.value, y.value)

        y.value = 10
        assertEquals(10, x.value)

        x.value = 5
        assertEquals(5, y.value)
    }

    @Test
    fun testBinding() {
        val x = Bindable<Int>(1)
        val y = Bindable<Int>(2)

        x.bindTo(y)
        assertEquals(x.value, y.value)

        y.value = 10
        assertEquals(10, x.value)

        x.value = 5
        assertEquals(5, y.value)

        x.resetToDefaultValue()
        assertEquals(1, y.value)

        x.unbind()
        x.value = 6
        assertEquals(6, x.value)
        assertEquals(1, y.value)
    }

    @Test
    fun testResetToDefaultValue() {
        val intBindable = Bindable<Int>(5)
        assertEquals(5, intBindable.value)
        intBindable.value = 727
        assertEquals(727, intBindable.value)
        intBindable.resetToDefaultValue()
        assertEquals(5, intBindable.value)
        intBindable.defaultValue = 69
        intBindable.resetToDefaultValue()
        assertEquals(69, intBindable.value)
    }

    @Test
    fun testFireOnceThenDispose() {
        val booleanBindable = Bindable<Boolean>(false)
        var isReady: Boolean = false

        booleanBindable.valueChangedThenSelfDispose { event ->
            isReady = event.newValue
        }

        booleanBindable.value = true
        assertEquals(true, isReady)
        booleanBindable.value = false
        assertEquals(true, isReady)
    }

    @Test
    fun testGetSetValue() {
        val intBindable = Bindable<Int>(5)
        val floatBindable = Bindable<Float>(10f)
        floatBindable.value = 5f
        assert(intBindable.value == 5)
        assert(floatBindable.value == 5f)
    }

    @Test
    fun testValueChangeListener() {
        val bindable = Bindable<Int>(727)
        var oldValue = 0
        var newValue = 0

        // Asserting outside in case this doesn't fire
        bindable.valueChanged {
            oldValue = it.oldValue
            newValue = it.newValue
        }
        bindable.value = 69

        assert(oldValue == 727)
        assert(newValue == 69)
    }

    @Test
    fun testSetSilently() {
        val bindable = Bindable<Int>(727)
        var newValue: Int? = null

        bindable.valueChanged {
            newValue = it.newValue
        }

        bindable.setSilently(69)
        assert(newValue == null)
    }

    @Test
    fun triggerUpdate() {
        val initialValue = 727
        val bindable = Bindable<Int>(initialValue)

        var newValue: Int? = null
        bindable.valueChanged {
            newValue = it.newValue
        }

        bindable.triggerUpdate()
        assert(newValue == initialValue)
    }

    @Test
    fun testToString() {
        val bindableInt = Bindable<Int>(727)
        val bindableFloat = Bindable<Float>(72.7f)
        val bindableString = Bindable<String>("owo :3")
        val bindableBoolean = Bindable<Boolean>(true)

        val expected = "727 72.7 owo :3 true"
        val string = "$bindableInt $bindableFloat $bindableString $bindableBoolean"

        assertEquals(expected, string)
    }

    @Test
    fun testLifecycleUnregister() {
        val bindable: Bindable<String> = Bindable("Testing :3")
        var newValue: String? = null

        val listener = bindable.valueChanged {
            newValue = it.newValue
        }
        bindable.value = "Value 1"
        bindable.unregister(listener)
        bindable.value = "newValue should not be this"
        assertEquals("Value 1", newValue)
    }

    @Test
    fun testLifecycleDispose() {
        val bindable: Bindable<String> = Bindable("Testing :3")
        var newValue1: String? = null
        var newValue2: String? = null
        var newValue3: String? = null

        bindable.valueChanged {
            newValue1 = it.newValue
        }

        bindable.valueChanged {
            newValue2 = it.newValue
        }

        bindable.valueChanged {
            newValue3 = it.newValue
        }

        bindable.value = "Value 1"
        bindable.dispose()
        bindable.value = "newValue should not be this"
        assertEquals("Value 1", newValue1)
        assertEquals("Value 1", newValue2)
        assertEquals("Value 1", newValue3)
    }
}