package com.example.kessekolah.ui.core.beranda.materi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentListMateriBinding
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.MateriListAdapter

class ListMateriFragment : Fragment() {
    private var _binding: FragmentListMateriBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListMateriViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MateriListAdapter()
        binding.rvMateri.adapter = adapter
        binding.rvMateri.layoutManager = LinearLayoutManager(requireContext())

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            materiList?.let {
                adapter.submitList(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.rvMateri.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.GONE
                binding.rvMateri.visibility = View.VISIBLE
            }
        }

        binding.btnTambahMateri.setOnClickListener {
            // Handle button click
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

