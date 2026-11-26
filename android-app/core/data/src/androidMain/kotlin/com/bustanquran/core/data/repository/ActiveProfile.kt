package com.bustanquran.core.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The child profile chosen on the "Who is learning today?" screen.
 *
 * The selection used to travel through `NavBackStackEntry.savedStateHandle`,
 * which only the previous entry could read — so any screen deeper in the graph
 * (games, hafalan, recordings) had no idea who was learning. Keeping it in a
 * single Koin-scoped holder makes the active child readable from anywhere.
 */
class ActiveProfile {

    private val _profileId = MutableStateFlow<String?>(null)

    /** Null while no child has been picked (first run, or after "Hapus Semua Data"). */
    val profileId: StateFlow<String?> = _profileId.asStateFlow()

    fun select(profileId: String?) {
        _profileId.value = profileId
    }

    fun current(): String? = _profileId.value

    fun clear() = select(null)
}
