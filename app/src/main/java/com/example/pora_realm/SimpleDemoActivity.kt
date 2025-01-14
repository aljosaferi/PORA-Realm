package com.example.pora_realm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pora_realm.databinding.ActivityMainBinding
import com.example.pora_realm.databinding.ActivitySimpleDemoBinding
import com.google.android.material.snackbar.Snackbar
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class SimpleDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimpleDemoBinding
    private lateinit var realm: Realm
    lateinit var adapter: PersonAdapter
    private lateinit var people: RealmResults<PersonRealm>

    private val names = listOf("John", "Alice", "Bob", "Sarah", "Tom")
    private val cities = listOf("Maribor", "Ljubljana", "Koper", "Fužine", "Celje")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Realm
        val config = RealmConfiguration.create(schema = setOf(PersonRealm::class))
        realm = Realm.open(config)

        // Query all PersonRealm objects
        people = realm.query(PersonRealm::class).find()

        logDatabaseContent()

        // Set up RecyclerView
        adapter = PersonAdapter(
            people,
            object: PersonAdapter.MyOnLongClick {
                override fun onLongClick(p0: View?, position: Int) {
                    val dialogBuilder = AlertDialog.Builder(this@SimpleDemoActivity)
                    dialogBuilder.setTitle("Delete")
                    dialogBuilder.setMessage("Are you sure you want to delete this person?")
                    dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                        removePersonAtIndex(position)
                        adapter.notifyItemRemoved(position)
                        dialog.dismiss()

                        Snackbar.make(binding.root, "Name deleted successfully", Snackbar.LENGTH_LONG).show()
                    }
                    dialogBuilder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogBuilder.create().show()
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnAddSimple.setOnClickListener {
            addPerson()
        }

        binding.btnBack1.setOnClickListener {
            finish()
        }

        binding.btnEditSimple.setOnClickListener {
            updatePerson()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun addPerson() {
        val nameRand = names.random()
        val cityRand = cities.random()

        realm.writeBlocking {
            copyToRealm(PersonRealm().apply {
                _id = UUID.randomUUID().toString()
                name = nameRand
                city = cityRand
            })
        }
        logDatabaseContent()
    }

    fun removePersonAtIndex(index: Int) {
        realm.writeBlocking {
            val frozenPerson = people[index]
            val livePerson = findLatest(frozenPerson)
            livePerson?.let { delete(it) }
        }
        logDatabaseContent()
    }

    fun updatePerson() {
        realm.writeBlocking {
            val activity = query<PersonRealm>(PersonRealm::class, "_id == $0", "b1897bc9-3acb-4b0b-9104-a603e0f73ded").first().find()
            activity?.apply {
                name = "Aljoša"
                city = "Maribor"
            }
        }
    }

    fun logDatabaseContent() {
        realm.writeBlocking {
            val people = query(PersonRealm::class).find()

            if (people.isNotEmpty()) {
                // Log each person's details
                for (person in people) {
                    Log.d("RealmData", "Person: ID: ${person._id}, Name: ${person.name}, City: ${person.city}")
                }
            } else {
                Log.d("RealmData", "No persons found in the database.")
            }
        }
    }
}



// Realm class
open class PersonRealm : RealmObject  {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var city: String = ""
}

open class StudentRealm : RealmObject {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()

    var person: PersonRealm? = null

    var studentId: String = ""
    var grade: Int = 0
}