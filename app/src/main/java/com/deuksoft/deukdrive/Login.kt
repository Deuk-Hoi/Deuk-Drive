package com.deuksoft.deukdrive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*


class Login : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var googleSigninClient : GoogleSignInClient
    val RC_SING_IN = 1000
    lateinit var googleSignIn : SignInButton
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    /*db = FirebaseFirestore.getInstance()
        var user : HashMap<String, Any> = HashMap<String, Any>()
        user.put("first", "DeukHoi")
        user.put("last", "Kim")
        user.put("born", 1213)

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("hello", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("world", "Error adding document", e)
            }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignIn = findViewById(R.id.googleSignIn)
        mAuth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(this, gso)

        googleSignIn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                signIn()
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SING_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Success", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Failed", "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    fun signIn(){
        var signInIntent : Intent = googleSigninClient.signInIntent
        startActivityForResult(signInIntent, RC_SING_IN)
    }

    private fun updateUI(user: FirebaseUser?) { //update ui code here
        if (user != null) {
            //앱을 실행시킬 때 마다 마지막 로그인 시간 수정
            var updateLastSignin = Date()
            var updateDate = hashMapOf("lastSignIn" to dateFormat.format(updateLastSignin))
            db.collection("userinfo").document(user.displayName.toString()).update(updateDate as Map<String, Any>)

            //이후 앱 실행
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("GoogleAccount", user)
            startActivity(intent)
            finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Success", "signInWithCredential:success")
                    //회원가입부분으로 여기에서 사용자 파일 크기를 지정해 준다.
                    val user = mAuth.currentUser
                    var SignzUpdate = Date(user!!.metadata!!.creationTimestamp)
                    var lastSignIndate = Date(user!!.metadata!!.lastSignInTimestamp)
                    dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var dbuser = hashMapOf(
                        "username" to user!!.displayName,
                        "useremail" to user!!.email,
                        "signUp" to dateFormat.format(SignzUpdate),
                        "lastSignIn" to dateFormat.format(lastSignIndate),
                        "fullsize" to 42949672960L,
                        "freesize" to 42949672960L
                    )
                    db.collection("UserInfo").document(user.email.toString())
                        .set(dbuser, SetOptions.merge()) // 문서가 있는지 확실하지 않은 경우 전체 문서를 실수로 덮어쓰지 않도록 새 데이터를 기존 문서와 병합하는 옵션
                        .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("Failed", "Error writing document", e) }

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Failed", "signInWithCredential:failure", task.exception)
                    // ...
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

}