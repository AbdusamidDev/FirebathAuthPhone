package develop.abdusamid.firebaseauthsmssend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import develop.abdusamid.firebaseauthsmssend.databinding.ActivitySmscodeBinding
import develop.abdusamid.firebaseauthsmssend.objects.MyObject
import develop.abdusamid.firebaseauthsmssend.objects.MySharedPreferences
import java.util.concurrent.TimeUnit

class SMSCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmscodeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationID: String
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken
    private var number = MyObject.number
    private lateinit var handler: Handler
    private var countTime = 60
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("uz")
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1)
        sendVerificationCode(number)
        binding.edtSms.addTextChangedListener {
            if (it.toString().length == 6) {
                verifyCode()
            }
        }
        binding.edtSms.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                val view = currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            true
        }
        mySubString()
    }

    @SuppressLint("SetTextI18n")
    private fun mySubString() {
        val bcode = "+998 (${number.substring(4, number.length - 7)}"
        val codes = "$bcode ${number.substring(6, number.length - 4)}-**-**"
        val ccode = "$bcode ${number.substring(6, number.length - 4)}"
        val dcode = "$ccode-${number.substring(number.length - 4, number.length - 2)}"
        val ecode = "$dcode-${number.substring(number.length - 2, number.length)}"
        binding.textNumber.text = "One-time code $codes\n sent to the number"
        MyObject.number = ecode
    }

    private fun verifyCode() {
        try {
            val credential = PhoneAuthProvider.getCredential(
                storedVerificationID,
                binding.edtSms.text.toString()
            )
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfull", Toast.LENGTH_SHORT).show()
                val list = MySharedPreferences.list
                startActivity(Intent(this, PhoneActivity::class.java))
                list.clear()
                list.add(MyObject.number)
            }
//            else {
//                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//                if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                    Toast.makeText(this, "You Entered Wrong Code", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendingToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credentials)
        }

        override fun onVerificationFailed(exception: FirebaseException) {}
        override fun onCodeSent(
            verificationID: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationID, token)
            storedVerificationID = verificationID
            resendingToken = token
        }
    }
    private var runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            if (binding.time.text.toString() != "00:00") {
                countTime--
                if (countTime <= 9) binding.time.text = "00:0$countTime"
                else binding.time.text = "00:$countTime"
                handler.postDelayed(this, 1000)
            } else {
                binding.reset.visibility = View.VISIBLE
                binding.reset.setOnClickListener {
                    resendCode(number)
                    countTime = 60
                    binding.reset.visibility = View.INVISIBLE
                }
            }
        }
    }
}