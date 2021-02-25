package com.grand.duke.elliot.madras.check.moviesearchapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.madras.check.moviesearchapp.R
import com.grand.duke.elliot.madras.check.moviesearchapp.adapter.MovieItemAdapter
import com.grand.duke.elliot.madras.check.moviesearchapp.databinding.ActivityMainBinding
import com.grand.duke.elliot.madras.check.moviesearchapp.network.MovieItem
import com.grand.duke.elliot.madras.check.moviesearchapp.network.NaverMovieApi
import timber.log.Timber


class MainActivity : AppCompatActivity(),
    MovieItemAdapter.OnItemClickListener, RecentSearchesBottomSheetDialogFragment.FragmentContainer {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    private val adapter = MovieItemAdapter()
    private val uiController = UiController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)

        uiController.init()
        adapter.setOnItemClickListener(this)
        initLiveData()

        setContentView(binding.root)
    }

    private fun initLiveData() {
        viewModel.movies.observe(this, {
            if (viewModel.total == 0)
                showToast(getString(R.string.no_search_result_found_message))
            else
                adapter.submitList(it)
        })

        viewModel.recentSearches.observe(this, {
            viewModel.setRowCount(it.count())
        })
    }

    private inner class UiController {
        fun init() {
            initRecyclerView()

            binding.textInputEditText.setText(viewModel.currentTitle)

            binding.buttonSearch.setOnClickListener {
                val searchWord = binding.textInputEditText.text.toString()

                if (searchWord.isBlank()) {
                    binding.textInputLayout.isErrorEnabled = true
                    binding.textInputLayout.error = getString(R.string.empty_input_error_message)
                    return@setOnClickListener
                }

                viewModel.searchMovies(searchWord) {
                    showToast(getString(R.string.check_internet_connection_message))
                }
            }

            binding.buttonResentSearches.setOnClickListener {
                RecentSearchesBottomSheetDialogFragment().apply {
                    show(supportFragmentManager, tag)
                }
            }

            binding.textInputEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.buttonSearch.performClick()
                    return@setOnEditorActionListener false
                }
                false
            }

            val textWatcher: TextWatcher = object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotBlank() && binding.textInputLayout.isErrorEnabled) {
                        binding.textInputLayout.isErrorEnabled = false
                        binding.textInputLayout.error = BLANK
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable) {}
            }

            binding.textInputEditText.addTextChangedListener(textWatcher)
        }

        private fun initRecyclerView() {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = this@MainActivity.adapter
            }

            binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = (recyclerView.layoutManager as? LinearLayoutManager)
                            ?: return
                    val itemCount = layoutManager.itemCount

                    if (itemCount < 1)
                        return

                    val lastCompletelyVisibleItemPosition =
                            layoutManager.findLastCompletelyVisibleItemPosition()

                    Timber.d("itemCount: ${itemCount}.")
                    Timber.d("lastCompletelyVisibleItemPosition: ${lastCompletelyVisibleItemPosition}.")
                    Timber.d("NaverMovieApi.start: ${NaverMovieApi.start}.")
                    Timber.d("viewModel.total: ${viewModel.total}.")

                    if (viewModel.total > itemCount) {
                        if (lastCompletelyVisibleItemPosition >= itemCount.dec())
                            viewModel.searchAdditionalMovies {
                                showToast(getString(R.string.check_internet_connection_message))
                            }
                    }
                }
            })
        }
    }

    private fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Toast.makeText(this, text, duration).show()
        }, 0)
    }

    /** MovieItemAdapter.OnItemClickListener. */
    override fun onItemClick(item: MovieItem) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(item.link)
        startActivity(intent)
    }

    /** RecentSearchesBottomSheetDialogFragment.FragmentContainer. */
    override fun onRequestOnChipClickListener() = object: RecentSearchesBottomSheetDialogFragment.OnChipClickListener {
        override fun onChipClick(title: String) {
            binding.textInputEditText.setText(title)
            binding.buttonSearch.performClick()
        }
    }
}