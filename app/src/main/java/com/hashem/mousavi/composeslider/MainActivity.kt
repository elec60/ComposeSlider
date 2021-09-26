package com.hashem.mousavi.composeslider

import android.graphics.Paint
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Blue)
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Slider()
            }

        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Slider(
    barHeight: Dp = 8.dp,
    barBackgroundColor: Color = Color.Black,
    tooltipWidth: Dp = 100.dp,
    tooltipHeight: Dp = 50.dp,
    triangleHeight: Dp = 50.dp
) {
    var touchX by remember {
        mutableStateOf(0f)
    }
    var touchY by remember {
        mutableStateOf(0f)
    }
    var drawTooltip by remember {
        mutableStateOf(false)
    }
    var r by remember {
        mutableStateOf(0f)
    }
    var l by remember {
        mutableStateOf(0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .onGloballyPositioned {
                val windowBounds = it.boundsInWindow()
                r = windowBounds.right
                l = windowBounds.left
                Log.d("onGloballyPositioned", "$l, $r")
            }
            .pointerInteropFilter { event ->
                touchX = event.x
                touchY = event.y

                if (touchX <= 0) touchX = 0f
                if (touchX >= r - l) touchX = r - l

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        drawTooltip = true
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        drawTooltip = false
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {



        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val rect = RoundRect(
                0f,
                0f,
                size.width,
                barHeight.toPx(),
                cornerRadius = CornerRadius(barHeight.toPx() / 2f, barHeight.toPx() / 2f)
            )

            val path = Path()
            path.addRoundRect(rect)

            drawRoundRect(
                color = barBackgroundColor,
                cornerRadius = CornerRadius(barHeight.toPx() / 2f, barHeight.toPx() / 2f)
            )


            clipPath(path = path) {
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(barHeight.toPx() / 2f, barHeight.toPx() / 2f),
                    size = Size(touchX, barHeight.toPx())
                )
            }

            //draw tooltip
            if (drawTooltip) {

                val left = touchX - tooltipWidth.toPx() / 2f
                val right = touchX + tooltipWidth.toPx() / 2f
                var L = left
                var R = right

                val tooltipPath = Path().apply {

                    if (left <= -l) {
                        L = -l
                        R = L + tooltipWidth.toPx()
                    }

                    if (right >= r) {
                        R = r
                        L = R - tooltipWidth.toPx()
                    }

                    addRoundRect(
                        RoundRect(
                            left = L,
                            top = -(triangleHeight + tooltipHeight).toPx(),
                            right = R,
                            bottom = -(triangleHeight).toPx(),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                        )
                    )
                    val p = Path().apply {
                        moveTo(touchX, 0f)
                        lineTo(touchX - 4.dp.toPx(), -(triangleHeight).toPx())
                        lineTo(touchX + 4.dp.toPx(), -(triangleHeight).toPx())
                        close()
                    }

                    addPath(p)

                    close()
                }

                drawPath(path = tooltipPath, color = Color.White)

                //draw text
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "${(touchX * 100 / (r - l)).roundToInt()}%",
                        L + tooltipWidth.toPx() / 2 - 12.dp.toPx(),
                        -(triangleHeight + tooltipHeight.div(2)).toPx() + 5.dp.toPx(),
                        TextPaint().apply {
                            color = android.graphics.Color.BLACK
                            this.textSize = 60f
                        }
                    )
                }
            }


        }

    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue)
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Slider()
    }
}