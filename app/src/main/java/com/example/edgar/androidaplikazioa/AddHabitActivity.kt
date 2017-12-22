package com.example.edgar.androidaplikazioa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.*

class AddHabitActivity : AppCompatActivity() {

    lateinit var addButton : Button
    lateinit var editText : EditText
    lateinit var btnLunes : CheckBox
    lateinit var btnMartes : CheckBox
    lateinit var btnMiercoles : CheckBox
    lateinit var btnJueves : CheckBox
    lateinit var btnViernes : CheckBox
    lateinit var btnSabado : CheckBox
    lateinit var btnDomingo : CheckBox

    lateinit var account : GoogleSignInAccount

    val database = FirebaseDatabase.getInstance()
    val idHabitRef = database.getReference("idHabitoGlobal")
    val habitRef = database.getReference("habits")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        addButton = findViewById<Button>(R.id.addButton)
        editText = findViewById<EditText>(R.id.editText2)
        btnLunes = findViewById<CheckBox>(R.id.btnLunes)
        btnMartes = findViewById<CheckBox>(R.id.btnMartes)
        btnMiercoles = findViewById<CheckBox>(R.id.btnMiercoles)
        btnJueves = findViewById<CheckBox>(R.id.btnJueves)
        btnViernes = findViewById<CheckBox>(R.id.btnViernes)
        btnSabado = findViewById<CheckBox>(R.id.btnSabado)
        btnDomingo = findViewById<CheckBox>(R.id.btnDomingo)
    }

    override fun onStart() {
        super.onStart()

        account = GoogleSignIn.getLastSignedInAccount(this)!!

        addButton.setOnClickListener(View.OnClickListener { v ->
            añadirHabito()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })


    }

    fun añadirHabito(){

        var name = editText.text.toString()
        var frecuencia = HashMap<String, Boolean>()
        frecuencia.put("lunes", btnLunes.isChecked)
        frecuencia.put("martes", btnMartes.isChecked)
        frecuencia.put("miercoles", btnMiercoles.isChecked)
        frecuencia.put("jueves", btnJueves.isChecked)
        frecuencia.put("viernes", btnViernes.isChecked)
        frecuencia.put("sabado", btnSabado.isChecked)
        frecuencia.put("domingo", btnDomingo.isChecked)

        idHabitRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var id : Int = p0!!.value.toString().toInt()
                var nuevoId = (id + 1).toString()
                idHabitRef.setValue(nuevoId)

                var nuevoHabito = Habit(name, account.id.toString(), frecuencia)
                var mapa = HashMap<String, Habit>()
                mapa.put(nuevoId, nuevoHabito)
                habitRef.updateChildren(mapa as Map<String, Habit>)
            }

            override fun onCancelled(p0: DatabaseError?) {
                //Ignore
            }
        })
    }
}
