package com.canunaldi.cuntrapp

import android.app.Notification
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.system.Os.remove
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_cuntr.*

class CuntrActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var navController:NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuntr)
        auth= FirebaseAuth.getInstance()

        bottom_navigation.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController=navHostFragment.findNavController()

        bottom_navigation.setupWithNavController(navController)
        nav_view.setupWithNavController(navController)

        appBarConfiguration= AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.discoverFragment,R.id.userFragment),
            drawer_layout
        )
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController,appBarConfiguration)
    }
    private val bottomNavItemSelectedListener=BottomNavigationView.OnNavigationItemSelectedListener { item->
        item.onNavDestinationSelected(navController)
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.options_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add_private_cuntr){
            item.onNavDestinationSelected(navController)
        }
        else if(item.itemId==R.id.add_shared_cuntr){
            item.onNavDestinationSelected(navController)
        }
        else if(item.itemId==R.id.add_public_cuntr){
            item.onNavDestinationSelected(navController)
        }
        else if(item.itemId==R.id.logout){
            auth.signOut()
            Toast.makeText(applicationContext,"Çıkış yapıldı.", Toast.LENGTH_LONG).show()
            val intent= Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}