# kotlin-bindables
bindables library for kotlin inspired by the [osu!framework](https://github.com/ppy/osu-framework/) bindable system

## what are bindables

Bindables are objects that hold a value and provide following listeners:
- Bindable
  - value change
- BindableList
  - value added to list
  - value removed from list
  - value set by index in list
- BindableMap
  - value set
  - value removed

### why not use kotlin delegate?

To use delegate with decent syntax the library would have to do reflection which is not very good for performance

## Installation

<img src="https://cdn.worldvectorlogo.com/logos/kotlin-2.svg" width="16px"></img>
**Kotlin DSL**
```kotlin
repositories {
    maven {
        name = "devOS"
        url = uri("https://mvn.devos.one/releases")
    }
}

dependencies {
    implementation("cz.lukynka:kotlin-bindables:1.1")
}
```
<img src="https://github.com/LukynkaCZE/PrettyLog/assets/48604271/3293feca-7395-4100-8b61-257ba40dbe3c" width="18px"></img>
**Gradle Groovy**
```groovy
repositories {
  maven {
    name "devOS"
    url "https://mvn.devos.one/releases"
  }
}

dependencies {
  implementation 'cz.lukynka:kotlin-bindables:1.1'
}
```
---

## Usage
~~~~
There are different types of bindables, each with their own events

**_Bindable_**
```kotlin
    // give initial value of 5
val playerHealth = Bindable<Int>(5)

// Register a listener
playerHealth.valueChanged {
    println("Player health changed from ${it.oldValue} to ${it.newValue}!")
}

// Set the value
playerHealth.value = 20
```

**_BindableList_**
```kotlin
val playersOnline = BindableList<String>()

// called when item gets added
playersOnline.itemAdded {
    println("Player ${it.item} has joined!")
}


// called when item gets removed
playersOnline.itemRemoved {
    println("Player ${it.item} has left!")
}

// called when item at index is set
playersOnline.itemChanged {
    println("object at index ${it.index} has been changed to ${it.item}!")
}

playersOnline.add("AsoDesu_")
playersOnline.add("LukynkaCZE")
playersOnline.remove("AsoDesu_")
playersOnline.setIndex(1, "ColleiImpact")
```

**_BindableMap_**
```kotlin
val uuidToPlayerName = BindableMap<UUID, String>()

// Is both called from BindableMap.set and BindableMap.add
uuidToPlayerName.itemSet {
    println("Player with uuid ${it.key} (${it.value}) has joined!")
}

// called when item is removed
uuidToPlayerName.itemRemoved {
    println("Player with uuid ${it.key} (${it.value}) has left!")
}

// called when the map is updated 
uuidToPlayerName.mapUpdated {
    println("The player list has been updated!")
}

uuidToPlayerName[UUID.fromString("aeb19a9c-a64a-4255-bb42-e74f05f9d30f")] = "AsoDesu_"
uuidToPlayerName[UUID.fromString("0c9151e4-7083-418d-a29c-bbc58f7c741b")] = "LukynkaCZE"
uuidToPlayerName.remove(UUID.fromString("aeb19a9c-a64a-4255-bb42-e74f05f9d30f"))
uuidToPlayerName.set(UUID.fromString("0c9151e4-7083-418d-a29c-bbc58f7c741b"), "ColleiImpact")
```

---

### Extras

On all the types, you can also use `.triggerUpdate()` to call the listeners without actually causing any update to the value(s)

Both `BindableList` and `BindableMap` have `.addIfNotPresent(value)` and `.removeIfPresent(value)` functions

All of the bindable types provide `.setSilently(value)` which will set the value of the bindable without calling any of the listeners

---