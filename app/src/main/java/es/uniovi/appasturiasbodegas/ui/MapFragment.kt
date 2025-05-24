package es.uniovi.appasturiasbodegas.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import org.osmdroid.views.MapView
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.databinding.FragmentMapBinding
import es.uniovi.appasturiasbodegas.domain.home.BodegaViewModel
import es.uniovi.appasturiasbodegas.domain.home.BodegaViewModelFactory
import es.uniovi.appasturiasbodegas.model.Bodega
import es.uniovi.appasturiasbodegas.model.BodegaDatabase
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

   // Usamos activityViewModels para poder 'compartir' el mismo BodegaViewModel y asi no sobreescribir datos de favoritos
   private val bodegaViewModel: BodegaViewModel by activityViewModels {
       val application = requireNotNull(this.activity).application // Obtener la aplicación para pasarla al ViewModelFactory
       val bodegaDAO = BodegaDatabase.getDatabase(application)!!.bodegaDAO() // Obtener el DAO de Bodega
       BodegaViewModelFactory(bodegaDAO) // Crear el ViewModelFactory con el DAO
   }
    private lateinit var mapView: MapView // Declarar la variable mapView para el mapa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Configurar OSMDroid
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        // Mostrar la barra de progreso mientras se carga el mapa
        binding.mapProgressBar.visibility = View.VISIBLE

        // Configuración básica del mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK) // Configura programáticamente el tileSource
        mapView.setMultiTouchControls(true) // Habilitar controles multitáctiles
        mapView.setHorizontalMapRepetitionEnabled(false) // Deshabilitar repetición horizontal del mapa
        mapView.setVerticalMapRepetitionEnabled(false) // Deshabilitar repetición vertical del mapa
        mapView.controller.setZoom(15.0) // Nivel de zoom inicial
        mapView.controller.setCenter(GeoPoint(43.3669, -5.8510)) // Coordenadas iniciales (Oviedo)

        // Observar bodegas y añadir marcadores
        bodegaViewModel.getBodegas().observe(viewLifecycleOwner) { bodegas ->
            if (bodegas != null) {
                addMarkers(bodegas) // Añadir marcadores al mapa
                // Ocultar la barra de progreso después de agregar los marcadores
                binding.mapProgressBar.visibility = View.GONE
            }
        }

        // Hacer desaparecer el toolbar que hay en activity_main.xml
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el comportamiento del botón de retroceso
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_mapFragment_to_homeFragment)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.show() // Mostrar de nuevo el toolbar
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeFragment -> {
                findNavController().navigate(R.id.action_mapFragment_to_homeFragment)
                true
            }
            R.id.settingsFragment -> {
                findNavController().navigate(R.id.action_mapFragment_to_settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun addMarkers(bodegas: List<Bodega>) {
        // Limpiar marcadores existentes
        mapView.overlays.clear()

        // Añadir marcadores a partir de los bodegas
        bodegas.forEach { bodega ->
            bodega.articles.article.forEach { article ->
                // Obtener las coordenadas desde la base de datos
                val coordenadasStr = article.geolocalizacion.coordenadas.content
                if (!coordenadasStr.isNullOrBlank()) {
                    // Conseguir coordenadas
                    val coordenadasArray = coordenadasStr.split(",")
                    if (coordenadasArray.size == 2) { // Deben ser dos coordenadas
                        try {
                            // Convertir las coordenadas a números
                            val latitud = coordenadasArray[0].trim().toDouble()
                            val longitud = coordenadasArray[1].trim().toDouble()

                            // Crear un marcador con las coordenadas
                            val marker = Marker(mapView)

                            // Configurar el marcador con las coordenadas
                            marker.position = GeoPoint(latitud, longitud)

                            // Obtener el recurso de cadena para "No hay información disponible"
                            val noInfoDisponible = getString(R.string.map_no_horario)
                            val horario = if (article.informacion.horario.content.isNullOrBlank()) {
                                // Si no hay horario, mostrar "No hay información disponible"
                                noInfoDisponible
                            }
                            else
                            {
                                // Parsear el contenido HTML del horario
                                android.text.Html.fromHtml(article.informacion.horario.content).toString()
                            }

                            // Añadir al marcador el nombre del lugar y su horario si lo tiene
                            marker.title = "${article.nombre.content}\n${article.informacion.horario.title}:\n${horario}"
                            mapView.overlays.add(marker) // Añadir marcador

                        } catch (e: NumberFormatException) {
                            // Coordenadas inválidas, ignorar
                        }
                    }
                }
            }
        }
        // Actualizar el mapa
        mapView.invalidate() // Redibujar el mapa
    }
}