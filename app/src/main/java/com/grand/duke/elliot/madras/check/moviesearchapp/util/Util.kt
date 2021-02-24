package com.grand.duke.elliot.madras.check.moviesearchapp.util

fun <T> List<T>.difference(list: List<T>) = Difference(
    removed = this.filterNot { list.contains(it) },
    added = list.filterNot { this.contains(it) }
)

data class Difference<T>(
    val removed: List<T>,
    val added: List<T>
)