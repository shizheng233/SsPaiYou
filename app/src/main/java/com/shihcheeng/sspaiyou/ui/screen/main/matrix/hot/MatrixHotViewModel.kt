package com.shihcheeng.sspaiyou.ui.screen.main.matrix.hot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.MatrixRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MatrixHotViewModel @Inject constructor(
    matrixRepository: MatrixRepository
) : ViewModel() {

    val data = matrixRepository.normalData.cachedIn(viewModelScope)

}