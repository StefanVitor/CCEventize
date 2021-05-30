package com.network.ramp.cceventize.ui.lost_password

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.network.ramp.cceventize.R
import com.network.ramp.cceventize.databinding.ActivityLostPasswordBinding
import com.network.ramp.cceventize.ui.login.afterTextChanged

class LostPasswordActivity : AppCompatActivity() {

    private lateinit var lostPasswordViewModel: LostPasswordViewModel
    private lateinit var binding: ActivityLostPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLostPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val email = binding.lostPassEmail
        val submit = binding.lostPassButtonSubmit

        lostPasswordViewModel = ViewModelProvider(this, LostPasswordViewModelFactory())
            .get(LostPasswordViewModel::class.java)

        lostPasswordViewModel.lostPasswordFormState.observe(this@LostPasswordActivity, Observer {
            val lostPasswordFormState = it ?: return@Observer

            // disable login button unless both username / password is valid
            submit.isEnabled = lostPasswordFormState.isDataValid

            if (lostPasswordFormState.emailError != null) {
                email.error = getString(lostPasswordFormState.emailError!!)
            }
        })

        email.afterTextChanged {
            lostPasswordViewModel.lostPasswordDataChanged(
                email.text.toString()
            )
        }

        submit.setOnClickListener {
            auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@LostPasswordActivity,
                            R.string.lost_pass_send_email,
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}