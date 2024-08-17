package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentHomeBinding
import com.example.kotlintodopractice.utils.adapter.TaskAdapter
import com.example.kotlintodopractice.utils.model.UserData
import com.example.kotlintodopractice.MyFirebaseMessagingService
import com.example.kotlintodopractice.fragments.ToDoDialogFragment.Companion
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    TaskAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<UserData>
    private lateinit var navController: NavController
    private lateinit var messagingService: MyFirebaseMessagingService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //get data from firebase
        getTaskFromFirebase()


        binding.addTaskBtn.setOnClickListener {

            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
            )

        }

        binding.logoutButton.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
        }
    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val userData = taskSnapshot.getValue(UserData::class.java)
                    Log.d(TAG, "taskSnapShot: " + taskSnapshot.value.toString())
                    val todoTask =
                        taskSnapshot.key?.let { userData }

                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }

                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("users")
            .child(authId)

        messagingService = MyFirebaseMessagingService()
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }

    override fun saveTask(userData: UserData, todoEdit: TextInputEditText) {
        val userId = userData.name+userData.studentId
        database.child(userId).setValue(userData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "User Added Successfully!", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null
//                    messagingService.generateNotification("User Added","A new user was added succesfully")

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun updateTask(userData: UserData, todoEdit: TextInputEditText) {
        val userId=  userData.name+userData.studentId
        Log.d(TAG,"Updating user data in updateTask: "+userData.toString())
            database.child(userId).updateChildren(userData.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(userData: UserData, position: Int) {
        val userId=  userData.name+userData.studentId
        database.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(userData: UserData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()
        frag = ToDoDialogFragment.newInstance(userData)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

}