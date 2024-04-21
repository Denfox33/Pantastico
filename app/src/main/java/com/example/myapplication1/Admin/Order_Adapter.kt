package com.example.myapplication1.Admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.myapplication1.R
import com.example.myapplication1.User.Product_Reservation
import com.google.firebase.database.FirebaseDatabase

class Order_Adapter(val orders: MutableList<Product_Reservation>, val isAdmin: Boolean) :
    RecyclerView.Adapter<Order_Adapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_item_card_name_data)
        val type: TextView = itemView.findViewById(R.id.tv_item_card_category_data)
        val price: TextView = itemView.findViewById(R.id.tv_item_card_price_data)
        val status: TextView = itemView.findViewById(R.id.tv_item_card_availability_data)
        val image: ImageView = itemView.findViewById(R.id.iv_item_product_logo)
        val btnAccept: AppCompatButton = itemView.findViewById(R.id.btn_item_pro_aceptOrder)
        val btnCancel: AppCompatButton = itemView.findViewById(R.id.btn_item_pro_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.name.text = order.productName
        holder.type.text = order.productType
        holder.price.text = order.price?.toString()
        holder.status.text = order.status
        Glide.with(holder.itemView.context).load(order.url_firebase_img).into(holder.image)

        // Hide buttons if user is not admin
        if (!isAdmin) {
            holder.btnAccept.visibility = View.GONE
            holder.btnCancel.visibility = View.GONE
        } else {
            holder.btnAccept.visibility = View.VISIBLE
            holder.btnCancel.visibility = View.VISIBLE
        }

        holder.btnAccept.setOnClickListener {
            updateOrderStatus(order, "Accepted")
        }

        holder.btnCancel.setOnClickListener {
            updateOrderStatus(order, "Cancelled")
        }
    }

    private fun updateOrderStatus(order: Product_Reservation, status: String) {
        val db = FirebaseDatabase.getInstance().getReference()
        db.child("Shop").child("Pending_Orders").child(order.id!!).child("status").setValue(status)
            .addOnSuccessListener {
                orders.remove(order)
                notifyDataSetChanged()
            }
    }

    override fun getItemCount() = orders.size
}