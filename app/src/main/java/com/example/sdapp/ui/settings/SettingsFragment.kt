package com.example.sdapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sdapp.DB.AppDatabase
import com.example.sdapp.R
import com.example.sdapp.SharedGalleryViewModel
import com.example.sdapp.authUser
import com.example.sdapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textUserName:TextView = view.findViewById(R.id.textUserName)
        var name:String=""
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val userDao = db.userDao()

            name = userDao.getUserById(authUser.idAuthUser)!!.login
            withContext(Dispatchers.Main) {
                textUserName.text = name
            }
        }

        val apiKeyUser: EditText = view.findViewById(R.id.editTextApiKey)
        val saveButton: Button = view.findViewById(R.id.saveButton)
        //apiKeyUser.text = authUser.getUserAPI()
        //короче с этой апишкой, надо будет с основного проекта кусок спизидть
        apiKeyUser.setText(authUser.getUserAPI())

        saveButton.setOnClickListener{
            authUser.setUserAPI(apiKeyUser.text.toString().trim())
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(requireContext())
                val userDao = db.userDao()
                userDao.updateUser(authUser.user!!)
            }
            Toast.makeText(requireContext(), "Изменения примененры!", Toast.LENGTH_LONG).show()
        }

        val exitButton: Button = view.findViewById(R.id.logoutButton)
        exitButton.setOnClickListener{
            Toast.makeText(
                requireContext(),
                "Пользователь ${authUser.user?.login} вышел",
                Toast.LENGTH_LONG)
                .show()
            SharedGalleryViewModel().exitUserGallery(requireContext(), authUser.idAuthUser)
            authUser.authUserExit()
            findNavController().navigate(R.id.navigation_auth)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}