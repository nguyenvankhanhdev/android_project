package com.example.test.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.models.User
import com.example.test.models.UserRole
import com.example.test.utils.ClothesEditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.random.Random

@Suppress("DEPRECATION")
class RegisterActivity : BaseActivity() {
    private lateinit var tv_login: TextView
    private lateinit var btn_register: Button
    private lateinit var toolbar_register_activity: androidx.appcompat.widget.Toolbar
    private lateinit var et_first_name: ClothesEditText
    private lateinit var et_last_name: ClothesEditText
    private lateinit var et_email: ClothesEditText
    private lateinit var et_password: ClothesEditText
    private lateinit var et_confirm_password: ClothesEditText
    private lateinit var cb_terms_and_condition: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        addControl()
        setupActionBar()
        tv_login.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        btn_register.setOnClickListener{
            registerUser()
        }
    }
    fun addControl(){
        tv_login = findViewById(R.id.tv_login)
        btn_register = findViewById<Button>(R.id.btn_register)
        et_first_name = findViewById(R.id.et_first_name)
        et_last_name = findViewById(R.id.et_last_name)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_confirm_password = findViewById(R.id.et_confirm_password)
        cb_terms_and_condition = findViewById(R.id.cb_terms_and_condition)
    }
    private fun setupActionBar() {
        toolbar_register_activity = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar_register_activity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_register_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false  }
            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false  }
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false  }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false }
            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true )
                false
            }
            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun createIDKey(password: String): String {
        // Kiểm tra nếu password toàn số
        if (password.all { it.isDigit() }) {
            val firstThreeDigits = password.substring(0, 3)
            val remainingDigits = password.substring(3)
            val newPassword = remainingDigits + firstThreeDigits
            return newPassword.map { if (it.isLetter()) it.toString() + (1..3).map { ('a'..'z').random() }.joinToString("") else it.toString() + (1..3).map { Random.nextInt(10) }.joinToString("") }.joinToString("")
        }
        // Kiểm tra nếu password toàn chữ
        else if (password.all { it.isLetter() }) {
            val alphabet = ('a'..'z').toList()
            val firstThreeLetters = password.substring(0, minOf(password.length, 3))
            val remainingLetters = password.substring(3)
            val shuffledPassword = remainingLetters + firstThreeLetters
            val newPassword = shuffledPassword.map { (1..3).map { alphabet.random() }.joinToString("") + it.toString() }.joinToString("")
            return newPassword
        }
        // Trường hợp còn lại
        else {
            // Lấy 3 ký tự đầu tiên của mật khẩu (bao gồm cả số và chữ)
            val firstThree = password.substring(0, minOf(password.length, 3))

            // Tạo danh sách chứa các ký tự còn lại của mật khẩu
            val remainingChars = password.substring(3).toCharArray().toMutableList()


            // Thêm firstThree vào đầu của remainingChars
            remainingChars.addAll(remainingChars.size, firstThree.toCharArray().asList())

            // Biến để lưu trữ mật khẩu mới
            val newPassword = StringBuilder()

            for (char in remainingChars) {
                // Thêm 3 ký tự ngẫu nhiên trước mỗi ký tự trong chuỗi remainingChars
                newPassword.append((1..3).map { if (Random.nextBoolean()) Random.nextInt(10) else ('a'..'z').random() }
                    .joinToString(""))
                newPassword.append(char)
            }

            // Trả về mật khẩu mới
            return newPassword.toString()
        }
    }


    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }
            val idKey: String=createIDKey(password);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(
                            idkey = idKey,
                            id = firebaseUser.uid,
                            firstName = et_first_name.text.toString().trim { it <= ' ' },
                            lastName = et_last_name.text.toString().trim { it <= ' ' },
                            email = et_email.text.toString().trim { it <= ' ' },
                            role = UserRole.USER.name

                        )
                        FirestoreClass().registerUser(this@RegisterActivity, user)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_succes),
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}