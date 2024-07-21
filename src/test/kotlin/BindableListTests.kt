import cz.lukynka.BindableList
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableListTests {

    @Test
    fun testGetSetValues() {
        val bindableList = BindableList<Int>()

        bindableList.add(7)
        bindableList.add(2)
        bindableList.add(7)

        assertEquals(listOf<Int>(7, 2, 7), bindableList.values)
    }

    @Test
    fun testListeners() {
        val bindableList = BindableList<Int>()

        var itemAdded: Int? = null
        bindableList.itemAdded {
            itemAdded = it.item
        }

        var itemChanged: Int? = null
        bindableList.itemChanged {
            itemChanged = it.item
        }

        var itemRemoved: Int? = null
        bindableList.itemRemoved {
            itemRemoved = it.item
        }

        var timesUpdateCalled = 0

        bindableList.listUpdated {
            timesUpdateCalled++
        }

        bindableList.add(5)
        bindableList.setIndex(0, 7)
        bindableList.remove(7)
        bindableList.triggerUpdate()

        assertEquals(5, itemAdded)
        assertEquals(7, itemChanged)
        assertEquals(7, itemRemoved)
        assertEquals(timesUpdateCalled, 4)
    }

    @Test
    fun testAddIfNotPresentRemoveIfPresent() {
        val list = BindableList<Int>(1, 2, 3, 5, 7)

        list.addIfNotPresent(4)
        list.addIfNotPresent(1)
        list.removeIfPresent(7)
        list.removeIfPresent(9)

        assertEquals(listOf(1, 2, 3, 5, 4), list.values)
    }
}