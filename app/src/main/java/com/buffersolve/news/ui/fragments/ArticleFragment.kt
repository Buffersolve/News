package com.buffersolve.news.ui.fragments

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.buffersolve.news.R
import com.buffersolve.news.databinding.FragmentArticleBinding
import com.buffersolve.news.models.Article
import com.buffersolve.news.ui.NewsActivity
import com.buffersolve.news.ui.NewsViewModel
import com.bumptech.glide.Glide
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    lateinit var article: Article

    // Fragment onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fragment onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // VM & Article
        viewModel = (activity as NewsActivity).viewModel
        article = args.article

        // Init Fun Parse HTML
        viewModel.parseHTML(article)

        // Live Data Observer
        viewModel.parsedText.observe(viewLifecycleOwner, Observer {
            binding.tv.text = it
        })

        Log.d("URL11", article.url)

        // Justify Text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.tv.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        // Img
        Glide.with(this).load(article.urlToImage).into(binding.imageView)

        // New Tool Bar Api
        (activity as NewsActivity).setSupportActionBar(binding.toolBar)
        setToolBar()
        binding.appBar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()))

    }

    private fun setToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tool_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.app_bar_save) {
                    viewModel.saveArticle(article)
                    view?.let { Snackbar.make(it, "Article Saved", Snackbar.LENGTH_SHORT).show() }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    // Fragment onDestroyView
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun toast(str: String) {
        Toast.makeText(
            requireContext(),
            str,
            Toast.LENGTH_SHORT).show()
    }

}