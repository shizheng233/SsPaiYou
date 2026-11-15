package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserFlag
import com.shihcheeng.sspaiyou.ui.compoents.WithTooltipBox
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun HeaderOfUserDetail(
    isMine: Boolean,
    imageUrl: String,
    followed: Boolean,
    primary: ColorScheme,
    onFollowedChange: (Boolean) -> Unit,
    followEnabled: Boolean,
    username: String,
    bio: String,
    followingCount: Int,
    beFollowedCount: Int,
    userFlag: List<ThirdUserFlag>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = imageUrlFor(imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                )
                Spacer(Modifier.weight(1f))
                WithTooltipBox(
                    tooltipText = stringResource(R.string.follow_this_or_dis_follow)
                ) {
                    FilledTonalIconToggleButton(
                        checked = followed,
                        onCheckedChange = onFollowedChange,
                        enabled = !isMine && followEnabled,
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (!followed) {
                                    R.drawable.mingcute__user_follow_2_line
                                } else {
                                    R.drawable.mingcute__user_follow_2_fill
                                }
                            ),
                            contentDescription = stringResource(R.string.follow_this_or_dis_follow),
                        )
                    }
                }
            }
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (userFlag.any { it.key == "writer" || it.key == "role_admin" }) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (userFlag.any { it.key == "writer" }) {
                        Surface(
                            shape = CircleShape,
                            color = primary.secondary,
                            contentColor = primary.onSecondary,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.game_icons__pencil_ruler),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(R.string.sspai_writer),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                    if (userFlag.any { it.key == "role_admin" }) {
                        Surface(
                            shape = CircleShape,
                            color = primary.secondaryContainer,
                            contentColor = primary.onSecondaryContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.streamline__pi_symbol_circle_solid),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(R.string.member_of_sspai),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }
            if (bio.isNotBlank()) {
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.followed, followingCount.toString()),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.followed_personal, beFollowedCount.toString()),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
