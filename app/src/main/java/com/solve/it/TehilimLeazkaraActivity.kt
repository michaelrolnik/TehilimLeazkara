package com.solve.it

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.text.buildSpannedString


class TehilimLeazkaraActivity : AppCompatActivity() {
    private var isSon: Boolean = true

    // UI Components
    private lateinit var buttonThilim: MaterialButton
    private lateinit var buttonElMale: MaterialButton
    private lateinit var buttonKadishY: MaterialButton
    private lateinit var buttonKadishD: MaterialButton
    private lateinit var buttonEntrance: MaterialButton
    private lateinit var buttonAshkava: MaterialButton
    private lateinit var buttonTfilaLeiluy: MaterialButton
    private lateinit var inputName: EditText
    private lateinit var inputParentName: EditText
    private lateinit var toggleGender: MaterialButton
    private lateinit var textViewNusach: TextView
    private lateinit var toolbar: MaterialToolbar

    // Settings
    private val settingsManager by lazy {
        SettingsManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        initializeViews()
        setupClickListeners()
        updateNusachDisplay()
    }

    private fun initializeViews() {
        // Initialize all view references
        this.isSon = true
        buttonAshkava = findViewById(R.id.buttonAshkava)
        buttonElMale = findViewById(R.id.buttonElMale)
        buttonEntrance = findViewById(R.id.buttonEntrance)
        buttonKadishD = findViewById(R.id.buttonKadishD)
        buttonKadishY = findViewById(R.id.buttonKadishY)
        buttonTfilaLeiluy = findViewById(R.id.buttonTfilaLeiluy)
        buttonThilim = findViewById(R.id.buttonTehilim)
        inputName = findViewById(R.id.name)
        inputParentName = findViewById(R.id.par_name)
        textViewNusach = findViewById(R.id.configed_nusach)
        toggleGender = findViewById(R.id.son_daughter)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupClickListeners() {
        buttonThilim.setOnClickListener {
            handleButtonClick_Tehilim()
        }

        buttonElMale.setOnClickListener {
            handleButtonClick_ElMale()
        }
        toggleGender.setOnClickListener {
            isSon = !isSon
            toggleGender.text = if (isSon) getString(R.string.son) else getString(R.string.girl)
        }
        buttonEntrance.setOnClickListener {
            handleEntranceButtonClick()
        }

        buttonAshkava.setOnClickListener {
            handleAshkavaButtonClick()
        }

        buttonKadishY.setOnClickListener {
            handleButtonClick_KadishY()
        }

        buttonTfilaLeiluy.setOnClickListener {
            handleTfilaLeiluyButtonClick()
        }

        buttonKadishD.setOnClickListener {
            handleButtonClick_KadishD()
        }
    }

    private fun handleEntranceButtonClick() {
        val selection = 0; // RAFI fix get from settings
        val string: String = if (selection == 0) resources.getString(R.string.enterAshkenaz) else resources.getString(
                R.string.enterSfardi
            )

        sendTextToView(updateNikud(string))
    }

    private fun handleAshkavaButtonClick() {
        if (inputName.text.isEmpty()) {
            Toast.makeText(this, R.string.error_missing_nams, Toast.LENGTH_LONG).show()
        }

        val wData = arrayOf(
            R.string.ashkava_w1,
            R.string.girl,
            R.string.ashkava_w_anon,
            R.string.ashkava_w2)
        val mData = arrayOf(
            R.string.ashkava_m1,
            R.string.son,
            R.string.ashkava_m_anon,
            R.string.ashkava_m2)
        toggleGender
            .isChecked
            .select(wData, mData)
            .let { item ->
                buildSpannedString {
                    append(updateNikud(resources.getString(item[0])))
                    append(" ")
                    if (inputName.text.isNotEmpty()) {
                        bold {
                            append(inputName.text)
                            append(" ")
                            append(resources.getString(item[1]))
                            append(" ")
                            append(inputParentName.text)
                        }
                    } else {
                        append(resources.getString(item[2]))
                    }
                    append(" ")
                    append(updateNikud(resources.getString(item[3]))) } }
            .let {
                sendTextToView(SpannableString(it))
            }
    }

    private fun <T> Boolean.select(ifTrue: T, ifFalse: T): T = if (this) ifTrue else ifFalse

    private fun handleTfilaLeiluyButtonClick() {
        val wData = Pair(R.string.girl, R.string.tfilat_leiluy_w)
        val mData = Pair(R.string.son, R.string.tfilat_leiluy_m)

        toggleGender
            .isChecked
            .select(wData, mData).let {
                buildSpannedString {
                    append(updateNikud(resources.getString(R.string.tfila_generic_start)))
                    append(" ")
                    bold {
                        append(inputName.text.toString())
                        append(" ")
                        append(resources.getString(it.first))
                        append(" ")
                        append(inputParentName.text.toString())
                    }
                    append(" ")
                    append(updateNikud(resources.getString(it.second))) } }
            .let {
                sendTextToView(SpannableString(it))
            }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this, R.style.MaterialDialog)
            .setTitle(R.string.about_title)
            .setMessage(R.string.credit)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun handleButtonClick_Tehilim() {
        val nameToProcess = "${inputName.text.toString().trim()}${getString(R.string.neshama)}"

        buildSpannedString {
            setArray()
                .forEach { append(it) }
            getTehilim(nameToProcess)
                .forEach { append(it) } }
            .let {
                sendTextToView(SpannableString(it))
            }
    }

    private fun handleButtonClick_Kadish(kadish: Array<Int>) {
        kadish
            .getOrElse(settingsManager.nusach.toInt()) {
                kadish[0] }
            .let {
                sendTextToView(updateNikud(resources.getString(it)))
            }
    }

    private fun handleButtonClick_KadishY() {
        handleButtonClick_Kadish(
            arrayOf(
                R.string.kadishY_Ashkenaz,
                R.string.kadishY_Sfard,
                R.string.kadishY_Edot,
                R.string.kadishY_Teiman
            )
        )
    }

    private fun handleButtonClick_KadishD() {
        handleButtonClick_Kadish(
            arrayOf(
                R.string.kadishD_Ashkenaz,
                R.string.kadishD_Sfard,
                R.string.kadishD_Edot,
                R.string.kadishD_Teiman
            )
        )
    }

    private fun handleButtonClick_ElMale() {
        if (inputName.text.isBlank() || inputParentName.text.isBlank()) {
            Toast.makeText(this, R.string.error_missing_nams, Toast.LENGTH_LONG).show()
            sendTextToView(updateNikud(getString(R.string.elMaleGeneric)))
            return
        }

        val elMaleText = buildSpannedString {
            append(updateNikud(getString(R.string.elMaleBeginGeneric)))
            append(updateNikud(" ${inputName.text} "))
            append(if (toggleGender.isChecked) getString(R.string.son) else getString(R.string.girl))
            append(updateNikud(" ${inputParentName.text} "))
            append(if (toggleGender.isChecked) getString(R.string.elMaleMan) else getString(R.string.elMaleWoman))
        }

        sendTextToView(SpannableString(elMaleText))
    }

    private fun sendTextToView(text: String) {
        sendTextToView(SpannableString(text))
    }

    private fun sendTextToView(text: Spannable) {
        val intent = Intent(this, ViewTehilim::class.java).apply {
            putExtra(Constants.FONT_TYPE, settingsManager.fontFamily)
            putExtra(Constants.TEXT_BLACK, settingsManager.isBlackText)
            Log.i("RAFI", "settingsManager.isNikudEnabled" + settingsManager.isNikudEnabled)

            putExtra(Constants.TEXT_KEY, text)
        }
        startActivity(intent)
    }

    private fun updateNusachDisplay() {

    }

    override fun onResume() {
        super.onResume()
        updateNusachDisplay()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.first_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val actions = mapOf(
            R.id._settings  to { startActivity(Intent(this, SettingsActivity::class.java)) },
            R.id._about     to { showAboutDialog() }
        )

        return actions[item.itemId]
            ?.let {
                it.invoke()
                true
            }
            ?: run {
                super.onOptionsItemSelected(item)
            }
    }

    private fun updateNikud(text: String): String {
        val nikud = getString(R.string.nikud).toRegex()

        return settingsManager
            .isNikudEnabled
            .select(text, text.replace(nikud, ""))
    }

    private fun getTehilim(name: String): List<SpannedString> {
        val kyt = resources.getStringArray(R.array.kytn)

        //  convert ABC into a Map<Char, Int>
        //  this can be done only once
        val alefBet = resources
            .getStringArray(R.array.alef_bet)
            .mapIndexed { index, letters ->
                letters.map { it to index } }
            .flatten()
            .toMap()

        return name.map { it to alefBet[it] }
            .filter { it.second != null }
            .map { (letter, index) ->
                buildSpannedString {
                    bold {
                        append(letter.toString())
                    }
                    append("\n")
                    append(updateNikud(kyt[index!!]))
                    append("\n\n")
                }
            }
    }

    private fun setArray(): List<SpannedString>  {
        val alefBet = resources.getStringArray(R.array.alef_bet)
        return listOf(
            Triple(11,  2, R.string.tehilimLg),
            Triple( 8,  6, R.string.tehilimTz),
            Triple( 9,  6, R.string.tehilimYz),
            Triple(15,  1, R.string.tehilimAb),
            Triple(22,  0, R.string.tehilimTza),
            Triple(18,  3, R.string.tehilimKd),
            Triple(18, 11, R.string.tehilimKl))
            .map { (frst, scnd, res) ->
                buildSpannedString {
                    bold {
                        append(getString(R.string.perek))
                        append(" ")
                        append(alefBet[frst])
                        append("\"")
                        append(alefBet[scnd])
                        append("\n")
                    }
                    append(updateNikud(getString(res)))
                    append("\n\n")
                }
            }
    }
}

// Helper class for managing settings
class SettingsManager(private val context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val nusach: String
        get() {
            val key = context.getString(R.string.pref_list_key)
            return prefs.getString(key, "0") ?: "0"
        }

    val fontFamily: String
        get() {
            return (prefs.getString(context.getString(R.string.pref_font_list), "0") ?: "0").also {
                Log.d("SettingsManager", "Getting fontFamily: $it")
            }
        }

    val isNikudEnabled: Boolean
        get() {
            val key = context.getString(R.string.pref_is_font_enabled)
            return prefs.getBoolean(key, true).also {
                Log.d("SettingsManager", "Getting isNikudEnabled with key $key: $it")
            }
        }

    val isBlackText: Boolean
        get() {
            return prefs.getBoolean(context.getString(R.string.pref_black_text), true).also {
                Log.d("SettingsManager", "Getting isBlackText: $it")
            }
        }
}
