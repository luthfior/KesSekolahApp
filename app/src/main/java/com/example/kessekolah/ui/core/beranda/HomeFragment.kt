package com.example.kessekolah.ui.core.beranda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentHome2Binding
import com.example.kessekolah.model.ButtonCoreFeatures
import com.example.kessekolah.ui.adapter.ButtonCoreFeaturesAdapter
import com.example.kessekolah.ui.adapter.EbookListAdapterCore
import com.example.kessekolah.ui.adapter.MateriListAdapterCore
import com.example.kessekolah.ui.adapter.VideoListAdapterCore
import com.example.kessekolah.utils.LoginPreference

class HomeFragment : Fragment() {

    private var _binding: FragmentHome2Binding? = null

    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var dataLogin: LoginData
    private lateinit var dataFirst: MateriData

    private val listButton = listOf<ButtonCoreFeatures>(
        ButtonCoreFeatures("@drawable/ic_book", "Materi"),
        ButtonCoreFeatures("@drawable/ic_video", "Video"),
        ButtonCoreFeatures("@drawable/ic_question", "Tanya Jawab"),
        ButtonCoreFeatures("@drawable/ic_form", "Forum"),
        ButtonCoreFeatures("@drawable/ic_professional", "Tanya Ahli"),
        ButtonCoreFeatures("@drawable/ic_e_book", "E-Book"),
    )

    private val videoList = listOf(
        R.drawable.image_card_video_1,
        R.drawable.image_card_video_2
    )

    private val eBookList = listOf(
        R.drawable.image_card_ebook_1,
        R.drawable.image_card_ebook_2
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataLogin = LoginPreference(requireContext()).getData()


        setupData(listButton)
        setupVideoBanner(videoList)
        setupEbookBanner(eBookList)
        loadingHandler()
    }


    private fun setupData(listData: List<ButtonCoreFeatures>) {

        val listAdapter = ButtonCoreFeaturesAdapter(listData)
        val listBannerMateriAdapter = MateriListAdapterCore()

        viewModel.materiList.observe(viewLifecycleOwner) { materiList ->
            if (materiList.isNotEmpty()) {
                dataFirst = materiList[0]
                listBannerMateriAdapter.submitList(materiList)
            } else {
                binding.imgDataEmpty.visibility = View.VISIBLE
                binding.rvBannerMateri.visibility = View.GONE
                if (dataLogin.role == "Guru") {
                    binding.imgDataEmpty.setOnClickListener {
                        findNavController().navigate(R.id.action_homeFragment2_to_addMateriFragment)
                    }
                }
                Log.i("HomeFragment", "Materi list is empty")
            }
        }

        listBannerMateriAdapter.setOnItemClickCallback(object : MateriListAdapterCore.OnItemClickCallback {
            override fun onItemClicked(data: MateriData) {
                val action = HomeFragmentDirections.actionHomeFragment2ToFlipBookTestFragment(data)
                findNavController().navigate(action)
            }

        })

        with(binding) {
            tvUsername.text = dataLogin.name

            rvButtonCore.layoutManager = GridLayoutManager(requireContext(), 3)
            rvButtonCore.adapter = listAdapter

            rvBannerMateri.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvBannerMateri.adapter = listBannerMateriAdapter

            listAdapter.setOnItemClickCallback(object : ButtonCoreFeaturesAdapter.OnItemClickCallback{
                override fun onItemClicked(data: String) {
                    Log.i("BUTTON CLICK out when", data)
                    when (data) {
                        "Materi" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_listMateriFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                        "Video" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                        "Tanya Jawab" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                        "Forum" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                        "Tanya Ahli" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                        "E-Book" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_noServiceFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                    }
                }

            })
        }

    }

    private fun setupVideoBanner(videoList: List<Int>) {
        val listBannerVideoAdapter = VideoListAdapterCore(this)

        binding.rvBannerVideo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBannerVideo.adapter = listBannerVideoAdapter

        listBannerVideoAdapter.submitList(videoList)
    }

    private fun setupEbookBanner(videoList: List<Int>) {
        val listBannerEbookAdapter = EbookListAdapterCore(this)

        binding.rvBannerEbook.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBannerEbook.adapter = listBannerEbookAdapter

        listBannerEbookAdapter.submitList(videoList)
    }

    private fun loadingHandler() {
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            with(binding) {
                if (loading) {
                    shimmerMateri.visibility = View.VISIBLE
                    layoutBannerMateri.visibility = View.GONE
                } else {
                    shimmerMateri.visibility = View.GONE
                    layoutBannerMateri.visibility = View.VISIBLE
                }

            }
        }
    }

}
