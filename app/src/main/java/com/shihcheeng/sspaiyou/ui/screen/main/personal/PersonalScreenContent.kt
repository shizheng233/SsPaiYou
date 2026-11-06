package com.shihcheeng.sspaiyou.ui.screen.main.personal

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.theme.AppTheme
import com.shihcheeng.sspaiyou.utils.imageUrlFor

@Composable
fun PersonalScreenContent(
    username: String,
    followingCount: Int,
    followerCount: Int,
    beingDays: Int?,
    imageUrl: String,
    onSetting: () -> Unit,
    onFavourite: () -> Unit,
    onMine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileCard(
                username = username,
                imageUrl = imageUrl,
                beingDays = beingDays,
                followingCount = followingCount,
                followerCount = followerCount,
                onMine = onMine
            )
            Text(
                text = stringResource(R.string.personal_shortcuts),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .semantics { heading() }
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalActionCard(
                    icon = R.drawable.baseline_bookmark_border_24,
                    title = stringResource(R.string.fav),
                    description = stringResource(R.string.personal_favourites_description),
                    containerColor = colors.secondaryContainer,
                    contentColor = colors.onSecondaryContainer,
                    onClick = onFavourite
                )
                PersonalActionCard(
                    icon = R.drawable.outline_settings_24,
                    title = stringResource(R.string.setting),
                    description = stringResource(R.string.personal_settings_description),
                    containerColor = colors.surfaceContainerLow,
                    contentColor = colors.onSurface,
                    onClick = onSetting
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    username: String,
    imageUrl: String,
    beingDays: Int?,
    followingCount: Int,
    followerCount: Int,
    onMine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = colors.surfaceContainerLowest,
                    contentColor = colors.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        AsyncImage(
                            model = imageUrlFor(imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                        )
                    }
                }
                FilledTonalButton(
                    onClick = onMine,
                    shapes = ButtonDefaults.shapes(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    modifier = Modifier.weight(1f, fill = false).padding(start = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.personal_view_profile),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.baseline_navigate_next_24),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = username.ifBlank { stringResource(R.string.member_of_sspai) },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
                Text(
                    text = beingDays?.let { stringResource(R.string.personal_joined_days, it) }
                        ?: stringResource(R.string.member_of_sspai),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(color = colors.onPrimaryContainer.copy(alpha = 0.16f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileStatistic(
                    value = followingCount,
                    label = stringResource(R.string.personal_following),
                    modifier = Modifier.weight(1f)
                )
                ProfileStatistic(
                    value = followerCount,
                    label = stringResource(R.string.personal_followers),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProfileStatistic(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PersonalActionCard(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                painter = painterResource(R.drawable.baseline_navigate_next_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(name = "Personal · Light", showBackground = true, widthDp = 360, heightDp = 760)
@Preview(
    name = "Personal · Dark",
    showBackground = true,
    widthDp = 360,
    heightDp = 760,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Personal · Narrow · Large font",
    showBackground = true,
    widthDp = 320,
    heightDp = 760,
    fontScale = 2f
)
@Composable
private fun PersonalScreenContentPreview() {
    AppTheme(dynamicColor = false) {
        Surface {
            PersonalScreenContent(
                username = "把日常过成喜欢的样子",
                followingCount = 128,
                followerCount = 960,
                beingDays = 1024,
                imageUrl = "",
                onSetting = {},
                onFavourite = {},
                onMine = {}
            )
        }
    }
}
