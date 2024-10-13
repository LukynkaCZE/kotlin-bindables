import cz.lukynka.BindablePool
import kotlin.test.Test
import kotlin.test.assertEquals

class BindablePoolTests {

    @Test
    fun testBindablePoolDispose() {
        val pool = BindablePool()

        val playerHealth = pool.provideBindable<Double>(20.0)
        val stringList = pool.provideBindableList<String>("uwu", "owo")
        val intMap = pool.provideBindableMap<Int, String>(0 to "a", 4 to "e")

        var mutations: Int = 0

        playerHealth.valueChanged { mutations++ }
        playerHealth.valueChanged { mutations++ }
        stringList.itemAdded { mutations++ }
        intMap.mapUpdated { mutations++ }

        playerHealth.value = 5.0 //2
        stringList.add("meow") //1
        intMap.remove(0)
        intMap.remove(4)
        intMap[5] = "f" // 3

        pool.dispose()

        playerHealth.value = 25.0
        stringList.remove("meow")
        intMap.clear(false)

        assertEquals(6, mutations)
    }

    @Test
    fun testBindablePoolUnregister() {
        val pool = BindablePool()

        val playerHealth = pool.provideBindable<Double>(20.0)
        val stringList = pool.provideBindableList<String>("uwu", "owo")
        val intMap = pool.provideBindableMap<Int, String>(0 to "a", 4 to "e")

        var mutations: Int = 0

        playerHealth.valueChanged { mutations++ }
        playerHealth.valueChanged { mutations++ }
        stringList.itemAdded { mutations++ }
        intMap.mapUpdated { mutations++ }

        playerHealth.value = 5.0 //2
        stringList.add("meow") //1
        intMap.remove(0)
        intMap.remove(4)
        intMap[5] = "f" // 3

        pool.unregister(intMap)
        pool.unregister(stringList)

        stringList.remove("meow")
        intMap.clear(false)
        playerHealth.value = 25.0 // 2

        assertEquals(8, mutations)
    }
}