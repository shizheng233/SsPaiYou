package com.shihcheeng.sspaiyou.ui.screen.main.matrix.column

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColumnViewModel @Inject constructor(
    repository: ColumnRepository
) : ViewModel() {

    val data = repository.columns.cachedIn(viewModelScope)

}