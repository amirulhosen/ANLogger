package com.lib.networklogger.remote.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lib.networklogger.R
import com.networklogger.NetworkLoggerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductsActivity : AppCompatActivity() {
    private val viewModel: ProductsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewModel.productListRequest.postValue(Unit)
        viewModel.productList.observe(this) {
            findViewById<TextView>(R.id.productText).text = it.title
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, NetworkLoggerActivity::class.java))
        }
    }
}