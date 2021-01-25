package me.vistark.fastdroid.interfaces

interface ILongClickable<T> {
    var onLongClick: ((T) -> Unit)?
}