package cz.lukynka.bindables

fun Bindable<Boolean>.toggle() {
    this.value = !this.value
}