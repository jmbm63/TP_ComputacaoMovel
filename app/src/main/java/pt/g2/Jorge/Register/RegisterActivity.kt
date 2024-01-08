package pt.g2.Jorge.Register

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.Adapters.UserAdapter
import pt.g2.Jorge.Login.MainActivity
import pt.g2.Jorge.R

class registerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        firestore = FirebaseFirestore.getInstance()
    }

    /**
     * func to verify the password requirements
     * 1 -> must be 8 char long
     * 2 -> at least Uppercase letter
     * 3 -> at least one number
     */

    private fun verifyPassword(password: String ):Boolean{

        val upperLetter = Regex("[A-Z]")
        val numberSearch = Regex("[0-9]")

        if(password.length >= 8 && upperLetter.containsMatchIn(password) && numberSearch.containsMatchIn(password)) {
            return true
        }
        return false
    }

    /**
     * func used to confirm if the password is correct
     * user needs to type the 2x the password in order to proceed with the register
     */

    private fun confirmPassword (password: String, confPassword: String): Boolean{

        if(password == confPassword){ // confirm if the passwords match
            return true
        }
        return false
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
     * func used to update thecloud firestore
     */
    private fun writeNewUser(userId: String, name: String, email: String) {
        val user = UserAdapter(userId, name, email, 2)
        val usersCollection = firestore.collection("users")


        usersCollection.document(userId)
            .set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User registration successful", Toast.LENGTH_LONG).show()
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(this, "User registration failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * In case on new user, its done the registration
     * if the password meets with the requirements then its done the registration on the database
     * otherwise the user needs to try again
     */
    fun doRegister(view: View) {
        val email = findViewById<EditText>(R.id.emailAdress).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val confPassword = findViewById<EditText>(R.id.confirmPassword).text.toString()

        if (verifyPassword(password) && confirmPassword (password, confPassword)) {

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: ""
                    val userName = findViewById<EditText>(R.id.userName).text.toString()

                    writeNewUser(userId, userName, email)

                    Toast.makeText(this, "Successful ", Toast.LENGTH_LONG).show()

                    val returnIntent = Intent(this@registerActivity, MainActivity::class.java)
                    returnIntent.putExtra("result", email) // returns to the first act the email
                    setResult(Activity.RESULT_OK,returnIntent)
                    finish() // go to first act

                }else{

                    Toast.makeText(this, "Couldn't proceed with the regist: Try again", Toast.LENGTH_LONG).show()
                    clearTextBoxes(findViewById<EditText>(R.id.emailAdress),findViewById(R.id.password), findViewById(R.id.confirmPassword))
                }
            }
        } else if (confirmPassword (password, confPassword) == false){

            Toast.makeText(this, "The passwords don't match: try again", Toast.LENGTH_LONG).show()
            clearTextBoxes(findViewById(R.id.password), findViewById(R.id.confirmPassword))

        } else if(verifyPassword(password) == false){

            Toast.makeText(this, "The password don't follow the requirements:\n 8 characters long\n 1 Upper case letter\n At least one number", Toast.LENGTH_LONG).show()
            clearTextBoxes(findViewById(R.id.password), findViewById(R.id.confirmPassword))

        }else{

            Toast.makeText(this, "Couldn't proceed with the regist: Try again", Toast.LENGTH_LONG).show()
            clearTextBoxes(findViewById<EditText>(R.id.emailAdress),findViewById(R.id.password), findViewById(R.id.confirmPassword))
        }
    }
}