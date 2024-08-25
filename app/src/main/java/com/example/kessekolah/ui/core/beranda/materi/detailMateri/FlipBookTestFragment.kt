package com.example.kessekolah.ui.core.beranda.materi.detailMateri

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.kessekolah.R
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.databinding.FragmentFlipBookTestBinding
import com.example.kessekolah.model.BookMarkViewModel
import com.example.kessekolah.model.ListMateriViewModel
import com.example.kessekolah.ui.adapter.ScreenSlideRecyclerAdapter
import com.example.kessekolah.utils.LoginPreference
import com.example.kessekolah.viewModel.ViewModelFactoryBookMark
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FlipBookTestFragment : Fragment() {

    private var _binding: FragmentFlipBookTestBinding? = null

    private val binding get() = _binding!!

    private val args : FlipBookTestFragmentArgs by navArgs()
    private lateinit var data: MateriData

    private lateinit var viewModel: ListMateriViewModel
    private lateinit var bookMarkViewModel: BookMarkViewModel

    private var materiBookMark: MateriData? = null
    private var isBookMarked = false
    private lateinit var dataLogin: LoginData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlipBookTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data = args.materi
        dataLogin = LoginPreference(requireContext()).getData()

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val vmFactory = ViewModelFactoryBookMark.getInstance(requireActivity().application)

        viewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[ListMateriViewModel::class.java]

        bookMarkViewModel = ViewModelProvider(
            requireActivity(),
            vmFactory
        )[BookMarkViewModel::class.java]

        if (data != null) {
            materiBookMark = data
        } else {
            Toast.makeText(requireContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show()
        }

//        viewModel.getFavoriteData().observe(viewLifecycleOwner) { materiBM ->
//            if (materiBM != null) {
//                isFavorite = materiBM.any { it.judul == data?.judul }
//                val bookmarkMenuItem = binding.topAppBar.menu.findItem(R.id.bookmark_bar)
//                bookmarkMenuItem.setIcon(if (isFavorite) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24)
//            }
//        }

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.bookmark_bar -> {
                    if (isBookMarked) {
                        removeBookmark()
                    } else {
                        addBookmark()
                    }
                    true
                }
                else -> false
            }
        }

        checkIfBookmarked()

//        bookFLip()
        setData()
        pdfToBitmap(data)
    }

    private fun setData() = with(binding.bannerMateri) {
        ilusBanner.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                requireContext().getResources()
                    .getIdentifier("ilus_banner_${data.dataIlus}", "drawable", requireContext().getPackageName())
            )
        )
        backColorBanner.setBackgroundColor(
            Color.parseColor(data.backColorBanner)
        )
        textNews.text = data.judul
    }

    private fun pdfToBitmap(data: MateriData) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.loadingProgressBar.visibility = View.VISIBLE
                // Unduh file PDF di latar belakang
                val pdfFile = withContext(Dispatchers.IO) {
                    downloadPdfFile(data.fileUrl)
                }

                // Buat Uri untuk file PDF yang diunduh
                val uri = Uri.fromFile(pdfFile)

                // Buka PDF menggunakan PdfRenderer
                val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(parcelFileDescriptor)

                // Daftar untuk menyimpan semua bitmap
                val bitmapList = mutableListOf<Bitmap>()

                // Proses setiap halaman PDF
                for (pageIndex in 0 until pdfRenderer.pageCount) {
                    val page = pdfRenderer.openPage(pageIndex)

                    val width = page.width
                    val height = page.height

                    // Buat bitmap untuk halaman saat ini
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmapList.add(bitmap)

                    page.close()
                }

                pdfRenderer.close()

                binding.loadingProgressBar.visibility = View.GONE

                // Gunakan bitmapList sesuai kebutuhan
                withContext(Dispatchers.Main) {
                    bookFlip(bitmapList)
                }

            } catch (ex: Exception) {
                Log.e("PDF TO BITMAP", ex.toString())
            }
        }
    }

    // Fungsi untuk mengunduh file PDF
    private suspend fun downloadPdfFile(pdfUrl: String): File = withContext(Dispatchers.IO) {
        val url = URL(pdfUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val inputStream = connection.inputStream
        val file = File(requireContext().cacheDir, "downloaded_file.pdf")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        outputStream.close()
        inputStream.close()

        file
    }

    private fun bookFlip(bitmap: List<Bitmap>) = with(binding) {

        var sliderAdapter = ScreenSlideRecyclerAdapter(ArrayList<Bitmap>())
        sliderAdapter.setItems(bitmap)

        flipViewPager.adapter = sliderAdapter

        var bookTransformer = BookFlipPageTransformer2()
        flipViewPager.setPageTransformer(bookTransformer)
        flipViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    private fun addBookmark() {
        viewModel.addBookmarkToFirebase(dataLogin.token!!, data)
        isBookMarked = true
        updateBookmarkIcon()
        Toast.makeText(requireContext(), "Bookmark added", Toast.LENGTH_SHORT).show()
    }

    private fun removeBookmark() {
        viewModel.removeBookmarkFromFirebase(dataLogin.token!!, data.judul)
        isBookMarked = false
        updateBookmarkIcon()
        Toast.makeText(requireContext(), "Bookmark removed", Toast.LENGTH_SHORT).show()
    }

    private fun checkIfBookmarked() {
        bookMarkViewModel.getAllBookmarks(dataLogin.token!!)
        bookMarkViewModel.bookmarks.observe(viewLifecycleOwner) { bookmarks ->
            isBookMarked = bookmarks.any { it.judul == data.judul }
            updateBookmarkIcon()
        }
    }

    private fun updateBookmarkIcon() {
        val bookmarkIcon = if (isBookMarked) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24
        binding.topAppBar.menu.findItem(R.id.bookmark_bar)?.icon = ContextCompat.getDrawable(requireContext(), bookmarkIcon)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.nav_bar_bookmark, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}