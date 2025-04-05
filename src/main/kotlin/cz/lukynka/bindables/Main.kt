package cz.lukynka.bindables

fun main() {
    val dispatcher = BindableDispatcher<Int>()

    dispatcher.subscribe { int ->
        println("dispatcher notification: $int")
    }

    dispatcher.dispatch(69)
}