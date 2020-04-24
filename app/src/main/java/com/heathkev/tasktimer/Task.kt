package com.heathkev.tasktimer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Task(val name: String, val description: String, val sortOrder: Int) : Parcelable {
    var id: Long = 0
}