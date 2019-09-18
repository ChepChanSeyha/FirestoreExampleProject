package com.example.firestoreexampleproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private lateinit var viewData: TextView

    // My first note is collection
    // NoteBook is Document
    private val db = FirebaseFirestore.getInstance().document("My first note/NoteBook")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        editTitle = findViewById(R.id.editTitle)
        editDescription = findViewById(R.id.editDescription)
        viewData = findViewById(R.id.viewData)

        btnSave.setOnClickListener {
            val title = editTitle.text.toString()
            val description = editDescription.text.toString()

            val notes = HashMap<String, Any>()
            notes["Title"] = title
            notes["Description"] = description

            db.set(notes)
        }

        btnLoad.setOnClickListener {
            db.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val title = it.getString("Title")
                        val description = it.getString("Description")

                        viewData.text = "Title: $title\nDescription: $description"
                    } else {
                        Toast.makeText(this, "Document doesn't exist", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
        }

        btnUpdate.setOnClickListener {
            val description = editDescription.text.toString()

            val notes = HashMap<String, Any>()
            notes["Description"] = description

            db.update("Description", description)
        }

        btnDelete.setOnClickListener {
            db.update("Description", FieldValue.delete())
        }

        btnDeleteAll.setOnClickListener {
            db.delete()
        }

    }

    override fun onStart() {
        super.onStart()
        db.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Toast.makeText(this, "Error while loading", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            if (documentSnapshot!!.exists()) {
                val title = documentSnapshot.getString("Title")
                val description = documentSnapshot.getString("Description")

                viewData.text = "Title: $title\nDescription: $description"
            } else {
                viewData.text = ""
            }
        }
    }

}
