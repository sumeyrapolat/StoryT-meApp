package com.example.englishnotebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishnotebook.model.SignUpUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor( private val auth: FirebaseAuth,
                                           private val db: FirebaseFirestore) : ViewModel() {

    // Kayıt durumu için StateFlow kullanımı
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState = _signUpState.asStateFlow()


    fun signUp(email: String, password: String) {
        _signUpState.value = SignUpState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _signUpState.value = SignUpState.Success("Sign up successful!")
                    } else {
                        _signUpState.value = SignUpState.Error(task.exception?.message ?: "Unknown error")
                    }
                }
        }
    }

    fun saveUserToFirestore(userId: String, firstName: String, lastName: String, email: String) {

        val signUpUser = SignUpUser(
            firstName = firstName,
            lastName = lastName,
            email = email
        )

        db.collection("Users").document(userId).set(signUpUser)
            .addOnSuccessListener {
                _signUpState.value = SignUpState.Success("User saved to Firestore")
            }
            .addOnFailureListener { e ->
                _signUpState.value = SignUpState.Error("Failed to save user to Firestore: ${e.message}")

         }
    }



}

// Kayıt durumu için durum sınıfları
sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    data class Success(val message: String) : SignUpState()
    data class Error(val error: String) : SignUpState()
}