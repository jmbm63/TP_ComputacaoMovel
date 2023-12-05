package pt.g2.Jorge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

class LoginMobilePhone : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private var number:String = "" //this variable will store the number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mobile_phone)
        auth = FirebaseAuth.getInstance()


         findViewById<Button>(R.id.number).setOnClickListener {
             login()
         }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                startActivity(Intent(applicationContext, ChatList::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG","onCodeSent:$verificationId")// Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token


                val intentVerification = Intent( this@LoginMobilePhone, NumberVerification:: class.java)
                intentVerification.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intentVerification)
                Toast.makeText(applicationContext, "Aqui", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    /**
     * fun used to send the verification code
     */
    private fun verificationCode(number: String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    /**
     * Retrieve cell phone number from edit box
     */
    private fun login(){
        number = findViewById<EditText>(R.id.editTextNumber).text.trim().toString()

        if (number.isNotEmpty()){
            number = "+351$number" // 351 nº de portugal
            verificationCode(number)
        }else{
            Toast.makeText(applicationContext, "Please Enter Phone Number", Toast.LENGTH_LONG).show()
        }
    }





}