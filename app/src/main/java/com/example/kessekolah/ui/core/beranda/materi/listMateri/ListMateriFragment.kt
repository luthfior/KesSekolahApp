package com.example.kessekolah.ui.core.beranda.materi.listMateri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentListMateriBinding
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.MateriListAdapter
import com.example.kessekolah.ui.adapter.MateriListSiswaAdapter
import com.example.kessekolah.ui.core.beranda.HomeFragmentDirections
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ListMateriFragment : Fragment() {
    private var _binding: FragmentListMateriBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListMateriViewModel
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataLogin = LoginPreference(requireContext()).getData()
        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[ListMateriViewModel::class.java]

        if (dataLogin.role == "Guru") {
            itemListGuru()
        } else {
            itemListSiswa()
            binding.btnTambahMateri.visibility = View.GONE
        }
    }

    private fun itemListGuru() {
        val adapter = MateriListAdapter()
        binding.rvMateri.adapter = adapter
        binding.rvMateri.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : MateriListAdapter.OnItemClickCallback {

            override fun onDeleteClicked(data: MateriData) {
                showDialog(data)
            }

            override fun onEditClicked(data: MateriData) {
                val action = ListMateriFragmentDirections.actionListMateriFragmentToEditMateriFragment(data)
                findNavController().navigate(action)
            }

            override fun onItemClicked(data: MateriData) {
                val action = ListMateriFragmentDirections.actionListMateriFragmentToFlipBookTestFragment(data)
                findNavController().navigate(action)
            }

        })

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            viewDataEmpty(materiList.isEmpty())
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

        buttonCLick()
    }

    private fun itemListSiswa() {
        val adapter = MateriListSiswaAdapter()
        binding.rvMateri.adapter = adapter
        binding.rvMateri.layoutManager = LinearLayoutManager(requireContext())

        adapter.setOnItemClickCallback(object : MateriListSiswaAdapter.OnItemClickCallback {

            override fun onItemClicked(data: MateriData) {
                val action = ListMateriFragmentDirections.actionListMateriFragmentToFlipBookTestFragment(data)
                findNavController().navigate(action)

            }

        })

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            viewDataEmpty(materiList.isEmpty())
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
    }

    private fun buttonCLick() {
        with(binding) {
            btnTambahMateri.setOnClickListener {
                findNavController().navigate(R.id.action_listMateriFragment_to_addMateriFragment)
            }
        }
    }

    //view handler when data empty
    private fun viewDataEmpty(isEmpty: Boolean) {
        with(binding) {
            if (isEmpty) imgDataEmpty.visibility = View.VISIBLE  else imgDataEmpty.visibility = View.GONE
        }
    }

    private fun showDialog(data: MateriData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.title_dialog_delete))
            .setMessage("Materi \"${data.judul}\" akan dihapus dari database")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                viewModel.deleteMateri(data)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

