import cz.lukynka.Bindable
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableTests {

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
}