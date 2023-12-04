package pt.g2.Jorge

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import pt.g2.Jorge.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1062679982796-1d0qkdhslp4hsbes9v4qbarm82i2gj1m.apps.googleusercontent.com")
            .requestEmail()
            .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            binding.Google.setOnClickListener {
                signIn()
        }

    }

    /**
     * func used to clear the password textBoxes when the register is not correct
     */
    private fun clearTextBoxes (vararg editTexts: EditText){ // vararg used to pass a diff num of variables as argument https://www.baeldung.com/kotlin/varargs-spread-operator
        for (editText in editTexts) {// iterate the diff editTexts that where sent as arg
            editText.text.clear()
        }
    }

    /**
     * if a user is already register it does the login option
     * in case of matching email and password with the database then its done the login
     */
    fun dologin(view: View) {

        val email = findViewById<EditText>(R.id.emailAdress).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Insert Email and Password", Toast.LENGTH_LONG).show()
            clearTextBoxes(findViewById<EditText>(R.id.emailAdress),findViewById<EditText>(R.id.password))

        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login", Toast.LENGTH_LONG).show()

                    val user = auth.currentUser
                    updateUI(user)

                    //val chatsIntent = Intent( this@MainActivity, ChatList:: class.java)
                    //startActivity(chatsIntent)

                } else {
                    Toast.makeText(this, "Email or Password Wrong: Try again", Toast.LENGTH_LONG).show()
                    clearTextBoxes(findViewById<EditText>(R.id.emailAdress),findViewById(R.id.password))
                }
            }
        }
    }

    /**
     * func that initiates the new activity chat list.
     */

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this@MainActivity, ChatList::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }

    /**
     * In case on new user, its done the registration
     * opens register activity
     */
    fun doRegister(view: View) {
        val intent = Intent( this@MainActivity, registerActivity:: class.java)
        startActivityForResult(intent,1)
    }

    /**
     * func to show the intent with the google accounts
     *
     * SE DER MERDA DEPOIS DO LOGIN MUITO PROVAVELMENTE É POR CAUSA DESTA CONDICAO
     * APOS DE DAR LOGIN UMA VEZ COM O GOOGLE ELE ASSUMIA SEMPRE A MESMA CONTA
     * ENTAO METI AQUELA CENA PRA PEDIR SMP O LOGIN QUANDO HOUVER LOGOUT OU ABRIR A APP
     * PODE DAR MERDA NO SENTIDO EM QUE NÃO SEI SE O LOGOUT ESTA REALMENTE A FUNCIONAR
     * OU SE AO APRESENTAR ESTA OPCAO ELE DE LOGIN AO NOVO MEN
     *
     */
    private fun signIn() {

        googleSignInClient.signOut().addOnCompleteListener(this){ // force to sign out before login with google
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    /**
     * func of login with google button
     */
    fun loginGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Logged In With Google", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Could Not Login With Google", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }

    }

    /**
     * Func that is used when the user wants to login sign in with the phone number
     * It will open a second activity "Login Mobile Phone", in order to type the contact
     */

    fun loginMobilePhone(view: View) {

        val intentMobilePhone = Intent( this@MainActivity, LoginMobilePhone:: class.java)
        startActivityForResult(intentMobilePhone,2)

    }


    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1){  // register
            if(resultCode == Activity.RESULT_OK){


                val result = data?.getStringExtra("result")

                val emailNew = findViewById<EditText>(R.id.emailAdress)
                emailNew.setText(result) // take the register email and write on login screen
            }
            else if (resultCode == Activity.RESULT_CANCELED){
            }
        }
        if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){

            }
            else{}

        }
        if (requestCode == RC_SIGN_IN) { // chat list
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if(resultCode == Activity.RESULT_OK) {
                val account = task.getResult(ApiException::class.java)!!
                loginGoogle(account.idToken!!)

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }




}



