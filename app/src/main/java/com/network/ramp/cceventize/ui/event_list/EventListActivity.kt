package com.network.ramp.cceventize.ui.event_list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.network.ramp.cceventize.R
import com.network.ramp.cceventize.data.model.RegisterUser
import com.network.ramp.cceventize.databinding.ActivityEventListBinding
import com.network.ramp.cceventize.ui.event_crud.EventActivity

class EventListActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEventListBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // Initialization function for event / ticket list screen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        setSupportActionBar(binding.appBarEventList.toolbar)

        binding.appBarEventList.addEvent.setOnClickListener { view ->
            val intent = Intent(this, EventActivity::class.java)
            startActivity(intent)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_event_list)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.all_events, R.id.my_tickets, R.id.my_events
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Fill navigation view with information about logged user (full name and email)
        val currentUser = auth.currentUser?.uid
        val header = navView.getHeaderView(0)
        val tvUserFullName = header.findViewById<TextView>(R.id.user_full_name)
        val tvUserEmail = header.findViewById<TextView>(R.id.user_email)
        if (currentUser != null) {
            database.child("users").child(currentUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(RegisterUser::class.java)
                            if (user != null) {
                                val text = java.lang.String.format("%s %s", user.first_name, user.last_name)
                                tvUserFullName.setText(text)
                                tvUserEmail.setText(user.email)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.event_list, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_event_list)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_logout -> {
                // Firebase user logout
                FirebaseAuth.getInstance().signOut()

                // Close app
                finishAffinity()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}