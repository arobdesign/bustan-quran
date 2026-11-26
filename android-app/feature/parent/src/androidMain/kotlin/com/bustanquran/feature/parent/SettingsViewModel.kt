package com.bustanquran.feature.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bustanquran.core.data.preferences.UserPreferencesRepository
import com.bustanquran.core.data.repository.UserDataAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What the parent settings screen needs from storage. */
data class SettingsUiState(
    val wifiOnly: Boolean = true,
    val appLang: String = "en",
    val busy: Boolean = false,
)

/**
 * Backs artboard 14 · Pengaturan orang tua.
 *
 * The screen used to be a mock: the Wi-Fi toggle lived in a `remember`, so it
 * forgot itself on every visit, and the two data buttons were wired to nothing.
 * Everything the parents can change here is stored or performed through the
 * repositories below.
 */
class SettingsViewModel(
    private val admin: UserDataAdmin,
    private val preferences: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.familyPreferences.collect { stored ->
                _uiState.update {
                    it.copy(wifiOnly = stored.wifiOnlyDownloads, appLang = stored.appLang)
                }
            }
        }
    }

    fun setWifiOnly(enabled: Boolean) {
        _uiState.update { it.copy(wifiOnly = enabled) }
        viewModelScope.launch { preferences.updateWifiOnlyDownloads(enabled) }
    }

    /**
     * Changing the language recreates the activity (see `BustanQuranAppRoot`), so
     * the new locale is applied to every resource instead of only being stored.
     */
    fun setLanguage(lang: String) {
        if (lang == _uiState.value.appLang) return
        _uiState.update { it.copy(appLang = lang) }
        viewModelScope.launch { preferences.updateAppLang(lang) }
    }

    /** Builds the export document and hands it to [onJson] (`null` when it failed). */
    fun exportJson(onJson: (String?) -> Unit) {
        if (_uiState.value.busy) return
        _uiState.update { it.copy(busy = true) }
        viewModelScope.launch {
            val json = runCatching { admin.exportJson() }.getOrNull()
            _uiState.update { it.copy(busy = false) }
            onJson(json)
        }
    }

    /** Wipes the device and calls [onDeleted] once the data is really gone. */
    fun deleteAllData(onDeleted: () -> Unit) {
        if (_uiState.value.busy) return
        _uiState.update { it.copy(busy = true) }
        viewModelScope.launch {
            runCatching { admin.deleteAllData() }
            _uiState.update { it.copy(busy = false) }
            onDeleted()
        }
    }
}
