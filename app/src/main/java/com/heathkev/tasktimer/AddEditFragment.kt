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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
    private val viewModel:TaskTimerViewModel by activityViewModels()


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
        // Create new task object with the details to be saved, then
        // call the viewModel's saveTask function to save it.
        // Task is now a data class, so we can compare the new details with the original task
        // and only save if they are different

        val newTask = taskFromUri()
        if(newTask != task){
            Log.d(TAG,"saveTask: saving task, id is ${newTask.id}")
            task = viewModel.saveTask(newTask)
            Log.d(TAG,"saveTask: id is ${task?.id}")
        }
    }

    private fun taskFromUri(): Task{
        val sortOrder = if(addedit_sort_order.text.isNotEmpty()){
            Integer.parseInt(addedit_sort_order.text.toString())
        } else {
            0
        }

        val newTask = Task(addedit_name.text.toString(), addedit_description.text.toString(), sortOrder)
        newTask.id = task?.id ?: 0

        return newTask
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
