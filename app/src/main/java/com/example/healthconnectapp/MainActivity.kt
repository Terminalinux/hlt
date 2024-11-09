package com.example.healthconnectapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.PermissionRequest
import androidx.health.connect.client.metadata.Metadata
import java.time.ZonedDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var permissionLauncher: ActivityResultLauncher<Set<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация HealthConnectClient
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        // Запрос разрешений на использование Health Connect
        val permissions = setOf(StepsRecord.PERMISSION_WRITE)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsGranted ->
            if (permissionsGranted.all { it.value }) {
                insertStepsRecord()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncher.launch(permissions)
    }

    private fun insertStepsRecord() {
        val data = listOf(
            StepsRecord(
                count = 1000,
                startTime = ZonedDateTime.now().minusMinutes(30),
                endTime = ZonedDateTime.now(),
                metadata = Metadata()
            )
        )

        healthConnectClient.insertRecords(data).addOnSuccessListener {
            Toast.makeText(this, "Steps inserted successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to insert steps: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
