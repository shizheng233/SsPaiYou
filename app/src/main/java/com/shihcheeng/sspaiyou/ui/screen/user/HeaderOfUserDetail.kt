package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserFlag
import com.shihcheeng.sspaiyou.ui.compoents.WithTooltipBox
import com.shihcheeng.sspaiyou.utils.extra.toDp
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun HeaderOfUserDetail(
    isMine: Boolean,
    imageUrl: String,
    followed: Boolean,
    primary: ColorScheme,
    onFollowedChange: (Boolean) -> Unit,
    username: String,
    bio: String,
    followingCount: Int,
    beFollowedCount: Int,
    userFlag: List<ThirdUserFlag>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrlFor(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
            )
            Spacer(Modifier.weight(1f))
            WithTooltipBox(
                tooltipText = stringResource(R.string.follow_this_or_dis_follow)
            ) {
                FilledTonalIconToggleButton(
                    checked = followed,
                    onCheckedChange = onFollowedChange,
                    enabled = !isMine
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (!followed) {
                                R.drawable.mingcute__user_follow_2_line
                            } else {
                                R.drawable.mingcute__user_follow_2_fill
                            }
                        ),
                        contentDescription = null
                    )
                }
            }
        }
        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (userFlag.any { x -> x.key == "writer" }) {
                Badge(
                    containerColor = primary.secondary,
                    contentColor = primary.onSecondary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.game_icons__pencil_ruler),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(MaterialTheme.typography.labelLarge.fontSize.toDp())
                    )
                    Text(
                        text = stringResource(R.string.sspai_writer),
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
            if (userFlag.any { x -> x.key == "role_admin" }) {
                Badge(
                    containerColor = primary.secondaryContainer,
                    contentColor = primary.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.streamline__pi_symbol_circle_solid
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(
                                with(LocalDensity.current) {
                                    MaterialTheme.typography.labelLarge.fontSize.toDp()
                                }
                            )
                    )
                    Text(
                        text = stringResource(R.string.member_of_sspai),
                        modifier = Modifier.padding(4.dp),
                    )
                }
            }
        }
        Text(
            text = bio,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "${followingCount}关注 \u2022 ${beFollowedCount}关注者",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}