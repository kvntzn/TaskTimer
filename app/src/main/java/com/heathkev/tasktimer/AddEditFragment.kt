package com.heathkev.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_add_edit.*
import java.lang.Exception
import java.lang.RuntimeException

private const val TAG = "AddEditFragment"

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditFragment : Fragment() {
    private var task: Task? = null
    private var listener: OnSaveClicked? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable(ARG_TASK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        if(savedInstanceState == null) {
            if (task != null) {
                val task = task!!
                Log.d(TAG, "onViewCreated: Task details found, editing task ${task.id}")
                addedit_name.setText(task.name)
                addedit_description.setText(task.description)
                addedit_sort_order.setText(task.sortOrder.toString())
            } else {
                //No task, so we must be adding a new task, and NOT editing and existing one
                Log.d(TAG, "onViewCreated: No arguments, adding new record")
            }
        }
    }

    private fun saveTask(){
        // Update the database if at least one field has changed
        // - There's no need to hit the database unless this happened
        val sortOrder = if (addedit_sort_order.text.isNotEmpty()){
            Integer.parseInt(addedit_sort_order.text.toString())
        }else{
            0
        }

        val values = ContentValues()
        val task = task

        if(task != null){
            Log.d(TAG,"saveTask: updating existing task")
            if(addedit_name.text.toString() != task.name){
                values.put(TasksContract.Columns.TASK_NAME, addedit_name.text.toString())
            }
            if(addedit_description.text.toString() != task.description){
                values.put(TasksContract.Columns.TASK_DESCRIPTION,
                    addedit_description.text.toString())
            }
            if(sortOrder != task.sortOrder){
                values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder)
            }
            if(values.size() != 0){
                Log.d(TAG, "saveTask: Updating task")
                activity?.contentResolver?.update(TasksContract.buildUriFromId(task.id),
                    values, null, null)
            }
        }else{
            Log.d(TAG,"saveTask: adding new task")
            if(addedit_name.text.isNotEmpty()){
                values.put(TasksContract.Columns.TASK_NAME, addedit_name.text.toString())
                if(addedit_description.text.isNotEmpty()){
                    values.put(TasksContract.Columns.TASK_DESCRIPTION,
                        addedit_description.text.toString())
                }
                values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder) //default zero if empty
                activity?.contentResolver?.insert(TasksContract.CONTENT_URI, values)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: starts")
        super.onActivityCreated(savedInstanceState)

        if(listener is AppCompatActivity) {
            val actionBar = (listener as AppCompatActivity)?.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }

        addedit_save.setOnClickListener {
            saveTask()
            listener?.onSaveClicked()
        }
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: starts")
        super.onAttach(context)
        if(context is OnSaveClicked){
            listener = context
        }else{
            throw RuntimeException("${context} must implement OnSaveClicked")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach: starts")
        super.onDetach()
        listener = null
    }

    interface OnSaveClicked{
        fun onSaveClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task The task to be edited, or null to add a new task
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK, task)
                }
            }
    }
}

//fun createFrag(task: Task){
//    val args = Bundle()
//    args.putParcelable(ARG_TASK, task)
//    val fragment = AddEditFragment()
//    fragment.arguments = args
//}
//
//fun createFrag2(task: Task){
//    val fragments = AddEditFragment().apply {
//        arguments = Bundle().apply {
//            putParcelable(ARG_TASK, task)
//        }
//    }
//}
//
//fun simpler(task: Task){
//    val fragment = AddEditFragment.newInstance(task)
//}
