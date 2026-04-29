package com.rambo.ramcryptr

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rambo.ramcryptr.encoding.AES256Encoder

class MainActivity : AppCompatActivity() {

    private lateinit var inputBox: EditText
    private lateinit var encodeBtn: Button
    private lateinit var decodeBtn: Button

    private val defaultKey =
        "12345678901234567890123456789012"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputBox = findViewById(R.id.inputBox)
        encodeBtn = findViewById(R.id.btnEncode)
        decodeBtn = findViewById(R.id.btnDecode)

        encodeBtn.setOnClickListener {
            encodeText()
        }

        decodeBtn.setOnClickListener {
            decodeText()
        }
    }

    private fun encodeText() {

        val text = inputBox.text.toString()

        if(text.isBlank()){
            toast("Enter text first")
            return
        }

        try{
            val encrypted =
                AES256Encoder.encryptText(
                    text,
                    defaultKey
                )

            showResult(encrypted)

        }catch(e:Exception){
            toast("Encoding failed")
        }
    }

    private fun decodeText(){

        val text=inputBox.text.toString()

        if(text.isBlank()){
            toast("Enter encoded text")
            return
        }

        try{
            val decoded=
                AES256Encoder.decryptText(
                    text,
                    defaultKey
                )

            showResult(decoded)

        }catch(e:Exception){
            toast("Decoding failed")
        }
    }

    private fun showResult(result:String){

        AlertDialog.Builder(this)
            .setTitle("Result")
            .setMessage(result)
            .setPositiveButton("Copy"){_,_->

                val clip=
                    getSystemService(
                    CLIPBOARD_SERVICE
                    ) as
                    android.content.ClipboardManager

                clip.setPrimaryClip(
                    android.content.ClipData
                    .newPlainText(
                        "encoded",
                        result
                    )
                )
            }
            .setNegativeButton("Close",null)
            .show()
    }

    private fun toast(msg:String){
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}
