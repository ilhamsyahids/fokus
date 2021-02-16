package com.isaiahvonrundstedt.fokus.features.log

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.fokus.R
import com.isaiahvonrundstedt.fokus.components.custom.ItemDecoration
import com.isaiahvonrundstedt.fokus.components.custom.ItemSwipeCallback
import com.isaiahvonrundstedt.fokus.databinding.FragmentLogsBinding
import com.isaiahvonrundstedt.fokus.features.schedule.Schedule
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseAdapter
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseFragment
import com.isaiahvonrundstedt.fokus.features.subject.Subject
import com.isaiahvonrundstedt.fokus.features.subject.SubjectAdapter
import com.isaiahvonrundstedt.fokus.features.subject.SubjectPackage
import com.isaiahvonrundstedt.fokus.features.subject.SubjectViewModel
import com.isaiahvonrundstedt.fokus.features.subject.editor.SubjectEditor
import dagger.hilt.android.AndroidEntryPoint
import me.saket.cascade.CascadePopupMenu
import me.saket.cascade.overrideOverflowMenu

@AndroidEntryPoint
class LogFragment : BaseFragment(), BaseAdapter.ActionListener {
    private var _binding: FragmentLogsBinding? = null
    private var controller: NavController? = null

    private val logAdapter = LogAdapter(this)
    private val viewModel: LogViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = Navigation.findNavController(requireActivity(), R.id.navigationHostFragment)

        with(binding.appBarLayout.toolbar) {
            setTitle(R.string.activity_logs)
            inflateMenu(R.menu.menu_logs)
            overrideOverflowMenu { context, anchor -> CascadePopupMenu(context, anchor) }
            setOnMenuItemClickListener(::onMenuItemClicked)
        }

        setupNavigation(binding.appBarLayout.toolbar, controller)

        with(binding.recyclerView) {
            addItemDecoration(ItemDecoration(context))
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter

            ItemTouchHelper(ItemSwipeCallback(context, logAdapter))
                .attachToRecyclerView(this)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.logs.observe(this) { logAdapter.submitList(it) }
        viewModel.isEmpty.observe(this) { binding.emptyView.isVisible = it }
    }

    override fun <T> onActionPerformed(t: T, action: BaseAdapter.ActionListener.Action,
                                       container: View?) {
        if (t is Log) {
            when (action) {
                BaseAdapter.ActionListener.Action.DELETE -> {
                    viewModel.remove(t)
                    val snackbar = Snackbar.make(binding.recyclerView, R.string.feedback_log_removed,
                        Snackbar.LENGTH_SHORT)
                    snackbar.setAction(R.string.button_undo) {
                        viewModel.insert(t)
                    }
                    snackbar.show()
                }
                BaseAdapter.ActionListener.Action.SELECT -> { }
            }
        }
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_clear_items -> viewModel.removeLogs()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}