package com.grand.duke.elliot.madras.check.moviesearchapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.grand.duke.elliot.madras.check.moviesearchapp.databinding.FragmentRecentSearchesBottomSheetDialogFragmentBinding
import com.grand.duke.elliot.madras.check.moviesearchapp.util.difference

class RecentSearchesBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentRecentSearchesBottomSheetDialogFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var onChipClickListener: OnChipClickListener? = null

    interface OnChipClickListener {
        fun onChipClick(title: String)
    }

    interface FragmentContainer {
        fun onRequestOnChipClickListener(): OnChipClickListener?
    }

    override fun onAttach(context: Context) {
        if (context is FragmentContainer)
            onChipClickListener = context.onRequestOnChipClickListener()

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentSearchesBottomSheetDialogFragmentBinding.inflate(inflater, container, false)

        viewModel.recentSearches.observe(viewLifecycleOwner, { recentSearches ->
            submitTitles(recentSearches.map { it.title })
        })

        return binding.root
    }

    private fun submitTitles(titles: List<String>) {
        val currentTitles = binding.chipGroup.children.map { it.tag.toString() }.toList()
        val difference = currentTitles.difference(titles)

        for (item in difference.added) {
            val chip = Chip(requireContext()).apply { this.tag = item }
            chip.text = item
            chip.setOnClickListener {
                onChipClickListener?.onChipClick(item)
                dismiss()
            }

            binding.chipGroup.addView(chip)
        }

        for (item in difference.removed) {
            val chip = binding.chipGroup.findViewWithTag<Chip>(item)
            binding.chipGroup.removeView(chip)
        }
    }
}