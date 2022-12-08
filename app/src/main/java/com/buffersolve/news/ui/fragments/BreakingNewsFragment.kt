package com.buffersolve.news.ui.fragments

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.buffersolve.news.R
import com.buffersolve.news.adapters.NewsAdapter
import com.buffersolve.news.databinding.FragmentBreakingNewsBinding
import com.buffersolve.news.ui.NewsActivity
import com.buffersolve.news.ui.NewsViewModel
import com.buffersolve.news.util.Constants.Companion.DOMAINS
import com.buffersolve.news.util.Resource
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    // Fragment onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fragment onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecycleView()

        // Open Article
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        // Tool Bar
        (activity as NewsActivity).setSupportActionBar(binding.toolBar)
        setToolBar()
        binding.appBar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()))

        // LiveData observe
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Snackbar.make(view, "Error: $message", Snackbar.LENGTH_LONG)
                            .setAnchorView(R.id.bottomNavigationView).show()
                        binding.textInternet.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    // Progress Bar
    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    // RV Setup
    private fun setupRecycleView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    // ToolBar Method
    private fun setToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                menu.findItem(R.id.app_bar_save).isVisible = false
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tool_bar_menu, menu)

                val search = menu.findItem(R.id.app_bar_search)
                val searchView = search.actionView as SearchView
                searchView.isSubmitButtonEnabled = true

                // Search View
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            searchDataBase(DOMAINS, query)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            searchDataBase(DOMAINS, newText)
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // Search View Method
    fun searchDataBase(domains: String, query: String) {
        viewModel.searchNews(domains, query)

        viewModel.searchNews.observe(viewLifecycleOwner) {
            newsAdapter.differ.submitList(it.data?.articles)
        }
    }

    // Fragment onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}