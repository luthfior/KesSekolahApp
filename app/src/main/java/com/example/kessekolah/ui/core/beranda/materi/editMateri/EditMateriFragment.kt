package com.example.kessekolah.ui.core.beranda.materi.editMateri

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentAddMateriBinding
import com.example.kessekolah.databinding.FragmentEditMateriBinding


class EditMateriFragment : Fragment() {

    private var _binding: FragmentEditMateriBinding? = null

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}