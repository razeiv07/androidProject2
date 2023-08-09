package com.fanshawe.project2

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var etEmail: TextInputEditText
    private lateinit var btnEmail: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email, container, false)
        etEmail = view.findViewById(R.id.etEmail)
        btnEmail = view.findViewById(R.id.btnEmail)
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val savedEmail = sharedPreferences.getString("email","")
        etEmail.setText(savedEmail)

        btnEmail.setOnClickListener {
            sendEmail()
        }

        // Inflate the layout for this fragment
        return view
    }


    private fun sendEmail() {
        val email = etEmail.text.toString().trim()


        val savedLatitudeString = sharedPreferences.getString("latitude", null)
        val savedLongitudeString = sharedPreferences.getString("longitude", null)
        val savedAddress = sharedPreferences.getString("address", null)

        if (isValidEmail(email)) {
            sharedPreferences.edit().apply {
                putString("email", email)
                apply()
            }

            val uriText = "mailto:$email" +
                    "?subject=" + "$savedLatitudeString,$savedLongitudeString" +
                    "&body=" + "address name: $savedAddress"
            val uri = Uri.parse(uriText)
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.data = uri
            startActivity(
                Intent.createChooser(mailIntent, "Send Email").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

        } else {
            Toast.makeText(
                requireContext(),
                "Please enter a valid email address.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidEmail(target: String): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)


                }
            }
    }
}