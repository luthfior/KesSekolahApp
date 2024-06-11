package com.example.kessekolah.ui.core.beranda.materi.editMateri

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriList
import com.example.kessekolah.databinding.FragmentAddMateriBinding
import com.example.kessekolah.databinding.FragmentEditMateriBinding
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class EditMateriFragment : Fragment() {

    private var _binding: FragmentEditMateriBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditMateriViewModel by viewModels()
    private val args : EditMateriFragmentArgs by navArgs()

    private lateinit var data: MateriList
    private val listIlus = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = args.data
        numberIlus = data.icon


        setupData()
        buttonClick()
        loadingHandler()
    }

    private fun setupData() = with(binding.inForm) {
        textJudulMateri.setText(data.title)
        textTahun.setText(data.tahun)
        tvPilihIlustrasi.setText("Ilustrasi ${data.icon} dipilih!")
        btnAddFile.setText(data.fileName)

        val listAdapter = IlusPickerAdapter(listIlus)
        rvIlus.layoutManager = GridLayoutManager(requireContext(), 4)
        rvIlus.adapter = listAdapter

        listAdapter.setOnItemClickCallback(object: IlusPickerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Int) {
                // get data ilus, Int type
                numberIlus = data
                tvPilihIlustrasi.text = "Ilustrasi $data dipilih!"
            }

        })
    }

    private fun buttonClick() = with(binding){
        btnSubmit.setOnClickListener {

            val mJudul = inForm.textJudulMateri.text.toString().trim()
            val mTahun = inForm.textTahun.text.toString().trim()

            if (mJudul.isEmpty() || mTahun.isEmpty() || numberIlus == 0) {
                Toast.makeText(
                    requireContext(),
                    "Lengkapi inputan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // edit data firebase
                viewLifecycleOwner.lifecycleScope.launch {
                    val isError = viewModel.editMateri(mJudul, mTahun, numberIlus, data.fileName.removeSuffix(".pdf"))
                    responseHandler(isError)
                }
            }
        }
    }

    private fun responseHandler(isError: Boolean) {
        if (isError) {
            Toast.makeText(requireContext(), "Data materi gagal dirubah", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Data materi berhasil dirubah", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun loadingHandler() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Data..")
        progressDialog.setCancelable(false)
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) progressDialog.show() else progressDialog.dismiss()
        }
    }

}