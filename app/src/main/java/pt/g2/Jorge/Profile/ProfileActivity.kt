package pt.g2.Jorge.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import pt.g2.Jorge.Adapters.UserAdapter
import pt.g2.Jorge.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var db: FirebaseDatabase

    private lateinit var nomeEditText: EditText
    private lateinit var apelidoEditText: EditText
    private lateinit var idadeEditText: EditText
    private lateinit var photoImageView: ImageView
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        firestore = FirebaseFirestore.getInstance()

        nomeEditText = findViewById<EditText>(R.id.Nome)
        apelidoEditText = findViewById(R.id.Apelido)
        idadeEditText = findViewById(R.id.Idade)
        photoImageView = findViewById(R.id.imageView)
        updateButton = findViewById(R.id.Update)

        // Definir um clique no botão de atualização
        updateButton.setOnClickListener {
            // Obter os dados dos campos
            val nome = nomeEditText.text.toString()
            val apelido = apelidoEditText.text.toString()
            val idade = idadeEditText.text.toString().toIntOrNull()
                ?: 0 // Se não for possível converter para Int, define 0 como padrão

            // Executar a lógica para atualizar o perfil do usuário com os dados fornecidos
            atualizarPerfil(nome, apelido, idade)
        }

        // Configurar o botão de voltar
        val backbutton = findViewById<ImageView>(R.id.backspaceprofile)
        backbutton.setOnClickListener {
            onBackPressed()
        }
    }

    // Sobrescrever o método onBackPressed para garantir que o botão de voltar funcione corretamente
    override fun onBackPressed() {
        super.onBackPressed()
        // Adicione qualquer lógica adicional que você possa precisar antes de voltar para a activity anterior
    }

    private fun atualizarPerfil(nome: String, apelido: String, idade: Int) {

        val usersCollection = firestore.collection("users")

        val currentUserID = auth.currentUser?.uid

        if (currentUserID != null) {

            val userUpdates = hashMapOf<String, Any>(
                "nome" to nome,
                "apelido" to apelido,
                "idade" to idade
            )

            usersCollection.document(currentUserID)
                .update(userUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_LONG)
                            .show()
                        nomeEditText.text.clear()
                        apelidoEditText.text.clear()
                        idadeEditText.text.clear()
                    } else {
                        // Lidar com falha na atualização
                        Toast.makeText(this, "Falha ao atualizar o perfil.", Toast.LENGTH_LONG)
                            .show()
                        nomeEditText.text.clear()
                        apelidoEditText.text.clear()
                        idadeEditText.text.clear()
                    }
                }
        }
    }
}
