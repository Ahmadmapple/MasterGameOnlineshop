package com.example.mastergameonlineshop

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.mastergameonlineshop.databinding.ActivityMainBinding
import com.example.mastergameonlineshop.mainactivityfragment.Cart
import com.example.mastergameonlineshop.mainactivityfragment.Home
import com.example.mastergameonlineshop.mainactivityfragment.Library
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var toggle: ActionBarDrawerToggle

        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "Master Game Onlineshop"
        binding.navigationView.bringToFront()

        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open, R.string.close
        )
        toggle.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottomNavHome -> {
                    replaceFragment(Home())
                }
                R.id.bottomNavLibrary -> {
                    replaceFragment(Library())
                }
                R.id.bottomNavMyCart -> {
                    replaceFragment(Cart())
                }
                else -> {}
            }
            true
        }

        // === CEK apakah dari GamePage untuk buka Cart ===
        if (intent.getBooleanExtra("open_cart", false)) {
            replaceFragment(Cart())
            binding.bottomNavigation.selectedItemId = R.id.bottomNavMyCart
        } else {
            // default ke Home
            replaceFragment(Home())
            binding.bottomNavigation.selectedItemId = R.id.bottomNavHome
        }
        // === SELESAI ===
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.user_menubutton -> {
                val intent = Intent(this, UserInformationPage::class.java)
                startActivity(intent)
            }
            R.id.logout_menubutton -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, loginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }
}
