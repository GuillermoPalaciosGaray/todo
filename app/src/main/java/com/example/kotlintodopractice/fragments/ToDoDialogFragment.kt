package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.kotlintodopractice.databinding.FragmentToDoDialogBinding
import com.example.kotlintodopractice.utils.model.UserData
import com.google.android.material.textfield.TextInputEditText


class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var userData: UserData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(userData: UserData) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("name", userData.name)
                    putString("lastName", userData.lastName)
                    putString("projectName", userData.projectName)
                    putString("studentId", userData.studentId)
                    putString("tutorName", userData.tutorName)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentToDoDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            userData = UserData(arguments?.getString("name").toString(),
                arguments?.getString("lastName").toString(),
                arguments?.getString("studentId").toString(),
                arguments?.getString("projectName").toString(),
                arguments?.getString("tutorName").toString())

            binding.name.setText(userData?.name)
            binding.lastName.setText(userData?.lastName)
            binding.studentId.setText(userData?.studentId)
            binding.projectName.setText(userData?.projectName)
            binding.tutorName.setText(userData?.tutorName)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val name = binding.name.text.toString()
            val studentId = binding.studentId.text.toString()
            val userId = name+studentId
            if(userId.isNotEmpty()) {
                if (userData?.lastName?.isEmpty() == true) {
                    val lastName    = binding.lastName.text.toString()
                    val projectName = binding.projectName.text.toString()
                    val tutorName = binding.tutorName.text.toString()
                    userData = UserData(name,lastName,studentId,projectName,tutorName)
                    listener?.saveTask(userData!!, binding.name)
                    Log.d(TAG, "Saving user data")
                } else {
                    val lastName    = binding.lastName.text.toString()
                    val projectName = binding.projectName.text.toString()
                    val tutorName = binding.tutorName.text.toString()
                    userData = UserData(name,lastName,studentId,projectName,tutorName)
                    Log.d(TAG, "Updating user data: "+userData.toString())
                    listener?.updateTask(userData!!, binding.name)
                }
            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(userData: UserData , todoEdit:TextInputEditText)
        fun updateTask(userData: UserData , todoEdit:TextInputEditText)
    }

}