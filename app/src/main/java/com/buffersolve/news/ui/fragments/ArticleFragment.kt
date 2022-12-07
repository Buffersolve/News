package com.buffersolve.news.ui.fragments

import android.graphics.Color
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    var checkCount: Long = 0

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

        binding.tvHead.text = article.title
        binding.tvUrl.text = article.url
        binding.tvDate.text = article.publishedAt
        binding.progressBar.visibility = View.INVISIBLE

        // Init Fun Parse HTML
        viewModel.parseHTML(article)

        // Img
        Glide.with(this).load(article.urlToImage).into(binding.imageView)

        val animSlideDown = AnimationUtils.loadAnimation(activity, R.anim.slide_down)
        val animMoveToLeft = AnimationUtils.loadAnimation(activity, R.anim.move_to_left)
        val animMoveToLeftLong = AnimationUtils.loadAnimation(activity, R.anim.move_to_left_long)
        val animSlideDownMoveLeft = AnimationUtils.loadAnimation(activity, R.anim.slide_down_move_left)
        val animMoveToUp = AnimationUtils.loadAnimation(activity, R.anim.move_to_up)
        binding.tvHead.startAnimation(animMoveToLeft)
        binding.tvText.startAnimation(animSlideDownMoveLeft)
        binding.tvUrl.startAnimation(animMoveToUp)
        binding.tvDate.startAnimation(animMoveToUp)

        // Live Data Observer for parsed text
        viewModel.parsedText.observe(viewLifecycleOwner, Observer {
            binding.tvText.text = it
        })

        // Already exist LiveData Listener
        viewModel.isArtAlreadySaved(article.url).observe(viewLifecycleOwner, Observer {
            checkCount = it
        })

        Log.d("URL11", article.url)

        // Justify Text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.tvText.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        // New Tool Bar Api
        (activity as NewsActivity).setSupportActionBar(binding.toolBar)
        setToolBar()
        binding.appBar.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()))

//        findNavController().popBackStack()

    }

    private fun setToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                menu.findItem(R.id.app_bar_search).isVisible = false
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tool_bar_menu, menu)
                // Color Icon
                menu.findItem(R.id.app_bar_save).icon.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Color.YELLOW, BlendModeCompat.SRC_ATOP)

                // Change Icon
                if (checkCount > 0) {
                    menu.findItem(R.id.app_bar_save).icon =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_full)
//                    // Color Icon
                    menu.findItem(R.id.app_bar_save).icon.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.YELLOW, BlendModeCompat.SRC_ATOP)

                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.app_bar_save) {
                    //Check is article already exist
                    if (checkCount > 0) {
                        Toast.makeText(requireContext(), "Already Exist", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveArticle(article)
                        view?.let { Snackbar.make(it, "Article Saved", Snackbar.LENGTH_SHORT).show() }
                        menuItem.icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_full)

                        menuItem.icon.colorFilter =
                            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                Color.YELLOW, BlendModeCompat.SRC_ATOP)
                    }
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

}