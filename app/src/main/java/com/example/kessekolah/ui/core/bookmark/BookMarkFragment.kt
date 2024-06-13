package com.example.kessekolah.ui.core.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.FragmentBookMarkBinding
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.ui.adapter.BookMarkAdapter
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark

class BookMarkFragment : Fragment() {

    private var _binding: FragmentBookMarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookMarkAdapter
    private lateinit var materiBookMarkViewModel: BookMarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookMarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)
        materiBookMarkViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[BookMarkViewModel::class.java]

        adapter = BookMarkAdapter()
        binding.rvMateriBookmark.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMateriBookmark.setHasFixedSize(true)
        binding.rvMateriBookmark.adapter = adapter

        materiBookMarkViewModel.getAllUsers().observe(viewLifecycleOwner) { listUser ->
            if (listUser != null) {
                showLoading(false)
                adapter.setListUsers(listUser)
                binding.rvMateriBookmark.visibility = if (listUser.isNotEmpty()) View.VISIBLE else View.GONE
                binding.imgDataEmpty.visibility = if (listUser.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        materiBookMarkViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        adapter.setOnItemClickCallback(object : BookMarkAdapter.OnItemClickCallback {
            override fun onItemClicked(data: MateriData) {
                Log.d("BookMarkFragment", "data: $data")
                val action = BookMarkFragmentDirections.actionBookMarkFragmentToDetailMateriFragment(data)
                findNavController().navigate(action)
            }
        })

    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
