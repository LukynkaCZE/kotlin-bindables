import cz.lukynka.bindables.BindableDispatcher
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BindableDispatcherTest {

    private lateinit var dispatcher: BindableDispatcher<Int>
    private lateinit var receivedValues: MutableList<Int>

    @BeforeTest
    fun setUp() {
        dispatcher = BindableDispatcher()
        receivedValues = mutableListOf()
    }

    @AfterTest
    fun after() {
        dispatcher.dispose()
    }

    @Test
    fun testSelfDisposing() {
        dispatcher = BindableDispatcher()
        receivedValues = mutableListOf()

        assertDoesNotThrow {
            dispatcher.subscribe { _ ->
                dispatcher.dispose()
            }

            dispatcher.dispatch(5)
        }
    }

    @Test
    fun `register and dispatch`() {
        dispatcher.subscribe { event ->
            receivedValues.add(event)
        }

        dispatcher.dispatch(10)
        dispatcher.dispatch(20)

        assertEquals(listOf(10, 20), receivedValues)
        dispatcher.dispose()
    }

    @Test
    fun `unregister listener`() {
        val listener = dispatcher.subscribe { event ->
            receivedValues.add(event)
        }

        dispatcher.dispatch(10)
        dispatcher.unsubscribe(listener)
        dispatcher.dispatch(20)

        assertEquals(listOf(10), receivedValues)
        dispatcher.dispose()
    }

    @Test
    fun `multiple listeners`() {
        val listener1 = dispatcher.subscribe { event ->
            receivedValues.add(event)
        }

        val listener2 = dispatcher.subscribe { event ->
            receivedValues.add(event * 2)
        }

        dispatcher.dispatch(5)
        assertEquals(listOf(5, 10), receivedValues)
        dispatcher.dispose()
    }

    @Test
    fun `dispose clears listeners`() {
        val listener = dispatcher.subscribe { event ->
            receivedValues.add(event)
        }

        dispatcher.dispose()
        dispatcher.dispatch(10)

        assertTrue(receivedValues.isEmpty())
        dispatcher.dispose()
    }

    @Test
    fun `register returns the same listener`() {
        val listener: (Int) -> Unit = { receivedValues.add(it) }
        val registeredListener = dispatcher.subscribe(listener)
        assertEquals(listener, registeredListener)
        dispatcher.dispose()
    }

    @Test
    fun `unregister removes only the specified listener`() {
        val listener1 = dispatcher.subscribe { event ->
            receivedValues.add(event)
        }

        dispatcher.subscribe { event ->
            receivedValues.add(event * 2)
        }

        dispatcher.unsubscribe(listener1)
        dispatcher.dispatch(5)

        assertEquals(listOf(10), receivedValues)
        dispatcher.dispose()
    }
}