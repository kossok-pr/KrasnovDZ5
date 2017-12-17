package com.example.android.krasnovdz5

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ViewSwitcher
import kotlinx.android.synthetic.main.item.view.*

class TodosAdapter(private var context: Context, private var cursor: Cursor, private val active: Boolean) : RecyclerView.Adapter<TodosAdapter.TodosViewHolder>() {

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
                if (active == true) holder.completeBtn.visibility = View.VISIBLE
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
            db.execSQL("DELETE FROM todos WHERE _id = " + cursor.getString(cursor.getColumnIndexOrThrow("_id")))
            if (active == true) cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("active"))
            else cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("not_active"))
            notifyDataSetChanged()
        }
        holder.completeBtn.setOnClickListener {
            cursor.moveToPosition(position)
            db = databaseHelper.writableDatabase
            db.execSQL("UPDATE todos SET active = 'not_active' WHERE _id = " + cursor.getString(cursor.getColumnIndexOrThrow("_id")))
            if (active == true) cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("active"))
            //else cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("not_active"))
            notifyDataSetChanged()
        }
        holder.todo.setOnLongClickListener {
            holder.switcher.showNext()
            holder.editText.setText(holder.todo.text)
            holder.applyBtn.visibility = View.VISIBLE
            holder.applyBtn.setOnClickListener {
                var text = holder.editText.text.toString()
                cursor.moveToPosition(position)
                db = databaseHelper.writableDatabase
                db.execSQL("UPDATE todos SET name = '" + text + "' WHERE _id = " + cursor.getString(cursor.getColumnIndexOrThrow("_id")))
                if (active == true) cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("active"))
                else cursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("not_active"))
                notifyDataSetChanged()
                holder.switcher.showPrevious()
                holder.applyBtn.visibility = View.GONE
            }
            true
        }
    }

    override fun getItemCount() = cursor.count

    class TodosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isUSD = false
        var todo: TextView = view.item_todo_text
        var deleteBtn: Button = view.item_delete_btn
        var completeBtn: Button = view.item_complete_btn
        var editText: EditText = view.item_edit_text
        var switcher: ViewSwitcher = view.item_switcher
        var applyBtn: Button = view.item_apply_btn
    }
}