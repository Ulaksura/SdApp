package com.example.sdapp.ui.img2img

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sdapp.databinding.FragmentImg2imgBinding

class Img2ImgFragment : Fragment() {

    private var _binding: FragmentImg2imgBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val img2ImgViewModel =
            ViewModelProvider(this).get(Img2ImgViewModel::class.java)

        _binding = FragmentImg2imgBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textImg2img
        img2ImgViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}