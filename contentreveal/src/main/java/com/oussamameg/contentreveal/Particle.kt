package com.oussamameg.contentreveal

import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

/**
 * Represents an animated particle with movement and twinkling visual effects.
 *
 * @property currentPosition Current x,y coordinates of the particle in the animation space
 * @property targetPosition Destination point that the particle is moving towards
 * @property initialPosition Starting point of the particle's current movement path
 * @property opacity Current rendered opacity value, calculated from twinkling animation
 * @property scale Current rendered scale factor, calculated from twinkling animation
 * @property moveProgress Progress (0.0 to 1.0) of particle's movement from initial to target position
 * @property twinkleProgress Progress of the twinkling animation cycle (0.0 to 2π), initialized with random phase
 * @property movementDuration Time in seconds for particle to complete one movement path (20-30 seconds)
 * @property currentOpacity Base opacity value for twinkling interpolation (0.3-1.0)
 * @property targetOpacity Target opacity value that the twinkling animation moves towards (0.3-1.0)
 * @property currentScale Base scale value for twinkling interpolation (0.8-1.2)
 * @property targetScale Target scale value that the twinkling animation moves towards (0.8-1.2)
 */
data class Particle(
    var currentPosition: Offset,
    var targetPosition: Offset,
    var initialPosition: Offset,
    var opacity: Float = 1f,
    var scale: Float = 1f,
    var moveProgress: Float = 0f,
    var twinkleProgress: Float = Random.nextFloat(),
    var movementDuration: Float = Random.nextFloat() * 10f + 20f,
    var currentOpacity: Float = Random.nextFloat() * 0.7f + 0.3f,
    var targetOpacity: Float = Random.nextFloat() * 0.7f + 0.3f,
    var currentScale: Float = Random.nextFloat() * 0.4f + 0.8f,
    var targetScale: Float = Random.nextFloat() * 0.4f + 0.8f
)