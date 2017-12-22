package com.example.edgar.androidaplikazioa

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.MenuItem
import android.widget.*
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*
import android.widget.TextView
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var floatingActionButton : FloatingActionButton
    lateinit var listaContentMain : ListView

    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var account : GoogleSignInAccount
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users")
    val habitRef = database.getReference("habits")
    val idHabitRef = database.getReference("idHabitoGlobal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        listaContentMain = findViewById<ListView>(R.id.listViewContentMain)
        registerForContextMenu(listaContentMain)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //Objeto que recibe el email y el pérfil (ID e información básica) del usuario
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        añadirUsuario()
        updateUI()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Infla el menu, esto añado items a la action bar en caso de que esté presente
        menuInflater.inflate(R.menu.main, menu)
        //Insertamos información del usuario en la barra de navegación
        findViewById<TextView>(R.id.emailHeaderTextView).setText(account.email)
        findViewById<TextView>(R.id.nameHeaderTextView).setText(account.displayName)
        var imageView = findViewById<ImageView>(R.id.imageHeaderView)
        Picasso.with(this).load(account.photoUrl).resize(150, 150).centerCrop().into(imageView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()

        account = GoogleSignIn.getLastSignedInAccount(this)!!

        floatingActionButton.setOnClickListener(View.OnClickListener { v ->
            var intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
            updateUI()
        })

        updateUI()
    }

    fun añadirUsuario(){
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                var flag = false
                var children = p0!!.children
                children.forEach {
                    if (it.key == account?.id.toString()) flag = true
                }
                if (flag) {
                    var usuario = User(account.id.toString(), account.displayName.toString())
                    var mapa = HashMap<String, User>()
                    mapa.put(account?.id.toString(), usuario)
                    userRef.setValue(mapa as Map<String, User>)

                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                //Ignore
            }
        })
    }

    fun eliminarHabito(hashMap:Any) {
        //Toast.makeText(this, idHabit, Toast.LENGTH_LONG).show()
        //habitRef.child(idHabit).child("activo").setValue(false)
        //updateUI()
    }

    fun modificarHabito(hashMap:Any) {

        var lag = hashMap.toString().split(
                delimiters = "="
        )
        var nameLag2 = lag[1].split(
                delimiters = ","
        )
        var name = nameLag2[0]


        var idLag = lag[2]
        var id = idLag.substring(
                startIndex = 0,
                endIndex = idLag.length-1
        )

        var myIntent = Intent(this, Main2Activity::class.java)
        myIntent.putExtra("id", id)
        myIntent.putExtra("name", name)
        startActivity(myIntent)
    }
    fun updateUI(){
        habitRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                var children = p0?.children!!
                var listaHabitosDelUsuario = HashMap<String, Habit>()
                children.forEach {
                    if (it.child("owner").getValue().toString()==account.id.toString() && it.child("activo").getValue() == true){
                        listaHabitosDelUsuario.put(it.key, it.getValue<Habit>(Habit::class.java)!!)
                    }
                }

                cargarPantalla(listaHabitosDelUsuario)
            }

            override fun onCancelled(p0: DatabaseError?) {
                //Ignore
            }
        })
    }

    private fun cargarPantalla(listaHabitosDelUsuario: HashMap<String, Habit>) {
        listaContentMain.adapter = MyCustomAdapter(this, listaHabitosDelUsuario, account.id.toString())

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        var inflater = menuInflater
        inflater.inflate(R.menu.contex_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        //return super.onContextItemSelected(item)
        var info = item?.menuInfo as AdapterView.AdapterContextMenuInfo

        when (item?.itemId) {
            R.id.modificarHabito -> modificarHabito(listaContentMain.getItemAtPosition(info.id.toInt()))
            R.id.eliminarHabito -> eliminarHabito(listaContentMain.getItemAtPosition(info.id.toInt()))
            else -> {Toast.makeText(this, "Por lo menos se ha entrado", Toast.LENGTH_LONG).show()}
        }
        return true
    }

    //Clase encargada de configurar nuestro textView
    private class MyCustomAdapter(context:Context, habitos:HashMap<String, Habit>, owner:String): BaseAdapter(){

        private val mContext: Context
        private var habitosLag = TreeMap<String, Habit>()
        private val owner = owner
        private val habitos = habitos

        private val names = ArrayList<String>()
        private val frecuencia = ArrayList<HashMap<String, Boolean>>()

        init {
            this.mContext = context
            habitosLag.putAll(habitos)
            habitosLag.forEach {
                names.add(it.value.name)
                frecuencia.add(it.value.frecuencia)
            }
        }
        override fun getCount(): Int {
            return names.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any{
            var nombre = names.get(position)
            habitos.forEach {
                if (it.value.owner == owner && it.value.name == nombre){
                    var array = HashMap<String, String>()
                    array.put("id", it.key)
                    array.put("nombre", it.value.name)
                    return array
                }
            }
            return "No hay nada"
        }

        //metodo responsable del renderear cada fila
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_main, viewGroup, false)

            val nameTextView = rowMain.findViewById<TextView>(R.id.name_TextView)
            val lunesTextView = rowMain.findViewById<TextView>(R.id.frecuency_TextView)
            val martesTextView = rowMain.findViewById<TextView>(R.id.textView)
            val miercolesTextView = rowMain.findViewById<TextView>(R.id.textView2)
            val juevesTextView = rowMain.findViewById<TextView>(R.id.textView3)
            val viernesTextView = rowMain.findViewById<TextView>(R.id.textView4)
            val sabadoTextView = rowMain.findViewById<TextView>(R.id.textView5)
            val domingoTextView = rowMain.findViewById<TextView>(R.id.frecuency_textView6)

            nameTextView.setText(names.get(position))
            if (frecuencia.get(position)["lunes"]!!){
                lunesTextView.setTextColor(Color.BLUE)
                lunesTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["martes"]!!){
                martesTextView.setTextColor(Color.BLUE)
                martesTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["miercoles"]!!){
                miercolesTextView.setTextColor(Color.BLUE)
                miercolesTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["jueves"]!!){
                juevesTextView.setTextColor(Color.BLUE)
                juevesTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["viernes"]!!){
                viernesTextView.setTextColor(Color.BLUE)
                viernesTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["sabado"]!!){
                sabadoTextView.setTextColor(Color.BLUE)
                sabadoTextView.setTypeface(null, Typeface.BOLD)
            }
            if (frecuencia.get(position)["domingo"]!!){
                domingoTextView.setTextColor(Color.BLUE)
                domingoTextView.setTypeface(null, Typeface.BOLD)
            }

            return rowMain
        }
    }
}
