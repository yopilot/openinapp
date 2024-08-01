package com.example.openinapp

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.openinapp.ui.CampaignFragment
import com.example.openinapp.ui.CoursesFragment
import com.example.openinapp.ui.LinkFragment
import com.example.openinapp.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navView: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        navView = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)

        // Dynamically adjust FloatingActionButton position
        fab.post {
            val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.anchorId = R.id.nav  // Adjust 'nav' to your BottomAppBar ID if different
            layoutParams.anchorGravity = Gravity.CENTER or Gravity.TOP
            fab.layoutParams = layoutParams
        }

        // Setup fragment initial display
        if(savedInstanceState == null){
            replaceFragment(LinkFragment())
        }

        // Setup BottomNavigationView
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_links -> {
                    replaceFragment(LinkFragment())
                    true
                }
                R.id.navigation_courses -> {
                    replaceFragment(CoursesFragment())
                    true
                }
                R.id.navigation_campaign -> {
                    replaceFragment(CampaignFragment())
                    true
                }
                R.id.navigation_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }


        // Handling edge-to-edge UI tweaks
        ViewCompat.setOnApplyWindowInsetsListener(navView) { view, insets ->
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = navigationBarsInsets.bottom
            }
            insets
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navhost, fragment)
            .commit()
    }
}
