package pt.g2.Jorge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth



class NumberVerification : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //TESTAR!!!!!!!!!!!!!!!!!!!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_verification)
        auth = FirebaseAuth.getInstance()

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intentVerification= Intent(this@NumberVerification, ChatList::class.java) // if credentials match open chats
                    startActivity(intentVerification)
                    finish()
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun Confirm(view: View) {
        val storedVerificationId = intent.getStringExtra("storedVerificationId")
        val otpGiven = findViewById<EditText>(R.id.PinNumber).text.trim().toString()

        if(!otpGiven.isEmpty()){
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId.toString(), otpGiven)
            signInWithPhoneAuthCredential(credential)
        }else{
            Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()
        }
    }
}