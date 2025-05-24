package es.uniovi.appasturiasbodegas.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import es.uniovi.appasturiasbodegas.R
import es.uniovi.appasturiasbodegas.adapters.BodegaDetailViewHolder
import es.uniovi.appasturiasbodegas.data.BodegaRepository
import es.uniovi.appasturiasbodegas.databinding.FragmentDetailBinding
import es.uniovi.appasturiasbodegas.domain.detalle.BodegaDetailsViewModel
import es.uniovi.appasturiasbodegas.domain.detalle.BodegaDetailsViewModelFactory
import es.uniovi.appasturiasbodegas.model.BodegaDatabase


class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var bodegaDetailsViewModel: BodegaDetailsViewModel?= null
    private var bodegaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // Obtener el bodegaId de los argumentos del fragmento
        arguments?.let {
            bodegaId = it.getInt("bodegaId", -1)
        }
        Log.d("DetailFragment", "Bodega ID: $bodegaId")
        if (bodegaId == -1)
        {
            // Manejar el caso en que el bodegaId no es válido
            Log.e("DetailFragment", "Bodega ID no válido")
            activity?.onBackPressed() // Volver a la pantalla anterior si no se ha pasado un ID válido
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Obtener una referencia al ViewModel y observar los datos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inicializar el ViewModel y la base de datos
        val application = requireNotNull(this.activity).application // Contexto de la aplicación
        val bodegaDAO = BodegaDatabase.getDatabase(application)!!.bodegaDAO() // Obtener el DAO de Bodega desde la base de datos
        val bodegaRepository = BodegaRepository(bodegaDAO) // Crear una instancia del repositorio de Bodega
        // Crear el ViewModelFactory con el bodegaRepository y el bodegaId
        val factory = BodegaDetailsViewModelFactory(bodegaRepository, bodegaId) // Pasar el repositorio y el ID de la bodega al ViewModelFactory
        val viewModel: BodegaDetailsViewModel by viewModels { factory } // Usar el ViewModelFactory para crear el ViewModel
        bodegaDetailsViewModel = viewModel

        // Observe the LiveData from the ViewModel
        // Usar una variable local inmutable para evitar el error de smart cast
        val localViewModel = bodegaDetailsViewModel // Obtener una referencia al ViewModel localmente
        val viewHolder = BodegaDetailViewHolder(binding) // Crear una instancia del ViewHolder con el binding
        localViewModel?.mBodega?.observe(viewLifecycleOwner, Observer { bodega ->
            Log.d("DetailFragment", "Bodega recibido: $bodega")
            viewHolder.bind(bodega)
        })

        // Sobrescribir el comportamiento del botón de atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homeFragment -> {
                findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
                true
            }
            R.id.mapFragment -> {
                findNavController().navigate(R.id.action_detailFragment_to_mapFragment)
                true
            }
            R.id.favoritesFragment -> {
                findNavController().navigate(R.id.action_detailFragment_to_favoritesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}