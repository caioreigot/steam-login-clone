package br.com.github.caioreigot

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


open class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var loggedUserEmail: String
        lateinit var userUid: String
    }

    lateinit var tvUsername: TextView

    // Referencias ao banco de dados
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }

    private fun initialize() {
        tvUsername = findViewById(R.id.tv_username)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase.reference.child("Users")

        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ss in snapshot.children) {
                    if (ss.child("emailAddress").getValue(String::class.java) == loggedUserEmail) {
                        var loggedUserFirstName = ss.child("firstName").getValue(String::class.java)
                        var loggedUserLastName = ss.child("lastName").getValue(String::class.java)
                        ss?.key.let { userUid = ss.key!! }

                        tvUsername.textSize = 30F
                        tvUsername.text = "$loggedUserFirstName $loggedUserLastName"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Utils.globalDispatchTouchEvent(this, R.id.parent_layout, event)
        return super.dispatchTouchEvent(event)
    }

}