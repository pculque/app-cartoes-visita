package com.pculque.linqme

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.ShareCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amyu.stack_card_layout_manager.StackCardLayoutManager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var typeCardSelected = TypeCard.BUSSINES
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar.apply {
            title = ""
        }
        val dbHandler = MindOrksDBOpenHelper(this, null)
        val user = Card("s")
        dbHandler.addName(user)
        Toast.makeText(this, "Added to database", Toast.LENGTH_LONG).show()

        val cursor = dbHandler.getAllName()
        cursor!!.moveToFirst()
        //tvDisplayName.append((cursor.getString(cursor.getColumnIndex(MindOrksDBOpenHelper.COLUMN_PRIMARY_VALUE))))
        while (cursor.moveToNext()) {
            Log.e(
                "Debug",
                cursor.getString(cursor.getColumnIndex(MindOrksDBOpenHelper.COLUMN_PRIMARY_VALUE))
            )
            Log.e("Debug", "\n")
        }
        cursor.close()

        button_camera.setOnClickListener {
            startActivity(Intent(this, CameraScannerActivity::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = StackCardAdapter(applicationContext).apply {
            onItemClickListener = { cardView, cardViewModel ->
                val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    cardView,
                    cardView.transitionName
                )
                typeCardSelected = cardViewModel.type

                startActivityWithOptions(compat) {
                    DetailActivity.createIntent(
                        it,
                        cardViewModel.primaryValue.value!!,
                        cardViewModel.secondaryLabel.value!!,
                        cardViewModel.secondaryValue.value!!,
                        cardViewModel.auxiliaryLabel.value!!,
                        cardViewModel.auxiliaryValue.value!!,
                        cardViewModel.backgroundColor.value!!,
                        cardViewModel.labelColor.value!!,
                        cardViewModel.valueColor.value!!,
                        cardViewModel.logo.value!!,
                        cardViewModel.thumbnail.value!!
                    ).putExtra("type", typeCardSelected)
                }
            }

            submitList(
                listOf(
                    CardViewModel(

                        backgroundColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FF0004"))
                        },
                        logo = MutableLiveData<Int>().apply {
                            postValue(R.drawable.logo_youtube)
                        }, labelColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, valueColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, thumbnail = MutableLiveData<Int>().apply {
                            postValue(R.drawable.thumbnail)
                        }, secondaryLabel = MutableLiveData<String>().apply {
                            postValue("CHANNEL")
                        }, secondaryValue = MutableLiveData<String>().apply {
                            postValue("YouTube Channel")
                        }, primaryValue = MutableLiveData<String>().apply {
                            postValue("Amauri Zerillo Jr.")
                        }, auxiliaryLabel = MutableLiveData<String>().apply {
                            postValue("")
                        }, auxiliaryValue = MutableLiveData<String>().apply {
                            postValue("")
                        }
                        , type = TypeCard.YOUTUBE
                    ),

                    CardViewModel(
                        backgroundColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#3C5A99"))
                        },
                        logo = MutableLiveData<Int>().apply {
                            postValue(R.drawable.logo_facebook)
                        }, labelColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, valueColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, thumbnail = MutableLiveData<Int>().apply {
                            postValue(R.drawable.thumbnail)
                        }, secondaryLabel = MutableLiveData<String>().apply {
                            postValue("")
                        }, secondaryValue = MutableLiveData<String>().apply {
                            postValue("")
                        }, primaryValue = MutableLiveData<String>().apply {
                            postValue("Amauri Zerillo Jr.")
                        }, auxiliaryLabel = MutableLiveData<String>().apply {
                            postValue("")
                        }, auxiliaryValue = MutableLiveData<String>().apply {
                            postValue("")
                        }, type = TypeCard.FACBOOK
                    ),
                    CardViewModel(
                        backgroundColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        },
                        logo = MutableLiveData<Int>().apply {
                            postValue(R.drawable.logo_instagram)
                        }, labelColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#212121"))
                        }, valueColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#212121"))
                        }, thumbnail = MutableLiveData<Int>().apply {
                            postValue(R.drawable.thumbnail)
                        }, secondaryLabel = MutableLiveData<String>().apply {
                            postValue("FOLLOW")
                        }, secondaryValue = MutableLiveData<String>().apply {
                            postValue("@linqme")
                        }, primaryValue = MutableLiveData<String>().apply {
                            postValue("Amauri Zerillo Jr.")
                        }, auxiliaryLabel = MutableLiveData<String>().apply {
                            postValue("FOLLOW:")
                        }, auxiliaryValue = MutableLiveData<String>().apply {
                            postValue("@instagram")
                        }, type = TypeCard.INSTAGRAM

                    ),
                    CardViewModel(
                        backgroundColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#1EBEA5"))
                        },
                        logo = MutableLiveData<Int>().apply {
                            postValue(R.drawable.logo_whatsapp)
                        }, labelColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, valueColor = MutableLiveData<Int>().apply {
                            postValue(Color.parseColor("#FFFFFF"))
                        }, thumbnail = MutableLiveData<Int>().apply {
                            postValue(R.drawable.thumbnail)
                        }, secondaryLabel = MutableLiveData<String>().apply {
                            postValue("MOBILE:")
                        }, secondaryValue = MutableLiveData<String>().apply {
                            postValue("+55 11 9 8785-4040")
                        }, primaryValue = MutableLiveData<String>().apply {
                            postValue("Amauri Zerillo Jr.")
                        }, auxiliaryLabel = MutableLiveData<String>().apply {
                            postValue("MOBILE:")
                        }, auxiliaryValue = MutableLiveData<String>().apply {
                            postValue("+55 11 9 8785-4040")
                        }, type = TypeCard.WHATSAPP
                    )
                )
            )
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StackCardLayoutManager(5)
        val itemDecor = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = true.also {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    adapter.notifyItemMoved(fromPos, toPos)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }
            })
        itemDecor.attachToRecyclerView(recyclerView)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val url = "Olá! Acabamos de nos conhecer através do LINQ.me. Baixe agora o seu."

        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = ShareCompat.IntentBuilder
                    .from(this)
                    .setType("text/plain")
                    .setChooserTitle("LINQ.me")
                    .setText(url)
                    .intent

                if (shareIntent.resolveActivity(packageManager) != null) {
                    startActivity(shareIntent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun AppCompatActivity.startActivityWithOptions(
        options: ActivityOptionsCompat,
        function: (context: Context) -> Intent
    ) {
        startActivity(function(applicationContext), options.toBundle())
    }
}
