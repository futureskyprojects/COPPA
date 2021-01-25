package me.vistark.fastdroid.interfaces

interface IDeletable<T> {
    var onDelete: ((T) -> Unit)?
}