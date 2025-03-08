import cz.lukynka.bindables.Bindable
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTests {

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