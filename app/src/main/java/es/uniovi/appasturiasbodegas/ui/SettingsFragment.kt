package es.uniovi.appasturiasbodegas.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import es.uniovi.appasturiasbodegas.R
import java.util.Locale


class SettingsFragment : PreferenceFragmentCompat() {
    // Este fragmento se encarga de mostrar y gestionar las preferencias de la aplicación
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el botón de retroceso para navegar a HomeFragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    // Este método se llama para crear las preferencias desde un recurso XML
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Configurar el cambio de idioma
        val languagePreference = findPreference<ListPreference>("language_switch")
        languagePreference?.setOnPreferenceChangeListener { _, newValue ->
            // Cambiar el idioma de la aplicación
            // Se obtiene el nuevo valor seleccionado
            val selectedLanguage = newValue as String
            // Se establece el nuevo locale y se actualiza la configuración de la aplicación
            val locale = Locale(selectedLanguage)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            // Se actualiza la configuración de recursos con el nuevo locale
            val context: Context = requireActivity().applicationContext.createConfigurationContext(config)
            requireActivity().resources.updateConfiguration(config, context.resources.displayMetrics)

            // Reiniciar actividad para aplicar cambios de idioma
            requireActivity().recreate()
            true
        }
    }
}