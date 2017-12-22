package com.example.edgar.androidaplikazioa

/**
 * Created by edgar on 15/12/17.
 */
class Habit (var name : String = "GenericName", var owner : String = "ownerGenerico", var frecuencia : HashMap<String, Boolean> = HashMap<String, Boolean>(), var activo:Boolean = true) {
    fun cambiarFrecuencia (lunes : Boolean, martes : Boolean, miercoles : Boolean, jueves : Boolean, viernes : Boolean, sabado : Boolean, domingo : Boolean) {
        frecuencia.put("lunes", lunes)
        frecuencia.put("martes", martes)
        frecuencia.put("miercoles", miercoles)
        frecuencia.put("jueves", jueves)
        frecuencia.put("viernes", viernes)
        frecuencia.put("sabado", sabado)
        frecuencia.put("domingo", domingo)
    }
}