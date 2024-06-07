package com.example.kessekolah.ui.core.beranda.materi

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.atwa.filepicker.core.FilePicker
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.FragmentAddMateriBinding
import com.example.kessekolah.ui.adapter.IlusPickerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class AddMateriFragment : Fragment() {
    private var _binding: FragmentAddMateriBinding? = null

    private val binding get() = _binding!!

    private val listIlus = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private var numberIlus = 0
    private var file: File? = null
    private val filePicker = FilePicker.getInstance(this)
    private lateinit var auth: FirebaseAuth
    private val materiRef = FirebaseDatabase.getInstance().getReference("materi")
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

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
                val mJudul = textJudulMateri.text.toString().trim()
                val mTahun = textTahun.text.toString().trim()

                if (mJudul.isEmpty() || mTahun.isEmpty() || numberIlus == 0 || file == null) {
                    Toast.makeText(
                        requireContext(),
                        "Lengkapi inputan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Upload data to firebase
                    uploadData(mJudul, mTahun, file!!)
                }
            }
        }
    }

    private fun uploadData(judul: String, tahun: String, file: File) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val fileId = UUID.randomUUID().toString()
        val fileRef = storage.child("materi/${user.uid}/$fileId.pdf")

        val metadata = storageMetadata {
            setCustomMetadata("owner", user.uid)
        }

        fileRef.putFile(Uri.fromFile(file), metadata)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val materiData = MateriData(
                        judul = judul,
                        tahun = tahun,
                        fileName = "$fileId.pdf",
                        fileUrl = uri.toString(),
                        timestamp = getCurrentDateTime(),
                        uid = user.uid
                    )

                    materiRef.child(fileId)
                        .setValue(materiData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Materi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            } else {
                                Toast.makeText(requireContext(), "Gagal menambahkan materi", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UploadData", "Gagal mengunggah file", e)
                Toast.makeText(requireContext(), "Gagal mengunggah file", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    private fun pickPdf() {

        filePicker.pickPdf { meta ->
            val sizeKb: Int? = meta?.sizeKb
            val file: File? = meta?.file

            binding.btnAddFile.text = meta?.name ?: "nama null"
            this.file = file

        }
    }
}