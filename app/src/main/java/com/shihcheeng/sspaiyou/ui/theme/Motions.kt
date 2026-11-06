package com.shihcheeng.sspaiyou.ui.theme

import androidx.activity.BackEventCompat
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlin.math.absoluteValue

/** Material 3 duration/easing values; distances supplied by callers are in pixels. */
object MaterialMotions {
    private const val Medium2 = 300
    private const val Short3 = 150
    private const val Short4 = 200
    private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    private val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Shared axis Z for parent/child navigation, with a 30% fade-through threshold. */
    fun navigationEnter(forward: Boolean): EnterTransition =
        scaleIn(
            animationSpec = tween(Medium2, easing = Standard),
            initialScale = if (forward) 0.92f else 1.04f
        ) + fadeIn(
            animationSpec = tween(210, delayMillis = 90, easing = EmphasizedDecelerate)
        )

    fun navigationExit(forward: Boolean): ExitTransition =
        scaleOut(
            animationSpec = tween(Medium2, easing = Standard),
            targetScale = if (forward) 1.04f else 0.92f
        ) + fadeOut(
            animationSpec = tween(90, easing = EmphasizedAccelerate)
        )

    /** Shared X axis for related destinations. Pass a negative distance in RTL layouts. */
    fun screenEnter(forward: Boolean, slideDistance: Int): EnterTransition =
        slideInHorizontally(
            animationSpec = tween(Medium2, easing = EmphasizedDecelerate),
            initialOffsetX = { if (forward) slideDistance else -slideDistance }
        ) + fadeIn(tween(Short4, easing = EmphasizedDecelerate))

    fun screenExit(forward: Boolean, slideDistance: Int): ExitTransition =
        slideOutHorizontally(
            animationSpec = tween(Medium2, easing = EmphasizedAccelerate),
            targetOffsetX = { if (forward) -slideDistance else slideDistance }
        ) + fadeOut(tween(Short3, easing = EmphasizedAccelerate))

    /** Shared Z axis for a destination that opens above the current content. */
    fun sharedAxisZ(forward: Boolean = true): ContentTransform = ContentTransform(
        targetContentEnter = fadeIn(tween(Short4, easing = EmphasizedDecelerate)) +
            scaleIn(
                animationSpec = tween(Medium2, easing = EmphasizedDecelerate),
                initialScale = if (forward) 0.92f else 1.04f
            ),
        initialContentExit = fadeOut(tween(Short3, easing = EmphasizedAccelerate)) +
            scaleOut(
                animationSpec = tween(Medium2, easing = EmphasizedAccelerate),
                targetScale = if (forward) 1.04f else 0.92f
            ),
        sizeTransform = null
    )

    /** Fade through for unrelated top-level destinations, such as bottom navigation. */
    fun fadeThrough(): ContentTransform = ContentTransform(
        targetContentEnter = fadeIn(
            tween(Short3, delayMillis = Short3, easing = EmphasizedDecelerate)
        ) + scaleIn(
            tween(Short3, delayMillis = Short3, easing = EmphasizedDecelerate),
            initialScale = 0.96f
        ),
        initialContentExit = fadeOut(tween(Short3, easing = EmphasizedAccelerate)),
        sizeTransform = null
    )

    /** Ordered tabs in AnimatedContent. HorizontalPager already supplies its own translation. */
    fun sharedAxisX(forward: Boolean, slideDistance: Int): ContentTransform = ContentTransform(
        targetContentEnter = screenEnter(forward, slideDistance),
        initialContentExit = screenExit(forward, slideDistance),
        sizeTransform = null
    )

    /** Linear, equal-length specs let NavHost seek every property directly with the gesture. */
    fun predictivePopEnter(): EnterTransition =
        scaleIn(tween(Medium2, easing = LinearEasing), initialScale = 0.96f) +
            fadeIn(tween(Medium2, easing = LinearEasing))

    /** Use a small physical-edge translation; slideDistance is supplied in pixels, not RTL-adjusted. */
    fun predictivePopExit(swipeEdge: Int, slideDistance: Int): ExitTransition {
        val fromLeft = swipeEdge == BackEventCompat.EDGE_LEFT
        return scaleOut(
            animationSpec = keyframes {
                durationMillis = Medium2
                1f at 0 using LinearEasing
                0.92f at 210 using LinearEasing
                0.9f at Medium2
            },
            targetScale = 0.9f,
            transformOrigin = TransformOrigin.Center
        ) + slideOutHorizontally(
            animationSpec = keyframes {
                durationMillis = Medium2
                IntOffset.Zero at 0 using LinearEasing
                IntOffset(if (fromLeft) slideDistance.absoluteValue else -slideDistance.absoluteValue, 0) at
                    210 using Standard
            },
            targetOffsetX = { width -> if (fromLeft) width else -width }
        ) + fadeOut(keyframes {
            durationMillis = Medium2
            1f at 0 using LinearEasing
            1f at 210 using LinearEasing
            0f at Medium2
        })
    }
}

/** The destination surface shares NavHost's seekable timeline, including cancellation. */
@Composable
private fun AnimatedContentScope.NavigationSurface(content: @Composable () -> Unit) {
    val departure = transition.animateFloat(
        transitionSpec = { tween(300, easing = LinearEasing) },
        label = "navigation_surface_departure"
    ) { state -> if (state == EnterExitState.PostExit) 1f else 0f }
    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                val progress = departure.value.coerceIn(0f, 1f)
                val lift = (progress / 0.25f).coerceIn(0f, 1f)
                shape = RoundedCornerShape(28.dp * lift)
                clip = lift > 0f
                shadowElevation = 20.dp.toPx() * lift * (1f - progress)
            }
            .background(MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

fun NavGraphBuilder.motionComposable(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(route) { entry ->
        NavigationSurface { content(entry) }
    }
}

enum class PagerMotion { Fade, FadeAndScale }

/** Apply to each page's root, not the pager. Progress is read only in the draw phase. */
fun Modifier.materialPagerMotion(
    state: PagerState,
    page: Int,
    motion: PagerMotion = PagerMotion.Fade
): Modifier = graphicsLayer {
    val distance = ((state.currentPage - page) + state.currentPageOffsetFraction)
        .absoluteValue.coerceIn(0f, 1f)
    alpha = 1f - 0.2f * distance
    val scale = if (motion == PagerMotion.FadeAndScale) 1f - 0.04f * distance else 1f
    scaleX = scale
    scaleY = scale
}
