# Compose Content Reveal

A sophisticated reveal effect library for Jetpack Compose that creates an interactive sliding reveal transition between two content layouts with particle effects. Perfect for before/after comparisons, progressive content revelation, and engaging user interactions in Android applications.


[![Maven Central](https://img.shields.io/maven-central/v/io.github.meglali20/content-reveal.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.meglali20%22%20AND%20a:%22content-reveal%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)


## Features

- Interactive touch-enabled reveal with smooth animations
- Progress tracking and callbacks
- Support for various Composable content
- Controllable reveal progress

## Installation

Add the dependency to your module's `build.gradle` file:

```gradle
dependencies {
    implementation("io.github.meglali20:content-reveal:1.0.0")
}
```

## Usage

Here's a basic example of how to use ContentReveal:

```kotlin
ContentReveal(
    modifier = Modifier.fillMaxWidth().height(300.dp),
    onRevealProgress = { progress ->
        // Handle progress updates
    },
    showParticles = true,
    particleColor = Color(0xFF9FEC5B),
    dividerColor = Color(0xFF3990FA)
) {
    visibleContent {
        Image(
            painter = painterResource(id = R.drawable.before_image),
            contentDescription = "Before",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
    hiddenContent {
        Image(
            painter = painterResource(id = R.drawable.after_image),
            contentDescription = "After",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
```

### Advanced Usage

```kotlin
ContentReveal(
    modifier = Modifier.fillMaxWidth().height(400.dp),
    touchEnabled = true,
    currentProgress = progress,
    animateProgressChange = true,
    progressRange = 0..100,
    onRevealProgress = { currentProgress ->
        // Track progress
    },
    onFullyRevealed = { isRevealed ->
        // Handle fully revealed state
    },
    dividerRotationEnabled = true,
    showParticles = true,
    particlesCount = 60,
    particlesSpeedMultiplier = 0.8f,
    particleColor = Color(0xFF9FEC5B),
    clipParticlesWithHiddenContent = true
) {
    visibleContent {
        // Your visible content
    }
    hiddenContent {
        // Your hidden content
    }
}
```

## Customization

The ContentReveal composable offers extensive customization options:

| Parameter | Description | Default |
|-----------|-------------|---------|
| touchEnabled | Enable/disable touch interaction | true |
| currentProgress | Control reveal progress programmatically | 0f |
| animateProgressChange | Animate progress changes | true |
| dividerRotationEnabled | Enable divider rotation effect | true |
| showParticles | Show/hide particle effects | true |
| particlesCount | Number of particles | 60 |
| particleColor | Color of particles | #9FEC5B |

## License

```
Copyright 2024 [Oussama Meglali]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.