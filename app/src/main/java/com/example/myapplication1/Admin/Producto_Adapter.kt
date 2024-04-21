package com.example.myapplication1.Admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.Filter

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication1.Main.Utilities
import com.example.myapplication1.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference



class Producto_Adapter(private val products_list: MutableList<Producto_Item>) :
    RecyclerView.Adapter<Producto_Adapter.ProductoViewHolder>(), Filterable {

    private lateinit var context: Context
    private var filtered_list = products_list
    private lateinit var sharedPreferences: SharedPreferences
    private var isAdmin = false
    private val db = FirebaseDatabase.getInstance().getReference()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Producto_Adapter.ProductoViewHolder {
        val item_view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        context = parent.context
        sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        isAdmin = sharedPreferences.getBoolean("isAdmin", false)
        return ProductoViewHolder(item_view)
    }

    override fun onBindViewHolder(holder: Producto_Adapter.ProductoViewHolder, position: Int) {
        val current_object = filtered_list[position]
        holder.name.text = current_object.nombre
        holder.category.text = current_object.tipo
        holder.price.text = current_object.precio.toString()
        holder.stock.text = current_object.stock?.toString() ?: "0"
        holder.availability.text = current_object.disponible.toString()
        holder.valoracionMedia.rating = current_object.valoracionMedia?.toFloat() ?: 0f // Agregado
        val URL: String? = when (current_object.url_firebase_img) {
            "" -> null
            else -> current_object.url_firebase_img
        }

        Glide.with(context)
            .load(URL)
            .apply(Utilities.glideOptions(context))
            .transition(Utilities.transitions)
            .into(holder.logo)

        // Aquí va el resto del código de onBindViewHolder, actualizado para Producto_Item
        if (isAdmin) {
            holder.btnBuy.visibility = View.GONE
            holder.constraint_row.setOnClickListener {
                val bundle = Bundle().apply {
                    putParcelable("product", current_object)
                }
                it.findNavController()
                    .navigate(R.id.action_productoFragment_Check_Edit_to_productoFragmentEdit, bundle)

            }

        }
    }

    override fun getItemCount(): Int = filtered_list.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.iv_item_product_logo)
        val name: TextView = itemView.findViewById(R.id.productAdd_name)
        val category: TextView = itemView.findViewById(R.id.productAdd_category)
        val price: TextView = itemView.findViewById(R.id.productAdd_price)
        val stock: TextView = itemView.findViewById(R.id.productAdd_stock)
        val availability: TextView = itemView.findViewById(R.id.productAdd_availability)
        val valoracionMedia: RatingBar = itemView.findViewById(R.id.tv_item_product_valoracion_media) // Corregido
        val constraint_row: ConstraintLayout = itemView.findViewById(R.id.constraint_row)
        val btnBuy: AppCompatButton = itemView.findViewById(R.id.btn_item_pro_aceptOrder)
        val btnCancel: AppCompatButton = itemView.findViewById(R.id.btn_item_pro_cancel)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()) {
                    filtered_list = products_list
                } else {
                    filtered_list = (products_list.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Producto_Item>
                }

                val filterResults = FilterResults()
                filterResults.values = filtered_list
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}