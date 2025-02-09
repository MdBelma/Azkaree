package com.ibrahim7.azkaree.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.FragmentMusbihaBinding // Import the generated binding class for fragment_musbiha
import com.ibrahim7.azkaree.databinding.SelectnumberPopupBinding // Import the generated binding class for selectnumber_popup
import java.util.*

class MusbihaFragment : Fragment() {

    private lateinit var binding: FragmentMusbihaBinding // Declare the binding variable for fragment_musbiha
    private val share by lazy {
        requireActivity().getSharedPreferences("file", Activity.MODE_PRIVATE)
    }
    private val editor by lazy {
        share.edit()
    }

    private var count = 0
    private var selectednum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusbihaBinding.inflate(inflater, container, false) // Initialize the binding
        val view = binding.root // Get the root view from the binding
        /*(activity as Activity).toolbar_main.title = "المسبحة"*/
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        toolbar.title = "المسبحة"

        count = share.getInt("count", 0)
        selectednum = share.getLong("number", 100).toInt()

        RandomText()
        setCount(count)

        binding.btnCount.setOnClickListener { // Use binding to access the Button
            if (count < share.getLong("number", 100).toInt()) {
                count++
                setCount(count)
            } else {
                val vibrate = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrate.vibrate(700)
                Toast.makeText(requireActivity(), "أتممت العدد المحدد سيتم تصفير العداد", Toast.LENGTH_SHORT).show()
                count = 0
                setCount(count)
            }
        }

        binding.btnSpacficeNumber.setOnClickListener { // Use binding to access the Button
            chooseNumber()
        }

        binding.btnRestart.setOnClickListener { // Use binding to access the Button
            count = 0
            setCount(0)
        }

        return view
    }

    private fun setCount(i: Int) {
        binding.circleCountNumber.text = i.toString() // Use binding to access the TextView
        editor.putInt("count", i).apply()
    }

    private fun chooseNumber() {
        val mDialog = Dialog(requireActivity())
        val dialogBinding = SelectnumberPopupBinding.inflate(LayoutInflater.from(requireContext())) // Use binding for the dialog layout
        mDialog.setContentView(dialogBinding.root) // Set the dialog content view using the binding root
        mDialog.show()
        dialogBinding.btnSave.setOnClickListener { // Use binding to access the Button in the dialog
            if (dialogBinding.etxtNumber.text.isNotEmpty()) { // Use binding to access the EditText in the dialog
                editor.putLong("number", dialogBinding.etxtNumber.text.toString().toLong()) // Use binding to access the EditText in the dialog
                mDialog.dismiss()
            } else {
                editor.putLong("number", 1000)
                mDialog.dismiss()
            }
            setCount(0)
            count = 0
        }
    }

    private fun RandomText() {
        Thread {
            while (true) {
                Thread.sleep(1 * 60000)
                val azkare = arrayOf(
                    "الحمدلله",
                    "لا اله الا الله",
                    "الله أكبر",
                    "استغفر الله العظيم",
                    "لا حول ولا قوة الا بالله",
                    "سبحان الله و بحمده"
                )
                val random = Random()
                val num = random.nextInt(azkare.size)
                binding.txt.text = azkare[num] // Use binding to access the TextView
            }
        }.start()
    }
}