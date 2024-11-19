package com.shihcheeng.sspaiyou.ui.screen.main.matrix.column

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnItem(
    modifier: Modifier = Modifier,
    title: String,
    followCount: Int,
    imageUrl: String,
    checkValue: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.then(modifier)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 16.dp)
                .padding(end = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = imageUrlFor(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .width(70.dp)
                        .aspectRatio(1f / 1f)
                        .clip(MaterialTheme.shapes.medium),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                    error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.weight(1f))
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_list),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    FilledTonalIconToggleButton(
                        onCheckedChange = onCheckedChange,
                        checked = checkValue
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.eos_icons_activate_subscriptions_outlined),
                            contentDescription = stringResource(R.string.add_to_list)
                        )
                    }
                }
            }
            Text(
                text = title,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(R.string.followed_personal, followCount),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

