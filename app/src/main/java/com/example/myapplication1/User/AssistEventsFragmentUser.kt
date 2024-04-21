package com.practicafinal2dam.User

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication1.Admin.AssistAdapter
import com.example.myapplication1.Admin.AssistItem
import com.example.myapplication1.databinding.FragmentAssistEventUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AssistEventsFragmentUser : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentAssistEventUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AssistAdapter
    private lateinit var database: DatabaseReference

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
        _binding = FragmentAssistEventUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn_events: AppCompatButton = binding.events

        btn_events.setOnClickListener {
            findNavController().popBackStack()
        }

        database = FirebaseDatabase.getInstance().getReference("Shop").child("Pending_Events")

        binding.assistEventsFragmentUser.layoutManager = LinearLayoutManager(context)
        adapter = AssistAdapter(mutableListOf(), false)
        binding.assistEventsFragmentUser.adapter = adapter

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(AssistItem::class.java) }
                // Filter events that belong to current user
                val currentUserEvents = events.filter { it.id_user == currentUserId }
                adapter.items.clear()
                adapter.items.addAll(currentUserEvents.reversed())
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssistEventsFragmentUser().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}