package com.oussamameg.contentreveal


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * A composable that provides a reveal with a clipping effect between two content layouts with an interactive divider and particle effects.
 *
 * @param modifier modifier for the box holding this composable
 * @param touchEnabled Whether the divider can be dragged by touch input (default: true)
 * @param currentProgress Current reveal progress value (default: 0f)
 * @param animateProgressChange Whether to animate progress changes (default: true)
 * @param progressRange Valid range for the reveal progress value (default: 0..100)
 * @param onRevealProgress Callback invoked with current progress value as content is revealed
 * @param onFullyHidden Callback invoked when content is fully hidden with boolean indicating if the content is hidden
 * @param onFullyRevealed Callback invoked when content is fully revealed with boolean indicating if the hidden content has been revealed
 * @param dividerRotationEnabled Whether the divider should slightly rotate during progress change (default: true)
 * @param dividerColor Color of the divider (default: #3990FA)
 * @param showParticles Whether to display particle effects (default: true)
 * @param particlesCount Number of particles to display when enabled (default: 60)
 * @param particlesSpeedMultiplier Controls particle animation speed (default: 0.8f)
 * @param particleColor Color of the particles (default: #9FEC5B)
 * @param clipParticlesWithHiddenContent Whether particles should be clipped to hidden content area (default: true)
 * @param content Scoped content block where visible and hidden content are defined
 *
 * Example usage:
 * ```
 * ContentReveal(
 *     onRevealProgress = { progress ->
 *         // Handle progress updates
 *     }
 * ) {
 *     visibleContent {
 *         // Content shown initially Box() Image() Text()....
 *     }
 *     hiddenContent {
 *         // Content to be revealed Box() Image() Text()....
 *     }
 * }
 * ```
 */
@Composable
fun ContentReveal(
    modifier: Modifier = Modifier,
    touchEnabled: Boolean = true,
    currentProgress: Float = 0f,
    animateProgressChange: Boolean = true,
    progressRange: ClosedRange<Int> = 0..100,
    onRevealProgress: (Int) -> Unit = {},
    onFullyHidden: (Boolean) -> Unit = {},
    onFullyRevealed: (Boolean) -> Unit = {},
    dividerRotationEnabled: Boolean = true,
    dividerColor: Color = Color(0xFF3990FA),
    showParticles: Boolean = true,
    particlesCount: Int = 60,
    particlesSpeedMultiplier: Float = 0.8f,
    particleColor: Color = Color(0xFF9FEC5B),
    clipParticlesWithHiddenContent: Boolean = true,
    content: @Composable ContentRevealScope.() -> Unit
) {
    val contentScope = ContentRevealScope()
    content(contentScope)
    contentScope.validate()
    var isTouching by remember { mutableStateOf(false) }
    val xOffSetPercentageAnimation = remember {
        Animatable(
            currentProgress.coerceIn(
                progressRange.start.toFloat(),
                progressRange.endInclusive.toFloat(),
            )
        )
    }

    val scope = rememberCoroutineScope()
    var parentOffSet = Offset(0f, 0f)
    var parentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val revealDividerWidth = 12.dp
    var visibleTextSize = IntSize(0, 0)
    var hiddenTextSize = IntSize(0, 0)
    var visibleTextOffset = Offset(0f, 0f)
    var hiddenTextOffset = Offset(0f, 0f)
    var lastProgressUpdateTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val particleState = remember { ParticleState() }


    fun updateRevealState(offsetPercentage: Float) {
        val progress = offsetPercentage.toInt()
        onRevealProgress(progress)
        onFullyHidden(progress == 0)
        onFullyRevealed(progress == 100)
    }

    var parentBoxModifier = modifier
        .drawWithContent {
            drawContent()
            val rotation =
                if (dividerRotationEnabled) (xOffSetPercentageAnimation.value - 50) * 0.1f else 0f
            rotate(degrees = rotation) {
                val revealDividerHeight = parentSize.height * 1.5f
                drawLine(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            dividerColor,
                            Color.Transparent
                        )
                    ),
                    start = Offset((xOffSetPercentageAnimation.value * size.width) / 100, 0f),
                    end = Offset(
                        (xOffSetPercentageAnimation.value * size.width) / 100,
                        revealDividerHeight
                    ),
                    strokeWidth = revealDividerWidth.value
                )
            }

        }
        .onGloballyPositioned {
            if (parentSize.width != it.size.width || parentSize.height != it.size.height)
                parentSize = it.size
            parentOffSet = it.positionOnScreen()
        }
    if (touchEnabled) {
        parentBoxModifier = parentBoxModifier.pointerInput(Unit) {
            awaitEachGesture {
                var actionUpCoordinates = Offset.Zero
                // ACTION_DOWN here
                val down = awaitFirstDown()
                down.consume()
                isTouching = true
                scope.launch {
                    val relativeX = down.position.x
                    val offsetPercentage = ((relativeX / parentSize.width) * 100).coerceIn(
                        progressRange.start.toFloat(),
                        progressRange.endInclusive.toFloat(),
                    )
                    xOffSetPercentageAnimation.animateTo(
                        offsetPercentage,
                        animationSpec = tween(
                            durationMillis = 600
                        )
                    )
                    updateRevealState(offsetPercentage)
                }

                do {
                    val event: PointerEvent = awaitPointerEvent()
                    // ACTION_MOVE loop
                    event.changes.forEach { change: PointerInputChange ->
                        change.consume()
                        val relativeX = change.position.x
                        val offsetPercentage =
                            ((relativeX / parentSize.width) * 100).coerceIn(
                                progressRange.start.toFloat(),
                                progressRange.endInclusive.toFloat(),
                            )
                        scope.launch {
                            xOffSetPercentageAnimation.snapTo(offsetPercentage)
                        }
                        updateRevealState(offsetPercentage)
                        actionUpCoordinates = change.position
                    }
                } while (event.changes.any { it.pressed })
                //ACTION_UP
                isTouching = false
                scope.launch {
                    val offsetPercentage =
                        ((actionUpCoordinates.x / parentSize.width) * 100).coerceIn(
                            progressRange.start.toFloat(),
                            progressRange.endInclusive.toFloat(),
                        )
                    xOffSetPercentageAnimation.animateTo(
                        offsetPercentage,
                        animationSpec = tween(
                            durationMillis = 600
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(
        currentProgress.coerceIn(
            progressRange.start.toFloat(),
            progressRange.endInclusive.toFloat(),
        )
    ) {
        scope.launch {
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - lastProgressUpdateTime
            lastProgressUpdateTime = currentTime
            val timeThreshold = 100L
            val progressPercentage = currentProgress.coerceIn(
                progressRange.start.toFloat(),
                progressRange.endInclusive.toFloat(),
            )
            val immediate = timeDifference < timeThreshold
            if (immediate || !animateProgressChange) {
                xOffSetPercentageAnimation.snapTo(progressPercentage)
            } else {
                xOffSetPercentageAnimation.animateTo(
                    progressPercentage,
                    animationSpec = tween(durationMillis = 850)
                )
            }
            updateRevealState(progressPercentage)
        }
    }
    Box(
        modifier = parentBoxModifier, contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()
                val offsetDiff =
                    if (hiddenTextSize.width > visibleTextSize.width) (hiddenTextOffset.x - visibleTextOffset.x) else 0f

                val widthPlusOffset =
                    if (hiddenTextSize.width > visibleTextSize.width) size.width + offsetDiff else size.width

                val relativeXWithOffset =
                    ((xOffSetPercentageAnimation.value * parentSize.width) / 100) + offsetDiff
                val offsetDiffPercentage = (relativeXWithOffset / widthPlusOffset) * 100
                drawRect(
                    color = Color.Red,
                    size = Size(
                        ((widthPlusOffset) - (((100 - (offsetDiffPercentage)) * (widthPlusOffset)) / 100)),
                        size.height
                    ),
                    blendMode = BlendMode.Clear
                )
            }) {
            //Visible Content
            Box(modifier = Modifier
                .onGloballyPositioned {
                    visibleTextOffset = it.positionOnScreen()
                    visibleTextSize = it.size
                }) {
                contentScope.visibleContent?.invoke() ?: Text(
                    text = "Visible",
                    color = Color(0xFF3990FA),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (clipParticlesWithHiddenContent)
                if (showParticles && parentSize != IntSize.Zero)
                    Particles(
                        particleState = particleState,
                        size = parentSize,
                        particlesCount = particlesCount,
                        particleColor = particleColor,
                        speedMultiplier = particlesSpeedMultiplier
                    )
        }

        if (!clipParticlesWithHiddenContent) {
            val density = LocalDensity.current
            if (showParticles && parentSize != IntSize.Zero) {
                Box(modifier = Modifier.size(
                    with(density) { DpSize(parentSize.width.toDp(), parentSize.height.toDp()) }
                )
                ) {
                    Particles(
                        particleState = particleState,
                        size = parentSize,
                        particlesCount = particlesCount,
                        particleColor = particleColor,
                        speedMultiplier = particlesSpeedMultiplier
                    )
                }
            }
        }

        //Hidden Content
        Box(modifier = Modifier
            .onGloballyPositioned {
                hiddenTextOffset = it.positionOnScreen()
                hiddenTextSize = it.size
            }
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                val offsetDiff =
                    if (hiddenTextSize.width < visibleTextSize.width) (hiddenTextOffset.x - visibleTextOffset.x) else 0f

                val widthPlusOffset =
                    if (hiddenTextSize.width < visibleTextSize.width) size.width + offsetDiff else size.width

                val relativeXWithOffset =
                    ((xOffSetPercentageAnimation.value * parentSize.width) / 100) - offsetDiff

                val offsetDiffPercentage = (relativeXWithOffset / widthPlusOffset) * 100

                drawRect(
                    color = Color.Yellow,
                    topLeft = Offset((offsetDiffPercentage * widthPlusOffset) / 100, 0f),
                    size = Size(
                        widthPlusOffset - ((offsetDiffPercentage * widthPlusOffset) / 100),
                        size.height
                    ),
                    blendMode = BlendMode.Clear
                )
            }) {
            contentScope.hiddenContent?.invoke() ?: Text(
                text = "Hidden",
                color = Color(0xFFFA39E7),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}
