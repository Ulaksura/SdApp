package com.example.sdapp.start

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sdapp.DB.AppDatabase
import com.example.sdapp.DB.User
import com.example.sdapp.R
import com.example.sdapp.security.generateHash
import com.example.sdapp.security.generateRandomSalt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val PASSWORD_PATTERN =
    "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"

class RegFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        val userLogin: EditText = view.findViewById(R.id.user_login)
        val userEmail: EditText = view.findViewById(R.id.user_email)
        val userPassword: EditText = view.findViewById(R.id.user_pass)

        val button: Button = view.findViewById(R.id.button_reg)

        val tvPasswordError = view.findViewById<TextView>(R.id.tvPasswordError)
        val passwordErrorLayout = view.findViewById<LinearLayout>(R.id.passwordErrorLayout)
        val btnCloseError = view.findViewById<Button>(R.id.btnCloseError)

        val linkToAuth: TextView = view.findViewById(R.id.link_to_auth)
        linkToAuth.setOnClickListener {
            navController.navigate(R.id.navigation_auth)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login == "" || email == "" || password == "")
                Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else if (login.length <= 3) {
                Toast.makeText(requireContext(), "Слишком короткий логин", Toast.LENGTH_LONG)
                    .show()
                userLogin.text.clear()

            } else if (!emailIsValid(email)) {
                Toast.makeText(requireContext(), "Неверный формат почты", Toast.LENGTH_LONG)
                    .show()
                userEmail.text.clear()

            } else if (!passwordIsValid(password)) {
                val errorMessage = validPass(password)
                tvPasswordError.text = errorMessage
                passwordErrorLayout.visibility = View.VISIBLE

                btnCloseError.setOnClickListener {
                    passwordErrorLayout.visibility = View.GONE
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(requireContext())
                    val userDao = db.userDao()
                    val salt = generateRandomSalt()
                    val user = User(
                        login = login,
                        password = generateHash(password, salt),
                        salt = salt,
                        email = email,
                        apiKey = null
                    )
                    val userId = userDao.insertUser(user)
                }

                Toast.makeText(
                    requireContext(),
                    "Пользователь $login зарегестрирован",
                    Toast.LENGTH_LONG
                )
                    .show()
                passwordErrorLayout.visibility = View.GONE
                userLogin.text.clear()
                userEmail.text.clear()
                userPassword.text.clear()
            }
        }
    }

    fun emailIsValid(text: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    fun passwordIsValid(text: String): Boolean {
        return text.matches(Regex(PASSWORD_PATTERN))
    }

    fun validPass(password: String): String {
        val errorList = mutableListOf<String>()

        if (!password.matches(".*[A-Z].*".toRegex()))
            errorList.add("Содержать хотя бы одну заглавную букву")
        if (!password.matches(".*[a-z].*".toRegex()))
            errorList.add("Содержать хотя бы одну строчную букву [a-z]")
        if (!password.matches(".*[0-9].*".toRegex()))
            errorList.add("Содержать хотя бы одну цифру [0-9]")
        if (!password.matches(".*[#?!@\$%^&*-].*".toRegex()))
            errorList.add("Содержать хотя бы один специальный символ [#?!@\$%^&*-]")
        if (password.length < 8)
            errorList.add("Иметь длину не менее 8 символов")

        return if (errorList.isEmpty()) "" else "Пароль должен:\n" + errorList.joinToString("\n")
    }
}