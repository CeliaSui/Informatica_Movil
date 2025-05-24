package es.uniovi.appasturiasbodegas

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.uniovi.appasturiasbodegas.databinding.ActivityMainBinding
import java.util.Locale
import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    // Cambia el idioma de la aplicación
    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode) // Crear un objeto Locale con el código de idioma
        Locale.setDefault(locale) // Establecer el locale por defecto
        val config = resources.configuration // Obtener la configuración actual de recursos
        config.setLocale(locale) // Establecer el locale en la configuración
        config.setLayoutDirection(locale) // Establecer la dirección del layout según el locale
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics) // Actualizar la configuración de recursos
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Poner valores por defecto de las preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        // Obtener las preferencias guardadas
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Cambiar el idioma de la aplicación según la preferencia almacenada
        val languageCode = sharedPreferences.getString("language_switch", "es") ?: "es"
        setAppLocale(languageCode)

        // Cambiar el tema según la preferencia almacenada
        val isDarkMode = sharedPreferences.getBoolean("theme_switch", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)

        // Configurar diseño de borde a borde porque no me dejaba usar la funcion enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            // Para versiones anteriores a Android 11, usamos el sistema de UI flags
            // para ocultar la barra de navegación y la barra de estado
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
        // Cambiar el color de la barra de estado y la barra de navegación
        WindowCompat.setDecorFitsSystemWindows(window, false) // Permite el diseño de borde a borde
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary) // Cambia el color de la barra de estado
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary) // Cambia el color de la barra de navegación
        // En teoria estan deprecated estos metodos a partir de API35, pero no encontré la forma de hacerlo para APIs superiores

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Ajusta el padding superior del Toolbar según la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(
                view.paddingLeft,
                statusBarInsets.top, // paddingTop dinámico para vertical que no se solape con la barra de estado
                navBarInsets.right, // paddingEnd dinámico para horizontal que no se solape con la barra de navegación
                view.paddingBottom
            )
            insets
        }
        // Ajusta el padding inferior del BottomNavigationView según la barra de navegación
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { view, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                navBarInsets.bottom // paddingBottom dinámico para que no se solape con la barra de navegación
            )
            insets
        }



        // Establecer el Toolbar
        setSupportActionBar(binding.toolbar)

        // Navegacion
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment // Obtener el NavHostFragment
        val navController = navHostFragment.navController // Obtener el NavController del NavHostFragment

        // Configurar el Bottom Navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Configurar el NavController con el ActionBar
        setupActionBarWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()
        try {
            // Registrar el listener de preferencias
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        } catch (e: Exception) {
            Log.e("MY_DEBUG", "Error al registrar listener de preferencias", e)
        }
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el listener de preferencias
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onSupportNavigateUp(): Boolean {
        // Manejar la navegación hacia arriba con el NavController
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?)
    {
        // Verificar si las preferencias han cambiado
        if (key == "theme_switch") {
            // Aquí gestionas el cambio de tema (si se guarda en preferencias)
            val isDarkMode = sharedPreferences?.getBoolean(key, false) ?: false
            if (isDarkMode) {
                Log.d("MY_DEBUG", "Modo oscuro activado")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                Log.d("MY_DEBUG", "Modo oscuro desactivado")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        else if (key == "language_switch") {
            // Aquí gestionas el cambio de idioma
            val languageCode = sharedPreferences?.getString(key, "es") ?: "es" // Valor por defecto "es" (español)
            setAppLocale(languageCode) // Cambia el idioma de la aplicación
            Log.d("MY_DEBUG", "Idioma cambiado a: $languageCode")
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Verificar si la actividad tiene el foco, que significa que está visible y activa
        if (hasFocus) {
            val hideNavBar = sharedPreferences.getBoolean("hide_nav_bar", false) // Obtener la preferencia de ocultar la barra de navegación
            setNavigationBarVisibility(hideNavBar)
        }
    }

    fun setNavigationBarVisibility(hide: Boolean) {
        // Configurar la visibilidad de la barra de navegación según la preferencia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+
            window.insetsController?.let { controller ->
                if (hide) {
                    controller.hide(WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                else {
                    controller.show(WindowInsets.Type.statusBars())
                }
            }
        }
        else {
            // API 21-29
            @Suppress("DEPRECATION")
            if (hide) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            else // Se muestra la barra de navegación
            {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }
}