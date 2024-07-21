package cz.lukynka

fun Bindable<Boolean>.toggle() {
    this.value = !this.value
}