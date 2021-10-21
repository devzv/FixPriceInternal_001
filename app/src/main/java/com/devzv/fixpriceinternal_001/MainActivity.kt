package com.devzv.fixpriceinternal_001

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.devzv.fixpriceinternal_001.databinding.MainActivityBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: MainActivityBinding
    private val mViewModel: MainViewModel by viewModels { MainViewModelFactory(application) }

    private val fileNameLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        mViewModel.download(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.button.setOnClickListener {
                fileNameLauncher.launch("filename.bin")
            }
        }
        lifecycleScope.launch {
            mViewModel.state.collect { state ->
                when (state) {
                    is Default -> {
                        mBinding.button.isVisible = true
                        mBinding.progress.isVisible = false
                        mBinding.error.isVisible = false
                    }
                    is Loading -> {
                        mBinding.button.isVisible = false
                        mBinding.progress.isVisible = true
                        mBinding.error.isVisible = false
                    }
                    is Complete -> {
                        mBinding.button.isVisible = false
                        mBinding.progress.isVisible = false
                        mBinding.error.isVisible = false
                        Toast.makeText(this@MainActivity, state.msg, Toast.LENGTH_SHORT).show()
                    }
                    is Error -> {
                        mBinding.button.isVisible = false
                        mBinding.progress.isVisible = false
                        mBinding.error.isVisible = true
                        mBinding.error.text = state.throwable.toString()
                        Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                        state.throwable.printStackTrace()
                    }
                }
            }
        }
    }

}