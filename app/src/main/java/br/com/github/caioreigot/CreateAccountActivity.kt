package br.com.github.caioreigot

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    // Elementos da interface
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnCreateAccount: Button
    private lateinit var progressBar: ProgressBar

    // Referencias ao banco de dados
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    // Variaveis globais
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialize()
    }

    private fun initialize() {
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        progressBar = findViewById(R.id.progress_bar)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {
        firstName = etFirstName.text.toString()
        lastName = etLastName.text.toString()
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
            && Utils.isValidEmail(email) && !TextUtils.isEmpty(password)) {

            Log.d("MY_DEBUG", "Informações preenchidas corretamente")

        } else {
            Utils.createCustomSnackbar(
                    window.decorView,
                    "INVALID CREDENTIALS",
                    R.drawable.snackbar_rounded_corner,
                    R.color.snackbarRedBackground
            ).show()

            return
        }

        progressBar.visibility = View.VISIBLE

        mAuth
                .createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        Log.d("MY_DEBUG", "CreateUserWithEmailAndPassword: Sucess")

                        var userID = mAuth.currentUser.uid

                        // Confirmar se o email é do user
                        verifyEmail()

                        // Atualizar as informações no banco de dados
                        val currentUserDb = mDatabaseReference.child(userID)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)
                        currentUserDb.child("emailAddress").setValue(email)

                        changeActivity()
                    } else {
                        password?.let { if (password!!.length < 6) {
                            Utils.createCustomSnackbar(
                                    window.decorView,
                                    "PASSWORD MUST BE MORE THAN 6 DIGITS",
                                    R.drawable.snackbar_rounded_corner,
                                    R.color.snackbarRedBackground
                            ).show()

                            return@addOnCompleteListener
                        } }

                        Utils.createCustomSnackbar(
                                window.decorView,
                                "AUTHENTICATION FAILED",
                                R.drawable.snackbar_rounded_corner,
                                R.color.snackbarRedBackground
                        ).show()
                    }
                }
    }

    private fun verifyEmail() {
        val mUser = mAuth.currentUser
        mUser.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful)
                Toast.makeText(this, "Verification email sent to " + mUser.email, Toast.LENGTH_SHORT).show()
            else {
                Log.e("MY_DEBUG", "Send verification email failed", task.exception)
                Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}