package com.example.kessekolah.ui.core.beranda.materi.detailMateri

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.databinding.FragmentDetailMateriBinding
import com.example.kessekolah.ui.adapter.BannerDetailMateriAdapter
import com.example.kessekolah.ui.adapter.ButtonCoreFeaturesAdapter
import com.example.kessekolah.ui.adapter.MateriListAdapterCore
import com.rajat.pdfviewer.PdfRendererView

class DetailMateriFragment : Fragment() {

    private var _binding: FragmentDetailMateriBinding? = null
    private val binding get() = _binding!!
    private lateinit var materiListAdapterCore: BannerDetailMateriAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMateriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val materiData = arguments?.getParcelable<MateriData>("data")

        if (materiData != null) {
            Log.d("Detail Materi", materiData.toString())
            setupBanner(materiData)
            displayPdf(materiData.fileUrl)
        } else {
            Toast.makeText(requireContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBanner(materiData: MateriData) {
        materiListAdapterCore = BannerDetailMateriAdapter()
        binding.rvBanner.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBanner.adapter = materiListAdapterCore

        materiListAdapterCore.submitList(listOf(materiData))
    }

    private fun displayPdf(fileUrl: String) {
        binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadStart() {
                Log.i("statusCallBack","onPdfLoadStart")
            }
            override fun onPdfLoadProgress(
                progress: Int,
                downloadedBytes: Long,
                totalBytes: Long?
            ) {
                //Download is in progress
            }

            override fun onPdfLoadSuccess(absolutePath: String) {
                Log.i("statusCallBack","onPdfLoadSuccess")
            }

            override fun onError(error: Throwable) {
                Log.i("statusCallBack","onError")
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                //Page change. Not require
            }
        }
        binding.pdfView.initWithUrl(
            url = fileUrl,
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
        binding.pdfView.jumpToPage(3)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}