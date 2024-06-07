package com.example.kessekolah.ui.core.beranda

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kessekolah.R
import com.example.kessekolah.databinding.FragmentHome2Binding
import com.example.kessekolah.databinding.FragmentHomeBinding
import com.example.kessekolah.databinding.FragmentNotificationsBinding
import com.example.kessekolah.model.ButtonCoreFeatures
import com.example.kessekolah.ui.adapter.ButtonCoreFeaturesAdapter
import kotlinx.parcelize.Parcelize

class HomeFragment : Fragment() {

    private var _binding: FragmentHome2Binding? = null

    private val binding get() = _binding!!

    private val listButton = listOf<ButtonCoreFeatures>(
        ButtonCoreFeatures("@drawable/ic_book", "Materi"),
        ButtonCoreFeatures("@drawable/ic_video", "Video"),
        ButtonCoreFeatures("@drawable/ic_question", "Tanya Jawab"),
        ButtonCoreFeatures("@drawable/ic_form", "Forum"),
        ButtonCoreFeatures("@drawable/ic_professional", "Tanya Ahli"),
        ButtonCoreFeatures("@drawable/ic_e_book", "E-Book"),
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


        setupData(listButton)
    }

    private fun setupData(listData: List<ButtonCoreFeatures>) {

        val listAdapter = ButtonCoreFeaturesAdapter(listData)
//        val listPreviewAdapter = ButtonCoreFeaturesAdapter(listData)

        with(binding) {
            rvButtonCore.layoutManager = GridLayoutManager(requireContext(), 3)
            rvButtonCore.adapter = listAdapter

            listAdapter.setOnItemClickCallback(object : ButtonCoreFeaturesAdapter.OnItemClickCallback{
                override fun onItemClicked(data: String) {
                    Log.i("BUTTON CLICK out when", data)
                    Toast.makeText(requireContext(), data,
                        Toast.LENGTH_LONG).show()
                    when (data) {
                        "Materi" -> {
                            findNavController().navigate(R.id.action_homeFragment2_to_listMateriFragment)
                            Log.i("BUTTON CLICK", data)
                        }
                    }
                }

            })
        }


    }

}
