package com.example.valkyria

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PasswordAdapter(
    private var listaOriginal: List<PasswordItem>,
    private val onClick: (PasswordItem) -> Unit):
    RecyclerView.Adapter<PasswordAdapter.ViewHolder>() {

    private var listaFiltrada = listaOriginal.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.titulo)
        val subtitulo: TextView = view.findViewById(R.id.subtitulo)
        val icono: ImageView = view.findViewById(R.id.icono)
        val copiar: ImageView = view.findViewById(R.id.btn_copy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listaFiltrada.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = listaFiltrada[position]

        holder.titulo.text = item.nombre
        holder.subtitulo.text = item.usuario
        holder.icono.setImageResource(item.icono)
        holder.copiar.setOnClickListener {
            val clipboard = holder.itemView.context
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clip = ClipData.newPlainText("dato", item.contrasena)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(holder.itemView.context, "Copiado", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun filtrar(texto: String) {
        listaFiltrada = if (texto.isEmpty()) {
            listaOriginal.toMutableList()
        } else {
            listaOriginal.filter {
                it.nombre.contains(texto, ignoreCase = true) ||
                        it.usuario.contains(texto, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun actualizarLista(nuevaLista: List<PasswordItem>) {
        listaOriginal = nuevaLista
        listaFiltrada = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}