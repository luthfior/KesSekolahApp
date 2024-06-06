package com.example.kessekolah.ui.core.beranda.materi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentHome2Binding
import com.example.kessekolah.databinding.FragmentListMateriBinding

class ListMateriFragment : Fragment() {

    private var _binding: FragmentListMateriBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambahMateri.setOnClickListener {
            findNavController().navigate(R.id.action_listMateriFragment_to_addMateriFragment)
        }

    }
}