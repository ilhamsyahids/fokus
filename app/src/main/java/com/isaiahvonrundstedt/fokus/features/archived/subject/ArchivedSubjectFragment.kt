package com.isaiahvonrundstedt.fokus.features.archived.subject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.isaiahvonrundstedt.fokus.R
import com.isaiahvonrundstedt.fokus.components.custom.ItemDecoration
import com.isaiahvonrundstedt.fokus.databinding.FragmentArchivedSubjectBinding
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseAdapter
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseFragment
import com.isaiahvonrundstedt.fokus.features.subject.SubjectPackage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedSubjectFragment: BaseFragment(), BaseAdapter.SelectListener {
    private var _binding: FragmentArchivedSubjectBinding? = null

    private val archivedSubjectAdapter = ArchivedSubjectAdapter(this)
    private val viewModel: ArchivedSubjectViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentArchivedSubjectBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.recyclerView) {
            addItemDecoration(ItemDecoration(context))
            layoutManager = LinearLayoutManager(context)
            adapter = archivedSubjectAdapter
        }
    }

    override fun <T> onItemSelected(t: T) {
        if (t is SubjectPackage) {
            MaterialDialog(requireContext()).show {
                lifecycleOwner(viewLifecycleOwner)
                title(R.string.dialog_confirm_unarchive_title)
                message(R.string.dialog_confirm_unarchive_summary)
                positiveButton {
                    viewModel.removeFromArchive(t)
                }
                negativeButton(R.string.button_cancel)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.items.observe(viewLifecycleOwner) {
            /**
             * We'll have to load the animation programmatically
             * because reasons.
             * Thanks, Google.
             */
            val animation = AnimationUtils.loadLayoutAnimation(context,
                R.anim.layout_anim_from_bottom)
            binding.recyclerView.layoutAnimation = animation

            archivedSubjectAdapter.submitList(it)
        }
        viewModel.isEmpty.observe(viewLifecycleOwner) {
            binding.emptyView.isVisible = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}