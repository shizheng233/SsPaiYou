package com.shihcheeng.sspaiyou.ui.screen.main.matrix.bullet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.MatrixRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BulletViewModel @Inject constructor(repository: MatrixRepository) : ViewModel() {
    val data = repository.bulletData.cachedIn(viewModelScope)
}
