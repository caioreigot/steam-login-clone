package br.com.github.caioreigot

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var lastHint: CharSequence? = null

    // Elementos da UI
    private var viewGroup: LinearLayout? = null
    private lateinit var tvForgotPassword: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnCreateAccount: Button
    private lateinit var progressBar: ProgressBar

    // Referencias ao banco de dados
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewGroup = findViewById(R.id.parent_layout);

        viewGroup?.let {
            // Loop dentro do viewGroup
            for (i in 0..viewGroup?.childCount!!) {
                // Pegando o child
                var childView = viewGroup?.getChildAt(i)

                if (childView is EditText) {
                    childView.setOnFocusChangeListener() { view, event ->
                        // Pegando o EditText atual
                        var currentEditText = findViewById<EditText>(view.id)

                        // Se tiver em foco, armazene a hint atual
                        if (currentEditText.hasFocus())
                            lastHint = currentEditText.hint

                        currentEditText.hint = if (currentEditText.hasFocus()) "" else lastHint
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColorTo(R.color.colorPrimary)

        initialize()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Window.setStatusBarColorTo(color: Int) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = ContextCompat.getColor(baseContext, color)
    }

    private fun initialize() {
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnCreateAccount = findViewById(R.id.btn_register_account)

        progressBar = findViewById(R.id.progress_bar)

        mAuth = FirebaseAuth.getInstance()

        tvForgotPassword.setOnClickListener {
            var intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnCreateAccount.setOnClickListener {
            var intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        var email = etEmail.text.toString()
        var password = etPassword.text.toString()

        if (Utils.isValidEmail(email) && !TextUtils.isEmpty(password)) {
            progressBar.visibility = View.VISIBLE

            Log.d("MY_DEBUG", "Usuario tentou login com informacoes validas")

            // Autenticando o usuario
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    Log.d("MY_DEBUG", "User logado com sucesso")
                    MainActivity.loggedUserEmail = email
                    changeActivity()
                    return@addOnCompleteListener
                }

                // !task.isSucessful
                Log.e("MY_DEBUG", "Erro ao logar", task.exception)

                Utils.createCustomSnackbar(
                        window.decorView,
                        "LOGIN FAILED",
                        R.drawable.snackbar_rounded_corner,
                        R.color.snackbarRedBackground
                ).show()
            }

            return
        }

        Utils.createCustomSnackbar(
                window.decorView,
                "INVALID CREDENTIALS",
                R.drawable.snackbar_rounded_corner,
                R.color.snackbarRedBackground
        ).show()
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Utils.globalDispatchTouchEvent(this, R.id.parent_layout, event)
        return super.dispatchTouchEvent(event)
    }

}
