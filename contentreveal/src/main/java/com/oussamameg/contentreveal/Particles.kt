package com.oussamameg.contentreveal

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * A composable that renders an animated particle system with twinkling effects.
 *
 * @param particleState State management class that maintains particle positions and properties
 * @param size The size of the container where particles will be rendered
 * @param particlesCount The number of particles to display (default: 60)
 * @param speedMultiplier Controls the speed of both movement and twinkling animations (default: 0.8f)
 *                       Higher values make animations faster, lower values make them slower
 * @param particleColor The color of all particles in the system (default: Light green #9FEC5B)
 *
 * Example usage:
 * ```
 * val particleState = remember { ParticleState() }
 * Particles(
 *     particleState = particleState,
 *     size = IntSize(width, height),
 *     particlesCount = 60,
 *     speedMultiplier = 0.8f,
 *     particleColor = Color(0xFF9FEC5B)
 * )
 * ```
 */
@Composable
fun Particles(
    particleState: ParticleState,
    size: IntSize,
    particlesCount: Int = 60,
    speedMultiplier: Float = 0.8f,
    particleColor: Color = Color(0xFF9FEC5B)
) {
    if (size.width == 0 || size.height == 0) return

    var updateTrigger by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        particleState.initializeIfNeeded(size, particlesCount)
    }

    LaunchedEffect(size, speedMultiplier) {
        while (true) {
            particleState.particles.forEach { particle ->

                particle.moveProgress += (0.016f * speedMultiplier) / particle.movementDuration
                if (particle.moveProgress >= 1f) {
                    particle.initialPosition = particle.targetPosition
                    // Generate new target because particle reached target position
                    particle.targetPosition = generatePosition(size)
                    particle.moveProgress = 0f
                    particle.movementDuration = Random.nextFloat() * 10f + 20f
                }

                // lerp interpolation between initial and target position
                particle.currentPosition = Offset(
                    x = lerp(
                        particle.initialPosition.x,
                        particle.targetPosition.x,
                        particle.moveProgress
                    ),
                    y = lerp(
                        particle.initialPosition.y,
                        particle.targetPosition.y,
                        particle.moveProgress
                    )
                )

                particle.twinkleProgress += 0.016f * speedMultiplier
                if (particle.twinkleProgress >= PI.toFloat() * 2) {
                    particle.twinkleProgress = 0f
                    particle.targetOpacity = Random.nextFloat() * 0.7f + 0.3f
                    particle.targetScale = Random.nextFloat() * 0.4f + 0.8f
                }

                val twinkleT = (sin(particle.twinkleProgress) + 1) / 2

                particle.opacity = lerp(
                    particle.currentOpacity,
                    particle.targetOpacity,
                    twinkleT
                )
                particle.scale = lerp(
                    particle.currentScale,
                    particle.targetScale,
                    twinkleT
                )

                if (twinkleT >= 0.99f) {
                    particle.currentOpacity = particle.targetOpacity
                    particle.currentScale = particle.targetScale
                }
            }

            updateTrigger = System.currentTimeMillis()
            delay(16)
        }
    }

    Box(
        modifier = Modifier.drawWithContent {
            updateTrigger
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                nativeCanvas.save()
                particleState.particles.forEach { particle ->
                    val paint = Paint().apply {
                        shader = RadialGradientShader(
                            center = particle.currentPosition,
                            radius = 10f * particle.scale,
                            colors = listOf(
                                particleColor,
                                particleColor.copy(alpha = 0.5f),
                                Color.Transparent,
                            )
                        )
                        alpha = (particle.opacity * 255).toInt()
                        maskFilter = BlurMaskFilter(
                            1.5f,
                            BlurMaskFilter.Blur.NORMAL
                        )
                    }

                    nativeCanvas.drawCircle(
                        particle.currentPosition.x,
                        particle.currentPosition.y,
                        5f * particle.scale,
                        paint
                    )
                }

                nativeCanvas.restore()
            }
        }
    )
}

fun randomMove(): Float = (Random.nextFloat() * 4f - 2f)

fun randomOpacity(): Float = Random.nextFloat()

fun generatePosition(parentSize: IntSize): Offset {
    val x = Random.nextFloat() * parentSize.width
    val y = Random.nextFloat() * parentSize.height
    val moveX = randomMove()
    val moveY = randomMove()
    return Offset(x + moveX, y + moveY)
}


private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}
