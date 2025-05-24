package es.uniovi.appasturiasbodegas.ui

import android.R
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import es.uniovi.appasturiasbodegas.databinding.FragmentFilterDialogBinding

class FilterDialogFragment(
    private val zonas: List<String>,
    private val concejos: List<String>,
    private val onFilterApply: (zona: String?, concejo: String?) -> Unit
) : DialogFragment() {

    private var _binding: FragmentFilterDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_filter_dialog, container, false)
        _binding = FragmentFilterDialogBinding.inflate(inflater, container, false)

        // Crear el diálogo
        val ta = getString(es.uniovi.appasturiasbodegas.R.string.spinner_todas)
        val to = getString(es.uniovi.appasturiasbodegas.R.string.spinner_todos)
        val zonaSpin = listOf(ta) + zonas
        val concejoSpin = listOf(to) + concejos

        // Adaptador para el spinner de zonas
        binding.spinnerZona.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            zonaSpin
        )

        // Adaptador para el spinner de concejos
        binding.spinnerConcejo.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            concejoSpin
        )

        // Configurar el botón de aplicar filtro
        binding.buttonAplicar.setOnClickListener {
            val zona = binding.spinnerZona.selectedItem.toString().takeIf { it != ta } // Si no es "Todas" se selecciona
            val concejo = binding.spinnerConcejo.selectedItem.toString().takeIf { it != to } // Si no es "Todos" se selecciona
            onFilterApply(zona, concejo) // Aplicar filtro
            dismiss() // Cerrar ventana
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}