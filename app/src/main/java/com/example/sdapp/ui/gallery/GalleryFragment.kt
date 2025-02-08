package com.example.sdapp.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sdapp.DB.ImageEntity
import com.example.sdapp.R
import com.example.sdapp.SharedGalleryViewModel
import com.example.sdapp.authUser
import com.example.sdapp.databinding.FragmentGalleryBinding


class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
   // public var galleryImageList = arrayListOf<Image>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedGalleryViewModel by activityViewModels()
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val galleryList: RecyclerView = view.findViewById(R.id.image_list)
        adapter = ImageAdapter(emptyList<ImageEntity>().toMutableList(), requireContext())
//        adapter = ImageAdapter(sharedViewModel.galleryImageList.value.orEmpty().toMutableList(), requireContext())

        galleryList.adapter = adapter
       // galleryList.layoutManager = LinearLayoutManager(context)
        galleryList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        sharedViewModel.galleryImageList.observe(viewLifecycleOwner) { newImageList ->
            adapter.images = newImageList.toMutableList()
            adapter.notifyDataSetChanged()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        sharedViewModel.reLoadAllImagesFromDatabase(requireContext(), authUser.idAuthUser)
    }

}