package com.example.kessekolah.ui.core.beranda.materi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.atwa.filepicker.core.FilePicker
import com.example.kessekolah.databinding.FragmentAddMateriBinding
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import java.io.File
import kotlin.properties.Delegates


class AddMateriFragment : Fragment() {
    private var _binding: FragmentAddMateriBinding? = null

    private val binding get() = _binding!!

    private val listIlus = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus = 0;
    private var file: File? = null

    private val filePicker = FilePicker.getInstance(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData(listIlus)
        buttonAction()
    }

    private fun setData(data: List<Int>) {
        val listAdapter = IlusPickerAdapter(data)

        with(binding) {
            rvIlus.layoutManager = GridLayoutManager(requireContext(), 2)
            rvIlus.adapter = listAdapter

            listAdapter.setOnItemClickCallback(object: IlusPickerAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Int) {
                    // get data ilus, Int type
                    numberIlus = data
                    tvPilihIlustrasi.text = "Ilustrasi $data dipilih!"
                }

            })
        }
    }

    private fun buttonAction() {
        with(binding) {
            btnAddFile.setOnClickListener { pickPdf() }

            btnSubmit.setOnClickListener {
                val mJudul = textJudulMateri.toString().trim().isNullOrEmpty()
                val mTahun = textTahun.toString().trim().isNullOrEmpty()

                if(mJudul || mTahun || (numberIlus == 0) || (file == null)) {
                    Toast.makeText(
                        requireContext(),
                        "Lengkapi inputan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //input data to firebase

                }
            }
        }
    }

    private fun pickPdf() {

        filePicker.pickPdf() { meta ->
            val name: String? = meta?.name
            val sizeKb: Int? = meta?.sizeKb
            val file: File? = meta?.file

            binding.btnAddFile.text = if (name == null) name else "nama null"
            this.file = file

        }
    }
}