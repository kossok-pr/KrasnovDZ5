package com.example.android.krasnovdz5

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.popup_window.*

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        databaseHelper = DatabaseHelper(applicationContext)
        db = databaseHelper.writableDatabase

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        fab.setOnClickListener {
            var inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var popupView = inflater.inflate(R.layout.popup_window, popup_root)
            var popupBtn: Button = popupView.findViewById(R.id.popup_add_btn)
            var popupEditText: EditText = popupView.findViewById(R.id.popup_edit_text)
            var popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.showAtLocation(main_content, Gravity.CENTER, 0, 0)
            popupBtn.setOnClickListener {
                var text = popupEditText.text.toString()
                if (!text.isEmpty()) {
                    db.execSQL("INSERT INTO todos (name, active) VALUES ('" + text + "', 'active');")
                    popupWindow.dismiss()
                    container.adapter = mSectionsPagerAdapter
                } else Toast.makeText(applicationContext, "Enter something", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            db = databaseHelper.readableDatabase
            return PlaceholderFragment.newInstance(position, db)
        }

        override fun getCount(): Int {
            return 2
        }
    }

    class PlaceholderFragment : Fragment() {
        private lateinit var todoCursor: Cursor
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            if (arguments.getInt(ARGUMENT_PAGE_NUMBER) == 0) {
                todoCursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("active"))
            } else if (arguments.getInt(ARGUMENT_PAGE_NUMBER) == 1) {
                todoCursor = db.rawQuery("SELECT * FROM todos WHERE active=?", arrayOf("not_active"))
            }
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.recycler_view.layoutManager = LinearLayoutManager(container!!.context, LinearLayoutManager.VERTICAL, false)
            rootView.recycler_view.adapter = TodosAdapter(todoCursor)
            return rootView
        }

        companion object {
            private val ARGUMENT_PAGE_NUMBER = "arg_page_number"
            private lateinit var db: SQLiteDatabase
            fun newInstance(position: Int, db0: SQLiteDatabase): PlaceholderFragment {
                db = db0
                val fragment = PlaceholderFragment()
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PAGE_NUMBER, position)
                fragment.arguments = arguments
                return fragment
            }
        }
    }
}
