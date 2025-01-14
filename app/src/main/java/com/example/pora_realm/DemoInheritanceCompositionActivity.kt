package com.example.pora_realm

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pora_realm.databinding.ActivityDemoInheritanceCompositionBinding
import com.example.pora_realm.databinding.ActivityMainBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class DemoInheritanceCompositionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoInheritanceCompositionBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = RealmConfiguration.create(schema = setOf(PersonRealm::class, StudentRealm::class))
        realm = Realm.open(config)

        enableEdgeToEdge()
        binding = ActivityDemoInheritanceCompositionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // odkomentiraj ce hoces vstavit
//        realm.writeBlocking {
//            val person1 = copyToRealm(PersonRealm().apply {
//                name = "Aljosa Golob"
//                city = "Maribor"
//            })
//
//            copyToRealm(StudentRealm().apply {
//                person = person1
//                studentId = "12345"
//                grade = 95
//            })
//        }

        binding.btnLogToConsole.setOnClickListener {
            val students = realm.query(StudentRealm::class).find()
            logStudentDetails(students)
        }
    }

    fun logStudentDetails(students: RealmResults<StudentRealm>) {
        if (students.isNotEmpty()) {
            for (student in students) {
                val person = student.person
                Log.d("RealmData", "Student ID: ${student.studentId}, Name: ${person?.name ?: "N/A"}, City: ${person?.city ?: "N/A"}, Grade: ${student.grade}, ClassId: ${student._id}")
            }
        } else {
            Log.d("RealmData", "No students found in the database.")
        }
    }
}
