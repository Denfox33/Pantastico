package com.example.myapplication1.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication1.R
import com.example.myapplication1.databinding.FragmentEventCheckDeleteEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class EventFragmentCheckDeleteEdit : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var list: MutableList<EventsItem>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adapter: EventsAdapter

    private var _binding: FragmentEventCheckDeleteEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventCheckDeleteEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("Shop").child("Events")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    snapshot.children.forEach { child: DataSnapshot? ->
                        val event = child?.getValue(EventsItem::class.java)
                        list.add(event!!)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        recyclerView = binding.checkEventsList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = EventsAdapter(list)
        recyclerView.adapter = adapter

        // Configurar el ItemTouchHelper después de inicializar el adaptador y el RecyclerView
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT  // Permitir deslizar a la izquierda
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // No necesitas implementar el movimiento, devolver false
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Llama al método onItemDismiss del adaptador cuando se desliza un elemento
                (recyclerView.adapter as EventsAdapter).onItemDismiss(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        binding.mainAdd.setOnClickListener {
            findNavController().navigate(R.id.action_eventsFragmentCheckDeleteEdit_to_eventsFragmentAdd)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}