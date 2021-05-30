package com.network.ramp.cceventize.ui.register

import android.content.Intent
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
import com.network.ramp.cceventize.data.model.RegisterUser
import com.network.ramp.cceventize.databinding.ActivityRegisterBinding
import com.network.ramp.cceventize.ui.event_list.EventListActivity
import com.network.ramp.cceventize.ui.login.afterTextChanged

/**
 * Activity class for the register form.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val firstName = binding.etFirstName
        val lastName = binding.etLastName
        val email = binding.etEmail
        val password = binding.etPassword
        val register = binding.btnRegister

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        //Check is correct data input
        registerViewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val registerState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = registerState.isDataValid

            if (registerState.firstNameError != null) {
                firstName.error = getString(registerState.firstNameError)
            }
            if (registerState.lastNameError != null) {
                lastName.error = getString(registerState.lastNameError)
            }
            if (registerState.emailError != null) {
                email.error = getString(registerState.emailError!!)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
        })

        firstName.afterTextChanged {
            registerViewModel.registerDataChanged(
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        lastName.afterTextChanged {
            registerViewModel.registerDataChanged(
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        email.afterTextChanged {
            registerViewModel.registerDataChanged(
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        password.afterTextChanged {
            registerViewModel.registerDataChanged(
                firstName.text.toString(),
                lastName.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }

        // Register button on click event
        register.setOnClickListener {
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser?.uid

                        val registerUser = RegisterUser(firstName.text.toString(), lastName.text.toString(), email.text.toString())
                        val registerUserValues = registerUser.toMap()

                        val childUpdates = hashMapOf<String, Any>(
                            "/users/$user" to registerUserValues
                        )
                        database.updateChildren(childUpdates)

                        val intent = Intent(this, EventListActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}