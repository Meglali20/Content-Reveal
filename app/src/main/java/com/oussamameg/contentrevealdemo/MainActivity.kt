package com.oussamameg.contentrevealdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oussamameg.contentreveal.ContentReveal
import com.oussamameg.contentrevealdemodemo.ui.theme.ContentRevealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ContentRevealTheme {
                var progress by remember { mutableFloatStateOf(0f) }
                var imageContentRevealed by remember { mutableStateOf(false) }
                var imageContentHidden  by remember { mutableStateOf(true) }
                var textContentRevealed by remember { mutableStateOf(false) }
                var textContentHidden  by remember { mutableStateOf(true) }
                Scaffold { _ ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(15.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Slider(
                            modifier = Modifier.padding(20.dp),
                            value = progress,
                            valueRange = 0f..100f,
                            onValueChange = {
                                progress = it
                            },
                            enabled = true
                        )
                        Text(text="Progress ${progress.toInt()}")


                        ContentReveal(
                            clipParticlesWithHiddenContent = true,
                            currentProgress = progress,
                            progressRange = 0..100,
                            touchEnabled = true,
                            particlesCount = 120,
                            onFullyRevealed = {
                                imageContentRevealed = it
                            },
                            onFullyHidden = {
                                imageContentHidden = it
                            },
                            particleColor = Color(0xFFEFD7CA),
                            particlesSpeedMultiplier = 2.5f,
                            dividerColor = Color(0xFF2D251C),
                            dividerRotationEnabled = false
                        ) {
                            val modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .background(Color(0xFF927C6D), shape = RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                            visibleContent {
                                Box(modifier = modifier)
                            }

                            hiddenContent {
                                Image(
                                    modifier = modifier,
                                    painter = painterResource(R.drawable.cat),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Text(text="Image revealed ? $imageContentRevealed  |  hidden ? $imageContentHidden")

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            ContentReveal(
                                clipParticlesWithHiddenContent = true,
                                currentProgress = progress,
                                progressRange = 0..100,
                                touchEnabled = true,
                                onFullyRevealed = {
                                    textContentRevealed = it
                                },
                                onFullyHidden = {
                                    textContentHidden = it
                                },
                                particlesCount = 80,
                                particleColor = Color(0xFF4280EA),
                                particlesSpeedMultiplier = 1.2f
                            ) {
                                visibleContent {
                                    Text(
                                        modifier = Modifier.padding(5.dp),
                                        text = "Jetpack Compose",
                                        color = Color(0xFF5FD580),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                hiddenContent {
                                    Text(
                                        modifier = Modifier.padding(5.dp),
                                        text = "is AWESOME",
                                        color = Color(0xFF4280EA),
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(text="Text revealed ? $textContentRevealed  |  hidden ? $textContentHidden")

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentRevealPreview() {
    var text by remember { mutableStateOf("") }
    ContentReveal(
        clipParticlesWithHiddenContent = true,
        dividerRotationEnabled = false,
        onFullyRevealed = {
            if (it)
                text = "VISIBLE"
        },
        onFullyHidden = {
            if (it)
                text = "HIDDEN"
        }
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Color(0xFF927C6D), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
        visibleContent {
            Box(modifier = modifier)
            /*Text(
                text = "This text is passed $text",
                color = Color(0xFF93E211),
                fontSize = 38.sp,
                fontWeight = FontWeight.W200
            )*/
        }

        hiddenContent {
            Image(
                modifier = modifier,
                painter = painterResource(R.drawable.cat),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            /*Text(
                text = "passed Hidden $text",
                color = Color(0xFF7F3D2D),
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )*/
        }
    }
}


