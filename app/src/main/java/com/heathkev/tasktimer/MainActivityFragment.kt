package com.heathkev.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 */
private const val TAG = "MainActivityFragment"
class MainActivityFragment : Fragment(),
    CursorRecyclerViewAdapter.OnTaskClickListener {

    private val viewModel:TaskTimerViewModel by activityViewModels()
    private val mAdapter = CursorRecyclerViewAdapter(null, this)

    interface OnTaskEdit{
        fun onTaskEdit(task: Task)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context !is OnTaskEdit){
            throw RuntimeException("${context.toString()} must implement OnTaskEdit")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe(this, Observer { cursor -> mAdapter.swapCursor(cursor)?.close() })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView: called")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task_list.layoutManager = LinearLayoutManager(context)
        task_list.adapter = mAdapter
    }

    override fun onEditClick(task: Task) {
        (activity as OnTaskEdit?)?.onTaskEdit(task)
    }

    override fun onDeleteClick(task: Task) {
        viewModel.deleteTask(task.id)
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }
}
