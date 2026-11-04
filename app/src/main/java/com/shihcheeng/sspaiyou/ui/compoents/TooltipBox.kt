package com.shihcheeng.sspaiyou.ui.compoents

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithTooltipBox(
    modifierToTooltip: Modifier = Modifier,
    tooltipText: String,
    withComponent: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above,
            spacingBetweenTooltipAndAnchor = 2.dp
        ),
        tooltip = {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = tooltipText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(6.dp)
                )
            }
        },
        state = rememberTooltipState(),
        content = withComponent,
        modifier = modifierToTooltip
    )
}