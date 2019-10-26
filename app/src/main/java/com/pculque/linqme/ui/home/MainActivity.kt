package com.pculque.linqme.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.ShareCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amyu.stack_card_layout_manager.StackCardLayoutManager
import com.pculque.linqme.database.CardHelper

import kotlinx.android.synthetic.main.activity_main.*

import com.google.gson.GsonBuilder
import com.google.gson.internal.`$Gson$Types`
import com.pculque.linqme.*
import com.pculque.linqme.ui.detail.DetailActivity
import com.pculque.linqme.ui.home.adapter.Card
import com.pculque.linqme.ui.home.adapter.CardViewModel
import com.pculque.linqme.ui.home.adapter.StackCardAdapter
import com.pculque.linqme.ui.scanner.CameraScannerActivity
import com.pculque.linqme.util.AppConstants
import com.pculque.linqme.util.FileUtils

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAX_ITEM_COUNT: Int = 6
    }

    private val dbHandler = CardHelper(this)
    lateinit var adapter: StackCardAdapter

    private fun startDetail(cardViewModel: CardViewModel) {
        val intent = Intent(this, DetailActivity::class.java)
        val id = cardViewModel.id

        intent.putExtra("card_id", id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        adapter = StackCardAdapter(this)
        supportActionBar.apply {
            title = ""
        }

        button_camera.setOnClickListener {
            startActivity(Intent(this, CameraScannerActivity::class.java))
        }

        dbHandler.clear()
        setupCards()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        adapter.apply {
            onItemClickListener = { cardView, cardViewModel ->
                /* val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                     this@MainActivity,
                     cardView,
                     cardView.transitionName
                 )
                 val id = cardViewModel.id

                 startActivityWithOptions(compat) {
                     DetailActivity.createIntent(it)
                         .putExtra("card_id", id)
                 }*/
                startDetail(cardViewModel = cardViewModel)
            }
            submitList(getCardViewModel())
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StackCardLayoutManager(MAX_ITEM_COUNT)
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

    override fun onResume() {
        super.onResume()
        adapter.apply {
           // submitList(getCardViewModel())
        }
    }

    private fun getCardViewModel(): List<CardViewModel> {
        val list = mutableListOf<CardViewModel>()
        dbHandler.getAllCards().map {
            list.add(
                CardViewModel(
                    backgroundColor = MutableLiveData<Int>().apply {
                        postValue(Color.parseColor(it.backgroundColor))
                    },
                    labelColor = MutableLiveData<Int>().apply {
                        postValue(Color.parseColor(it.labelColor))
                    }, valueColor = MutableLiveData<Int>().apply {
                        postValue(Color.parseColor(it.valueColor))
                    }, secondaryLabel = MutableLiveData<String>().apply {
                        postValue(it.secondaryLabel)
                    }, secondaryValue = MutableLiveData<String>().apply {
                        postValue(it.secondaryValue)
                    }, primaryValue = MutableLiveData<String>().apply {
                        postValue(it.primaryValue)
                    }, auxiliaryLabel = MutableLiveData<String>().apply {
                        postValue(it.auxiliaryLabel)
                    }, bitmap = MutableLiveData<Bitmap>().apply {
                        //postValue()
                    }, auxiliaryValue = MutableLiveData<String>().apply {
                        postValue(it.auxiliaryValue)
                    }, type = it.getTypeId()
                    , logo = MutableLiveData<Int>().apply {
                        postValue(it.getLogoDrawable())
                    }, id = it.id
                )
            )
        }
        return list
    }

    private fun setupCards() {
        if (dbHandler.isSetupDB()) {
            Log.e("Debug", "Setup is ready")
            return
        }
        val builder = GsonBuilder().excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()
        val type =
            `$Gson$Types`.newParameterizedTypeWithOwner(null, List::class.java, Card::class.java)
        val cardList = gson.fromJson<List<Card>>(
            FileUtils.loadJSONFromAsset(
                this,
                AppConstants.SEED_DATABASE_QUESTIONS
            ),
            type
        )
        cardList.map { card -> dbHandler.addCard(card) }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val url = getString(R.string.share_app)

        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = ShareCompat.IntentBuilder
                    .from(this)
                    .setType("text/plain")
                    .setChooserTitle(getString(R.string.app_name))
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
