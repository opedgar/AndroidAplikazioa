package com.example.edgar.androidaplikazioa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Main2Activity : AppCompatActivity() {

    lateinit var addButton : Button
    lateinit var textView : TextView

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
        setContentView(R.layout.activity_main2)

        btnLunes = findViewById<CheckBox>(R.id.modifyBtnLunes)
        btnMartes = findViewById<CheckBox>(R.id.modifyBtnMartes)
        btnMiercoles = findViewById<CheckBox>(R.id.modifyBtnMiercoles)
        btnJueves = findViewById<CheckBox>(R.id.modifyBtnJueves)
        btnViernes = findViewById<CheckBox>(R.id.modifyBtnViernes)
        btnSabado = findViewById<CheckBox>(R.id.modifyBtnSabado)
        btnDomingo = findViewById<CheckBox>(R.id.modifyBtnDomingo)
        textView = findViewById<TextView>(R.id.modifyTextView)

        var myIntent = intent
        var id = myIntent.getStringExtra("id")
        var name : String = myIntent.getStringExtra("name")

        textView.setText(name)

        addButton = findViewById(R.id.modifyAddButton)
        addButton.setOnClickListener(View.OnClickListener { v ->
            modificarHabito(id, name)
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onStart() {
        super.onStart()

        account = GoogleSignIn.getLastSignedInAccount(this)!!


    }

    fun modificarHabito(id:String, name:String){

        var frecuencia = HashMap<String, Boolean>()
        frecuencia.put("lunes", btnLunes.isChecked)
        frecuencia.put("martes", btnMartes.isChecked)
        frecuencia.put("miercoles", btnMiercoles.isChecked)
        frecuencia.put("jueves", btnJueves.isChecked)
        frecuencia.put("viernes", btnViernes.isChecked)
        frecuencia.put("sabado", btnSabado.isChecked)
        frecuencia.put("domingo", btnDomingo.isChecked)

        var habit = Habit(name, account.id!!, frecuencia)

        habitRef.child(id).setValue(habit)

    }
}
