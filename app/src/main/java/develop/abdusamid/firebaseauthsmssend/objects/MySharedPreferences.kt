package develop.abdusamid.firebaseauthsmssend.objects

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MySharedPreferences {
    var number: String? = null
    private const val NAME = "KeshXotiraga"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var list: ArrayList<String>
        get() = gsonStringToArray(sharedPreferences.getString("object", "[]"))
        set(value) = sharedPreferences.edit() {
            it.putString("object", arrayToGsonString(value))
        }

    private fun arrayToGsonString(arrayList: ArrayList<String>): String {
        return Gson().toJson(arrayList)
    }

    private fun gsonStringToArray(gsonString: String?): java.util.ArrayList<String> {
        val typeToken = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(gsonString, typeToken)
    }
}