package develop.abdusamid.firebaseauthsmssend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import develop.abdusamid.firebaseauthsmssend.databinding.ActivityPhoneBinding
import develop.abdusamid.firebaseauthsmssend.objects.MyObject
import develop.abdusamid.firebaseauthsmssend.objects.MySharedPreferences

class PhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.number.text = MySharedPreferences.list[0]
    }
}