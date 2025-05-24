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
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.adapters.BodegaDataListAdapter
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.databinding.FragmentFavoritesBinding
import es.uniovi.appasturiasbodegas.domain.favoritos.BodegaFavoritosViewModel
import es.uniovi.appasturiasbodegas.domain.favoritos.BodegaFavoritosViewModelFactory
import es.uniovi.appasturiasbodegas.model.BodegaDatabase
import es.uniovi.appasturiasbodegas.utils.observeOnce

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var bodegaDataListAdapter: BodegaDataListAdapter // Adaptador para el RecyclerView

    // ViewModel para manejar la lógica de negocio
    private val bodegaFavoritosViewModel: BodegaFavoritosViewModel by activityViewModels {
        val application = requireNotNull(this.activity).application // Obtener la aplicación
        val bodegaDAO = BodegaDatabase.getDatabase(application)!!.bodegaDAO() // Obtener el DAO de la base de datos
        BodegaFavoritosViewModelFactory(BodegaRepository( bodegaDAO)) // Crear el ViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el RecyclerView
        initializeRecyclerView()

        // Observar los bodegas favoritas
        bodegaFavoritosViewModel.bodegasFavoritos.observe(viewLifecycleOwner, Observer { bodegas ->
            // Actualizar la lista de bodegas favoritas en el adaptador
            if (bodegas != null)
            {
                if(bodegas.isEmpty())
                {
                    // Si la lista está vacía, mostrar el mensaje de "sin resultados"
                    bodegaDataListAdapter.submitList(emptyList()) // Actualizar lista vacía
                    binding.noResultsTextViewFavorites.visibility = View.VISIBLE // Mostrar texto de "sin resultados"
                }
                else
                {
                    // Si la lista no está vacía, ocultar el mensaje de "sin resultados"
                    bodegaDataListAdapter.submitList(bodegas) // Actualizar lista
                    binding.noResultsTextViewFavorites.visibility = View.GONE // Ocultar texto
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

        // Filtros segun zona y concejo
        bodegaFavoritosViewModel.bodegasFiltradas.observe(viewLifecycleOwner) { lista ->
            //bodegaDataListAdapter.submitList(lista)
            if (lista.isNullOrEmpty()) {
                // Si la lista filtrada está vacía, mostrar el mensaje de "sin resultados"
                binding.noResultsTextViewFavorites.visibility = View.VISIBLE // Mostrar texto de "sin resultados"
                bodegaDataListAdapter.submitList(emptyList()) // Actualizar lista vacía
            } else {
                // Si la lista filtrada no está vacía, ocultar el mensaje de "sin resultados"
                binding.noResultsTextViewFavorites.visibility = View.GONE // Ocultar texto
                bodegaDataListAdapter.submitList(lista) // Actualizar lista
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bodegaFavoritosViewModel.limpiarFiltro() // Limpiar el filtro al salir del fragmento
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        // Funcionalidad de búsqueda
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView // Obtener el SearchView del menú
        val search = getString(R.string.buscar_bodega)
        // Establecer el texto de sugerencia
        searchView.queryHint = search
        // Configurar el listener para la búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Método llamado cuando se envía la consulta
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchBodegas(it) // Llamar a la función de búsqueda
                }
                searchView.clearFocus() // Limpiar el foco del SearchView
                return true
            }
            // Método llamado cuando el texto cambia
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchBodegas(it) // Llamar a la función de búsqueda
                }
                return true
            }
        })
    }

    // Función para buscar bodegas por nombre
    fun searchBodegas(query: String) {
        bodegaFavoritosViewModel.searchBodegas(query).observe(viewLifecycleOwner, Observer { bodegas ->
            val vacia = bodegas.isNullOrEmpty()
            if (vacia)
            {
                // Si la lista está vacía, mostrar el mensaje de "sin resultados"
                binding.noResultsTextViewFavorites.visibility = View.VISIBLE // Muestra el mensaje de no resultados
                bodegaDataListAdapter.submitList(emptyList()) // Actualizar la lista del adaptador a vacía
            }
            else
            {
                // Si la lista no está vacía, ocultar el mensaje de "sin resultados"
                binding.noResultsTextViewFavorites.visibility = View.GONE // Oculta el mensaje
                bodegaDataListAdapter.submitList(bodegas) // Actualizar la lista del adaptador con los resultados de la búsqueda
            }

        })
    }

    // Función para manejar la selección de elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeFragment -> {
                findNavController().navigate(R.id.action_favoritesFragment_to_homeFragment)
                true
            }
            R.id.settingsFragment -> {
                findNavController().navigate(R.id.action_favoritesFragment_to_settingsFragment)
                true
            }
            R.id.mapFragment -> {
                findNavController().navigate(R.id.action_favoritesFragment_to_mapFragment)
                true
            }
            R.id.action_filter -> {
                // Colapsar y limpiar el foco del SearchView antes de abrir el diálogo de filtro
                val activity = requireActivity()
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                val menu = toolbar.menu
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? SearchView
                searchView?.let {
                    it.setQuery("", false)
                    it.isIconified = true
                    it.clearFocus()
                }
                searchItem?.collapseActionView() // Colapsar el SearchView para asegurarse de que no esté abierto y se muestre de nuevo el menú

                // Obtener las zonas y concejos de la base de datos
                // Se usa observeOnce para evitar múltiples observaciones
                bodegaFavoritosViewModel.getZonasYConcejos().observeOnce(viewLifecycleOwner) { zonasYConcejos ->

                    // Obtener las zonas y concejos de la lista
                    val zonas = zonasYConcejos?.first ?: emptyList()
                    val concejos = zonasYConcejos?.second ?: emptyList()

                    // Mostrar el diálogo de filtro
                    // Verificar si el diálogo ya está abierto para evitar múltiples instancias
                    if (parentFragmentManager.findFragmentByTag("FilterDialog") == null) {
                        // Crear y mostrar el diálogo de filtro
                        val dialog = FilterDialogFragment(zonas, concejos) { zonaSeleccionada, concejoSeleccionado ->
                            bodegaFavoritosViewModel.aplicarFiltro(zonaSeleccionada, concejoSeleccionado)
                        }
                        dialog.show(parentFragmentManager, "FilterDialog") // Mostrar el diálogo
                    }

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Inicializa el RecyclerView
    private fun initializeRecyclerView() {
        bodegaDataListAdapter = BodegaDataListAdapter(
            onItemClicked = { position -> // Manejar el clic en un elemento de la lista
                // Manejar el clic en el elemento de la lista
                val bodega = bodegaDataListAdapter.currentList[position]
                val bundle = Bundle().apply {
                    putInt("bodegaId", bodega.id) // Pasar el ID de la bodega al siguiente fragmento
                }
                findNavController().navigate(R.id.action_favoritesFragment_to_detailFragment, bundle)
            },
            onFavClick = { id, isFavorite -> // Manejar el clic en el botón de favorito
                // Manejar el clic en el botón de favorito
                bodegaFavoritosViewModel.setFavorito(id, isFavorite)
            }
        )

        // Configurar el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context) // Usar LinearLayoutManager para la lista
        binding.recyclerView.adapter = bodegaDataListAdapter // Asignar el adaptador al RecyclerView
    }

    // Muestra el Snackbar con el mensaje de error
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

    }
}