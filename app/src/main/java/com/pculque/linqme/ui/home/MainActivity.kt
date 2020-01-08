package com.pculque.linqme.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ShareCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.pculque.linqme.data.CardHelper

import kotlinx.android.synthetic.main.activity_main.*

import com.google.gson.GsonBuilder
import com.google.gson.internal.`$Gson$Types`
import com.pculque.linqme.*
import com.pculque.linqme.data.PreferenceHelper
import com.pculque.linqme.data.PreferenceHelper.readTerm
import com.pculque.linqme.ui.TermActivity
import com.pculque.linqme.ui.detail.DetailActivity
import com.pculque.linqme.ui.home.adapter.*
import com.pculque.linqme.ui.scanner.CameraScannerActivity
import com.pculque.linqme.util.AppConstants
import com.pculque.linqme.util.FileUtils
import org.jetbrains.anko.alert
import br.com.hands.mdm.libs.android.geobehavior.MDMGeoBehavior
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAX_ITEM_COUNT: Int = 6
        const val RESULT_CODE = 3
    }

    private val dbHandler = CardHelper(this)
    lateinit var adapter: StackCardAdapter


    private fun startDetail(cardViewModel: CardViewModel) {
        // val intent = Intent(this, DetailActivity::class.java)
        val id = cardViewModel.id

        val intent = Intent(this, DetailActivity::class.java)
        Log.d("onActivityResult", "startDetail")

        intent.putExtra("card_id", id)
        startActivityForResult(intent, RESULT_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Pedir permissão de Geolocalização ao usuário e inicia o módulo de GeoBehavior
        requestGeoTrackingPermissions()

        setSupportActionBar(toolbar)
        adapter = StackCardAdapter(this)
        supportActionBar.apply {
            title = ""
        }

        button_camera.setOnClickListener {
            startActivity(Intent(this, CameraScannerActivity::class.java))
        }

        //dbHandler.clear()
        setupCards()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        adapter.apply {
            onItemClickListener = { _, cardViewModel ->
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
        //val prefs = PreferenceHelper.customPreference(this)
        //prefs.readTerm = false
        val prefs = PreferenceHelper.customPreference(this)

        if (!prefs.readTerm) {
            alert("Ao utilizar o nosso App você aceita os termos de uso") {
                title = "Termos e Condições"
                positiveButton("Aceitar") {
                    prefs.readTerm = true
                }
                negativeButton("Cancelar") {
                    recuseTermActivity()
                }
                neutralPressed("Ler mais...") {
                    openTermActivity()
                }
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()

        //  startActivity(Intent(this, TermActivity::class.java))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CODE) {
            adapter.apply {
                submitList(getCardViewModel())
            }
        }
    }

    private fun openTermActivity() {
        startActivity(Intent(this, TermActivity::class.java))
    }

    private fun recuseTermActivity() {
        val prefs = PreferenceHelper.customPreference(this)

        prefs.readTerm = false
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
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
                        if (it.getTypeId() == TypeCard.YOUTUBE && it.secondaryValue.isEmpty())
                            postValue("Digite o ID do seu canal")
                        else if (it.getTypeId() == TypeCard.INSTAGRAM && it.secondaryValue.isEmpty())
                            postValue("Digite o ID da sua conta (@meu_id)")
                        else if (it.getTypeId() == TypeCard.WHATSAPP && it.secondaryValue.isEmpty())
                            postValue("Digite o seu número")
                        else if (it.getTypeId() == TypeCard.LINKEDIN && it.secondaryValue.isEmpty())
                            postValue("Digite o seu cargo")
                        else
                            postValue(it.secondaryValue)
                    }, primaryValue = MutableLiveData<String>().apply {
                        if (it.primaryValue.isEmpty())
                            postValue("Digite o seu Nome")
                        else
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

    // Método exemplo para pedir permissão de geolocalização
    @SuppressLint("InlinedApi")
    private fun requestGeoTrackingPermissions() {
        // Verifica Google Play Services
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(applicationContext)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, 9000).show()
            }
            // Usuário precisa atualizar o Google Play Services
            return
        }

        // Verifica permissão de geolocalização
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Requisitar permissão de geolocalização
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                ),
                200
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (200 == requestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permissão concedida, inicializar módulo GeoBehavior
                MDMGeoBehavior.start(applicationContext)
            } else {
                // Permissão não concedida
            }
        }
    }

}
