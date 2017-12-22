package com.example.edgar.androidaplikazioa

/**
 * Created by edgar on 15/12/17.
 */
class User (var id : String = "GenericId", var name : String = "GenericName", var listaHabitos : HashMap<String, Boolean> = HashMap<String, Boolean>()) {
    fun addHabit (id : String, activo : Boolean) {
        listaHabitos.put(id, activo)
    }
}