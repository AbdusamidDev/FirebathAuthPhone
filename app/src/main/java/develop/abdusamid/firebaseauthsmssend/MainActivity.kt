package develop.abdusamid.firebaseauthsmssend

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import develop.abdusamid.firebaseauthsmssend.databinding.ActivityMainBinding
import develop.abdusamid.firebaseauthsmssend.objects.MyObject
import develop.abdusamid.firebaseauthsmssend.objects.MySharedPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MySharedPreferences.init(this)
        if (MySharedPreferences.list.isEmpty()) {
            binding.signIn.setOnClickListener {
                val text = binding.edtNumber.text.toString()
                if (text.isNotEmpty() && text.length == 13 && text.substring(
                        0,
                        text.length - 9
                    ) == "+998"
                ) {
                    MyObject.number = text
                    startActivity(Intent(this, SMSCodeActivity::class.java))
                } else {
                    Toast.makeText(this, "This Is Not Uzbek Number", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            finish()
            startActivity(Intent(this, PhoneActivity::class.java))
        }
    }
}