package com.example.aplicationpaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.aplicationpaw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "")

        val navView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Configuración de navegación
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.bibliotecaFragment, R.id.perfilFragment,
                R.id.paseosFragment, R.id.veterinariaFragment, R.id.guarderiaFragment,
                R.id.homePaseadorFragment, R.id.detallePaseadorFragment
            )
        )
        // Ocultar la flecha hacia atrás en la barra de acción
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Navegar al fragmento adecuado basado en el rol del usuario
        navView.menu.clear()
        when (userRole) {
            "walker" -> {
                navView.inflateMenu(R.menu.bottom_nav_menu_walker)
                navController.navigate(R.id.homePaseadorFragment)
            }
            else -> {
                navView.inflateMenu(R.menu.bottom_nav_menu)
                navController.navigate(R.id.navigation_home)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestination = navController.currentDestination?.id
        // Devuelve false cuando estás en el PerfilPaseadorFragment
        return currentDestination != R.id.perfilPaseadorFragment && navController.navigateUp()
    }

}
