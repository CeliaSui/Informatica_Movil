package es.uniovi.appasturiasbodegas.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import es.uniovi.appasturiasbodegas.adapters.BodegaDataListAdapter
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.databinding.FragmentHomeBinding
import es.uniovi.appasturiasbodegas.domain.favoritos.BodegaFavoritosViewModel
import es.uniovi.appasturiasbodegas.domain.favoritos.BodegaFavoritosViewModelFactory
import es.uniovi.appasturiasbodegas.domain.home.BodegaViewModel
import es.uniovi.appasturiasbodegas.model.BodegaDatabase
import es.uniovi.appasturiasbodegas.domain.home.BodegaViewModelFactory
import es.uniovi.appasturiasbodegas.utils.observeOnce

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bodegaDataListAdapter: BodegaDataListAdapter

    // ViewModel para obtener los datos de las bodegas
    private val bodegaViewModel: BodegaViewModel by activityViewModels {
        val application = requireNotNull(this.activity).application // Obtener la aplicación
        val bodegaDAO = BodegaDatabase.getDatabase(application)!!.bodegaDAO() // Obtener el DAO de la base de datos
        BodegaViewModelFactory(bodegaDAO) // Crear el ViewModel
    }

    // ViewModel para gestionar los favoritos
    private val bodegaFavoritosViewModel: BodegaFavoritosViewModel by activityViewModels {
        val application = requireNotNull(this.activity).application // Obtener la aplicación
        val bodegaDAO = BodegaDatabase.getDatabase(application)!!.bodegaDAO() // Obtener el DAO de la base de datos
        BodegaFavoritosViewModelFactory(BodegaRepository(bodegaDAO)) // Crear el ViewModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the menu in this fragment

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            // Configurar el RecyclerView
            initializeRecyclerView()

            // Observar el estado de la UI
            bodegaViewModel.appUIStateObservable.observe(viewLifecycleOwner) { state ->
                when (state) {
                    // Estado de carga
                    is AppUIState.Loading -> {
                        binding.homeProgressBar.visibility = View.VISIBLE
                    }
                    // Estado de éxito
                    is AppUIState.Success -> {
                        binding.homeProgressBar.visibility = View.GONE
                    }
                    // Estado de error
                    is AppUIState.Error -> {
                        binding.homeProgressBar.visibility = View.GONE
                        val errorMessage = getString(R.string.error_generico_con_mensaje, state.message)
                        showErrorSnackbar(errorMessage)
                    }
                }
            }

            // Observar los datos de las bodegas
            bodegaViewModel.getBodegas().observe(viewLifecycleOwner, Observer { bodegas ->
                if (bodegas != null)
                {
                    if(bodegas.isEmpty())
                    {
                        // Si las bodegas estan vacias, hay que mostrar un mensaje
                        bodegaDataListAdapter.submitList(emptyList()) // Actualizar lista
                        binding.noResultsTextView.visibility = View.VISIBLE // Mostrar texto de "sin resultados"
                    }
                    else
                    {
                        bodegaDataListAdapter.submitList(bodegas) // Actualizar lista
                        binding.noResultsTextView.visibility = View.GONE // Ocultar texto
                    }
                    binding.swiper.isRefreshing = false
                }
                else
                {
                    // Manejar el caso de error
                    val e = getString(R.string.error_cargar_datos)
                    showErrorSnackbar(e)
                }
            })

            // Configurar SwipeRefreshLayout
            binding.swiper.setOnRefreshListener {
                if (!binding.swiper.isRefreshing) {
                    binding.swiper.isRefreshing = true
                    bodegaViewModel.getBodegaData()
                }
            }

            // Filtros segun zona y concejo
            bodegaViewModel.bodegasFiltradas.observe(viewLifecycleOwner) { lista ->
                //bodegaDataListAdapter.submitList(lista)
                if (lista.isNullOrEmpty()) {
                    binding.noResultsTextView.visibility = View.VISIBLE // Mostrar texto de "sin resultados"
                    bodegaDataListAdapter.submitList(emptyList())
                } else {
                    binding.noResultsTextView.visibility = View.GONE // Ocultar texto
                    bodegaDataListAdapter.submitList(lista) // Actualizar lista
                }
            }


        } catch (e: Exception) {
            val errorMessage = getString(R.string.error_generico_con_mensaje, e.message)
            showErrorSnackbar(errorMessage)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Create the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        // Funcionalidad de búsqueda
        val searchItem = menu.findItem(R.id.action_search) // Buscar el elemento de menú de búsqueda
        val searchView = searchItem.actionView as SearchView // Obtener el SearchView del elemento de menú
        val search = getString(R.string.buscar_bodega)
        searchView.queryHint = search // Establecer el texto de sugerencia
        // Establecer el icono de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Función que se llama al enviar la consulta
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchBodegas(it) // Llama a la función de búsqueda
                }
                return true
            }

            // Función que se llama al cambiar el texto
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchBodegas(it)
                }
                return true
            }
        })
    }

    // Función para buscar bodegas por nombre
    fun searchBodegas(query: String) {
        bodegaViewModel.searchBodegas(query).observe(viewLifecycleOwner, Observer { bodegas ->
            val vacia = bodegas.isNullOrEmpty()
            if (vacia) {
                binding.noResultsTextView.visibility = View.VISIBLE // Muestra el mensaje de no resultados
                bodegaDataListAdapter.submitList(emptyList())
                binding.homeProgressBar.visibility = View.GONE // Ocultar barra de progreso
            } else {
                binding.noResultsTextView.visibility = View.GONE // Oculta el mensaje
                bodegaDataListAdapter.submitList(bodegas) // Actualiza la lista
                binding.homeProgressBar.visibility = View.GONE // Ocultar barra de prrogreso
            }
        })
    }

    // Función para manejar la selección de elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Maneja la selección de elementos del menú
        return when (item.itemId) {
            R.id.settingsFragment -> {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                true
            }
            R.id.mapFragment -> {
                findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
                true
            }
            R.id.favoritesFragment -> {
                findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
                true
            }
            R.id.action_filter -> {
                // Colapsar y limpiar el foco del SearchView antes de abrir el diálogo de filtro para evitar que el buscador se quede abierto, con el teclado abierto y demás
                val activity = requireActivity() // Obtener la actividad actual
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar) // Obtener la barra de herramientas
                val menu = toolbar.menu // Obtener el menú de la barra de herramientas
                val searchItem = menu.findItem(R.id.action_search) // Buscar el elemento de menú de búsqueda
                val searchView = searchItem?.actionView as? SearchView // Obtener el SearchView
                // Colapsar el SearchView y limpiar el foco
                searchView?.let {
                    it.setQuery("", false) // Limpiar la consulta actual
                    it.clearFocus() // Limpiar el foco del SearchView
                    it.isIconified = true // Colapsar el SearchView
                }
                searchItem?.collapseActionView() // Colapsar el SearchView para asegurarse de que no esté abierto y se muestre de nuevo el menú

                // Obtener las zonas y concejos de la base de datos
                // Se utiliza observeOnce para evitar múltiples observaciones
                bodegaViewModel.getZonasYConcejos().observeOnce(viewLifecycleOwner) { zonasYConcejos ->

                    // Obtener las zonas y concejos de la lista
                    val zonas = zonasYConcejos?.first ?: emptyList()
                    val concejos = zonasYConcejos?.second ?: emptyList()

                    // Mostrar el diálogo de filtro solo si no está ya visible
                    if (parentFragmentManager.findFragmentByTag("FilterDialog") == null) {
                        val dialog = FilterDialogFragment(zonas, concejos) { zonaSeleccionada, concejoSeleccionado -> // Aplicar el filtro
                            bodegaViewModel.aplicarFiltro(zonaSeleccionada, concejoSeleccionado)
                        }
                        dialog.show(parentFragmentManager, "FilterDialog") // Mostrar el diálogo
                    }

                }
                true
            }
            else -> super.onOptionsItemSelected(item) // Llama al método de la superclase para manejar otros elementos del menú
        }
    }

    // Inicializa el RecyclerView y su adaptador
    private fun initializeRecyclerView()
    {
        // Inicializa el adaptador
        bodegaDataListAdapter = BodegaDataListAdapter(
            onItemClicked = { position ->
                // Obtiene la posición del elemento clicado
                val list = bodegaDataListAdapter.currentList

                // Verifica si la posición está dentro de los límites de la lista para evitar IndexOutOfBoundsException
                if (position in list.indices) {
                    val bodega = list[position] // Obtiene el bodega de la lista
                    val bundle = Bundle().apply {
                        putInt("bodegaId", bodega.id) // Pasa el ID del bodega al fragmento de detalle
                    }
                    Log.d("MY_DEBUG", "ID del bodega seleccionado: ${bodega.id}")
                    findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
                } else {
                    Log.e("MY_DEBUG", "Posición fuera de rango: $position")
                }

            },
            onFavClick = { id, isFavorite ->
                // Cambia el estado del favorito
                bodegaFavoritosViewModel.setFavorito(id, isFavorite)
            }
        )

        // Configura el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context) // Establece el LayoutManager
        binding.recyclerView.adapter = bodegaDataListAdapter // Establece el adaptador
    }

    // Muestra el Snackbar con el mensaje de error
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

    }
}