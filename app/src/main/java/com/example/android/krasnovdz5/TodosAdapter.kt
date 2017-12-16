package com.example.android.krasnovdz5

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.item.view.*

class TodosAdapter(private val cursor: Cursor) : RecyclerView.Adapter<TodosAdapter.TodosViewHolder>() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TodosViewHolder {
        databaseHelper = DatabaseHelper(parent.context)
        return TodosViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: TodosViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.todo.text = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        holder.todo.setOnClickListener {
            if(holder.isUSD == false) {
                holder.deleteBtn.visibility = View.VISIBLE
                holder.completeBtn.visibility = View.VISIBLE
                holder.isUSD = !holder.isUSD
            } else {
                holder.deleteBtn.visibility = View.GONE
                holder.completeBtn.visibility = View.GONE
                holder.isUSD = !holder.isUSD
            }
        }
        holder.deleteBtn.setOnClickListener {
            cursor.moveToPosition(position)
            db = databaseHelper.writableDatabase
            db.execSQL("DELETE FROM todos WHERE _id = " + cursor.getColumnIndexOrThrow("_id"))
        }
    }

    override fun getItemCount() = cursor.count

    class TodosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isUSD = false
        var todo: TextView = view.item_todo_text
        var deleteBtn: Button = view.item_delete_btn
        var completeBtn: Button = view.item_complete_btn
    }
}