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
    fun testClear() {
        val list = BindableList<String>("test", "woah", "very testing", "yes")
        var removeCount = 0
        list.itemRemoved { removeCount++ }

        list.clear()

        assertEquals(4, removeCount)
    }

    @Test
    fun testClearSilent() {
        val list = BindableList<String>("test", "woah", "very testing", "yes")
        var removeCount = 0
        list.itemRemoved { removeCount++ }

        list.clear(true)

        assertEquals(0, removeCount)
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

    @Test
    fun testLifecycleUnregister() {
        var updateCount: Int = 0
        var addCount: Int = 0
        var removeCount: Int = 0
        var changeCount: Int = 0
        val bindable: BindableList<String> = BindableList()

        val addListener = bindable.itemAdded { addCount++ } // 2
        val removeListener = bindable.itemRemoved { removeCount++ } // 1
        val changeListener = bindable.itemChanged { changeCount++ } // 1
        val updateListener = bindable.listUpdated { updateCount++ } // 3

        bindable.add("test")
        bindable.setIndex(0, "test1")
        bindable.remove("test1")

        bindable.unregister(removeListener)
        bindable.unregister(changeListener)
        bindable.unregister(updateListener)

        bindable.add("test")
        bindable.setIndex(0, "test1")
        bindable.remove("test1")

        assertEquals(2, addCount)
        assertEquals(1, removeCount)
        assertEquals(1, changeCount)
        assertEquals(3, updateCount)
    }

    @Test
    fun testLifecycleDispose() {
        var updateCount: Int = 0
        var addCount: Int = 0
        var removeCount: Int = 0
        var changeCount: Int = 0
        val bindable: BindableList<String> = BindableList()

        val addListener = bindable.itemAdded { addCount++ } // 1
        val removeListener = bindable.itemRemoved { removeCount++ } // 1
        val changeListener = bindable.itemChanged { changeCount++ } // 1
        val updateListener = bindable.listUpdated { updateCount++ } // 3

        bindable.add("test")
        bindable.setIndex(0, "test1")
        bindable.remove("test1")

        bindable.dispose()

        bindable.add("test")
        bindable.setIndex(0, "test1")
        bindable.remove("test1")

        assertEquals(1, addCount)
        assertEquals(1, removeCount)
        assertEquals(1, changeCount)
        assertEquals(3, updateCount)
    }
}