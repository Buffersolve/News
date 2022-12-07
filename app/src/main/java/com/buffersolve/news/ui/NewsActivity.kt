package com.buffersolve.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.buffersolve.news.R
import com.buffersolve.news.databinding.ActivityNewsBinding
import com.buffersolve.news.db.ArticleDatabase
import com.buffersolve.news.repository.NewsRepository
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dynamic colors A12 & Navigation Bar Color & Status Bar
        DynamicColors.applyToActivityIfAvailable(this)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this)

        //Binding
        binding = ActivityNewsBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        //News Repository
        val newsRepository = NewsRepository(ArticleDatabase(this))

        // ViewModel
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]

        //Hav Host & Controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        //Bottom Navigation View
        binding.bottomNavigationView.setupWithNavController(navController)

//        binding.bottomNavigationView.setOnItemReselectedListener {
//            navController.popBackStack(it.itemId, true)
//        }

    }

}