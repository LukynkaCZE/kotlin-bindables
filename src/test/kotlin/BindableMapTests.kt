import cz.lukynka.BindableMap
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableMapTests {

    @Test
    fun testGetSetValues() {
        val unicornStartupInfo = BindableMap<String, Int>()
        val startupMetadata = BindableMap<String, Boolean>()

        unicornStartupInfo["users"] = 0
        unicornStartupInfo["profit"] = -7

        startupMetadata["is_crypto_scam"] = true
        startupMetadata["is_ai_scam"] = false

        assertEquals(0, unicornStartupInfo["users"])
        assertEquals(-7, unicornStartupInfo["profit"])

        assertEquals(true, startupMetadata["is_crypto_scam"])
        assertEquals(false, startupMetadata["is_ai_scam"])
    }

    @Test
    fun testListeners() {
        val bindableMap = BindableMap<String, Int>()

        var itemSet: Pair<String, Int>? = null
        bindableMap.itemSet {
            itemSet = it.key to it.value
        }

        var mapUpdatedTimes = 0
        bindableMap.mapUpdated {
            mapUpdatedTimes++
        }

        bindableMap["wysi"] = 727
        bindableMap.triggerUpdate()

        assertEquals(Pair<String, Int>("wysi", 727), itemSet)
        assertEquals(2, mapUpdatedTimes)
    }

    @Test
    fun testSilent() {
        val bindableMap = BindableMap<String, Int>()

        var itemSet: Pair<String, Int>? = null
        bindableMap.itemSet {
            itemSet = it.key to it.value
        }

        bindableMap.setSilently("test", 1)
        assertEquals(1, bindableMap["test"])
        assertEquals(null, itemSet)
    }

    fun testClear() {
        val bindableMap = BindableMap<String, Int>("test" to 1, "no" to 0)

        var updateCount = 0
        bindableMap.mapUpdated {
            updateCount++
        }

        bindableMap.clear()
        assertEquals(0, bindableMap.size)
        assertEquals(2, updateCount)
    }

    @Test
    fun testClearSilent() {
        val bindableMap = BindableMap<String, Int>("test" to 1, "no" to 0)

        var updateCount = 0
        bindableMap.mapUpdated {
            updateCount++
        }

        bindableMap.clear(true)
        assertEquals(0, bindableMap.size)
        assertEquals(1, updateCount)
    }
}