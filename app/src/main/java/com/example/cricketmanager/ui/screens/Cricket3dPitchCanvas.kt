package com.example.cricketmanager.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cricketmanager.data.model.BallEventEntity
import com.example.cricketmanager.ui.theme.*
import kotlin.math.*

@Composable
fun Cricket3dPitchCanvas(
    lastBall: BallEventEntity?,
    isBowlingActive: Boolean,
    modifier: Modifier = Modifier
) {
    var cameraMode by remember { mutableStateOf("3D Broadcast") }

    // Ball simulation animation cycle
    val transition = rememberInfiniteTransition(label = "pitch_anim")
    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_progress"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CricketNavySurface)
            .border(1.dp, CricketGoldBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw 3D Stadium Atmosphere & Crowd Backdrop
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090E20),
                        Color(0xFF131D3F),
                        Color(0xFF0E3821),
                        TurfGreenDark
                    ),
                    startY = 0f,
                    endY = height * 0.45f
                ),
                size = Size(width, height * 0.45f)
            )

            // Stadium Floodlights Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x66FFFFFF), Color(0x33448AFF), Color.Transparent),
                    center = Offset(width * 0.12f, height * 0.12f),
                    radius = 80f
                ),
                center = Offset(width * 0.12f, height * 0.12f),
                radius = 80f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x66FFFFFF), Color(0x33448AFF), Color.Transparent),
                    center = Offset(width * 0.88f, height * 0.12f),
                    radius = 80f
                ),
                center = Offset(width * 0.88f, height * 0.12f),
                radius = 80f
            )

            // 2. 3D Turf Ground (Perspective Oval)
            val turfPath = Path().apply {
                moveTo(width * 0.05f, height)
                lineTo(width * 0.15f, height * 0.28f)
                cubicTo(
                    width * 0.35f, height * 0.20f,
                    width * 0.65f, height * 0.20f,
                    width * 0.85f, height * 0.28f
                )
                lineTo(width * 0.95f, height)
                close()
            }
            drawPath(
                path = turfPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF144D29), Color(0xFF0D331A)),
                    startY = height * 0.25f,
                    endY = height
                )
            )

            // 30-Yard Fielding Circle (Perspective Oval)
            drawOval(
                brush = Brush.horizontalGradient(listOf(Color(0x44FFFFFF), Color(0x22FFFFFF))),
                topLeft = Offset(width * 0.20f, height * 0.35f),
                size = Size(width * 0.60f, height * 0.55f),
                style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
            )

            // 3. 22-Yard Pitch Strip (Perspective Trapezoid)
            val pitchTopWidth = width * 0.14f
            val pitchBottomWidth = width * 0.30f
            val pitchTopY = height * 0.30f
            val pitchBottomY = height * 0.88f

            val pitchLeftTop = (width - pitchTopWidth) / 2
            val pitchRightTop = pitchLeftTop + pitchTopWidth
            val pitchLeftBottom = (width - pitchBottomWidth) / 2
            val pitchRightBottom = pitchLeftBottom + pitchBottomWidth

            val pitchPath = Path().apply {
                moveTo(pitchLeftTop, pitchTopY)
                lineTo(pitchRightTop, pitchTopY)
                lineTo(pitchRightBottom, pitchBottomY)
                lineTo(pitchLeftBottom, pitchBottomY)
                close()
            }

            drawPath(
                path = pitchPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC7A87A), Color(0xFFB59364), Color(0xFFA17F52)),
                    startY = pitchTopY,
                    endY = pitchBottomY
                )
            )

            // Pitch Creases (White Lines)
            // Bowling Crease (Top)
            drawLine(
                color = Color.White,
                start = Offset(pitchLeftTop + 4f, pitchTopY + 12f),
                end = Offset(pitchRightTop - 4f, pitchTopY + 12f),
                strokeWidth = 2f
            )

            // Popping Crease (Bottom / Striker End)
            drawLine(
                color = Color.White,
                start = Offset(pitchLeftBottom - 12f, pitchBottomY - 20f),
                end = Offset(pitchRightBottom + 12f, pitchBottomY - 20f),
                strokeWidth = 2.5f
            )

            // 4. Stumps & Bails with 3D Depth
            // Non-striker Stumps (Top End)
            val topStumpsCenterX = width * 0.5f
            for (i in -1..1) {
                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(topStumpsCenterX + (i * 3.5f), pitchTopY + 10f),
                    end = Offset(topStumpsCenterX + (i * 3.5f), pitchTopY + 2f),
                    strokeWidth = 1.8f
                )
            }
            // Top Bail
            drawLine(
                color = Color(0xFFFFE082),
                start = Offset(topStumpsCenterX - 5f, pitchTopY + 2f),
                end = Offset(topStumpsCenterX + 5f, pitchTopY + 2f),
                strokeWidth = 1.5f
            )

            // Striker Stumps (Bottom End - Bigger in 3D perspective)
            val bottomStumpsCenterX = width * 0.5f
            for (i in -1..1) {
                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(bottomStumpsCenterX + (i * 6f), pitchBottomY - 14f),
                    end = Offset(bottomStumpsCenterX + (i * 6f), pitchBottomY - 32f),
                    strokeWidth = 3f
                )
            }
            // Bottom Bail
            drawLine(
                color = Color(0xFFFFE082),
                start = Offset(bottomStumpsCenterX - 8f, pitchBottomY - 32f),
                end = Offset(bottomStumpsCenterX + 8f, pitchBottomY - 32f),
                strokeWidth = 2f
            )

            // 5. Dynamic Actors Animation
            val runsScored = lastBall?.runsBat ?: 1
            val isSix = lastBall?.runsBat == 6
            val isFour = lastBall?.runsBat == 4
            val isWicket = lastBall?.isWicket == true

            // Bowler Run-up & Delivery (Top to Bowling Crease)
            val bowlerRunY = if (animationProgress < 0.35f) {
                pitchTopY - 26f + (animationProgress / 0.35f) * 30f
            } else {
                pitchTopY + 4f
            }
            drawCircle(
                color = CricketStadiumBlue,
                center = Offset(topStumpsCenterX - 18f, bowlerRunY),
                radius = 7f
            )
            // Bowler Head
            drawCircle(
                color = Color(0xFFFFCC80),
                center = Offset(topStumpsCenterX - 18f, bowlerRunY - 8f),
                radius = 4.5f
            )

            // Batsman Stance & Swing (Bottom Striker End)
            val batsmanX = bottomStumpsCenterX - 18f
            val batsmanY = pitchBottomY - 24f

            // Batsman Body
            drawCircle(
                color = CricketGold,
                center = Offset(batsmanX, batsmanY),
                radius = 11f
            )
            // Batsman Helmet
            drawCircle(
                color = Color(0xFF1E88E5),
                center = Offset(batsmanX, batsmanY - 14f),
                radius = 6.5f
            )

            // Bat Angle & Swing
            val batAngle = if (animationProgress in 0.40f..0.70f) {
                if (isSix) -45f else if (isFour) 30f else -10f
            } else {
                15f
            }
            val batLength = 22f
            val batEndX = batsmanX + cos(Math.toRadians(batAngle.toDouble())).toFloat() * batLength
            val batEndY = batsmanY + sin(Math.toRadians(batAngle.toDouble())).toFloat() * batLength

            drawLine(
                color = Color(0xFFD7CCC8),
                start = Offset(batsmanX + 2f, batsmanY),
                end = Offset(batEndX, batEndY),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )

            // Wicket Keeper (Behind Stumps)
            drawCircle(
                color = CricketStadiumPurple,
                center = Offset(bottomStumpsCenterX, pitchBottomY + 12f),
                radius = 9f
            )

            // 6. Dynamic Ball Physics in 3D Arc
            if (animationProgress in 0.30f..0.95f) {
                val ballCycle = (animationProgress - 0.30f) / 0.65f // 0 to 1

                val startBallPos = Offset(topStumpsCenterX - 16f, pitchTopY + 12f)
                val pitchBouncePos = Offset(width * 0.5f, height * 0.60f)

                val hitTargetPos = when {
                    isSix -> Offset(width * 0.88f, height * 0.08f) // Flies into stands!
                    isFour -> Offset(width * 0.08f, height * 0.65f) // Boundaries
                    isWicket -> Offset(bottomStumpsCenterX, pitchBottomY - 26f) // Stumps smashed!
                    else -> Offset(width * 0.72f, height * 0.52f) // Fielded
                }

                val currentBallPos: Offset
                val ballRadius: Float

                if (ballCycle < 0.45f) {
                    // Travel from bowler to bounce on pitch
                    val subP = ballCycle / 0.45f
                    val curX = startBallPos.x + (pitchBouncePos.x - startBallPos.x) * subP
                    val curY = startBallPos.y + (pitchBouncePos.y - startBallPos.y) * subP
                    val arcHeight = sin(subP * Math.PI).toFloat() * 18f
                    currentBallPos = Offset(curX, curY - arcHeight)
                    ballRadius = 3f + (subP * 2f)

                    // Ball Shadow on pitch
                    drawCircle(
                        color = Color(0x55000000),
                        center = Offset(curX, curY),
                        radius = ballRadius * 0.8f
                    )
                } else {
                    // Off the bat into the ground / air
                    val subP = (ballCycle - 0.45f) / 0.55f
                    val curX = pitchBouncePos.x + (hitTargetPos.x - pitchBouncePos.x) * subP
                    val curY = pitchBouncePos.y + (hitTargetPos.y - pitchBouncePos.y) * subP
                    val arcHeight = if (isSix) sin(subP * Math.PI).toFloat() * 70f else sin(subP * Math.PI).toFloat() * 14f
                    currentBallPos = Offset(curX, curY - arcHeight)
                    ballRadius = if (isSix) 5f + sin(subP * Math.PI).toFloat() * 4f else 4.5f

                    // Trajectory tracer line
                    drawLine(
                        brush = Brush.linearGradient(
                            listOf(Color(0x88FFD700), Color(0x00FFD700)),
                            start = pitchBouncePos,
                            end = currentBallPos
                        ),
                        start = pitchBouncePos,
                        end = currentBallPos,
                        strokeWidth = 2.5f
                    )
                }

                // Draw 3D Cricket Ball (White / Crimson with seam)
                drawCircle(
                    color = Color(0xFFD32F2F),
                    center = currentBallPos,
                    radius = ballRadius
                )
                drawCircle(
                    color = Color.White,
                    center = currentBallPos,
                    radius = ballRadius * 0.35f
                )
            }

            // 7. Fielders in Realistic 3D Positions
            val fielders = listOf(
                Offset(width * 0.65f, height * 0.78f) to "Slip",
                Offset(width * 0.22f, height * 0.66f) to "Point",
                Offset(width * 0.28f, height * 0.52f) to "Cover",
                Offset(width * 0.40f, height * 0.38f) to "Mid-off",
                Offset(width * 0.60f, height * 0.38f) to "Mid-on",
                Offset(width * 0.78f, height * 0.56f) to "Mid-wicket",
                Offset(width * 0.85f, height * 0.72f) to "Fine Leg",
                Offset(width * 0.12f, height * 0.42f) to "Deep Cover",
                Offset(width * 0.88f, height * 0.38f) to "Deep Wicket"
            )

            fielders.forEach { (pos, label) ->
                drawCircle(
                    color = CricketNavySurfaceElevated,
                    center = pos,
                    radius = 7.5f
                )
                drawCircle(
                    color = CricketStadiumBlue,
                    center = pos,
                    radius = 6f
                )
                drawCircle(
                    color = Color.White,
                    center = Offset(pos.x, pos.y - 1f),
                    radius = 2.5f
                )
            }

            // 8. Event Text Banner overlay if 4, 6 or Wicket
            if (lastBall != null && animationProgress in 0.50f..0.98f) {
                val bannerText = when {
                    lastBall.isWicket -> "WICKET!"
                    lastBall.runsBat == 6 -> "MAXIMUM 6!"
                    lastBall.runsBat == 4 -> "FOUR RUNS!"
                    else -> null
                }
                if (bannerText != null) {
                    val textColor = if (lastBall.isWicket) CricketRedAccent else CricketGold
                    val measured = textMeasurer.measure(
                        text = bannerText,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                    val textCenter = Offset((width - measured.size.width) / 2, height * 0.42f)

                    drawRoundRect(
                        color = Color(0xDD0A0F26),
                        topLeft = Offset(textCenter.x - 16f, textCenter.y - 8f),
                        size = Size(measured.size.width + 32f, measured.size.height + 16f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                    drawRoundRect(
                        color = textColor,
                        topLeft = Offset(textCenter.x - 16f, textCenter.y - 8f),
                        size = Size(measured.size.width + 32f, measured.size.height + 16f),
                        cornerRadius = CornerRadius(12f, 12f),
                        style = Stroke(width = 2f)
                    )
                    drawText(
                        textLayoutResult = measured,
                        topLeft = textCenter
                    )
                }
            }
        }

        // Overlay Badge with 3D Camera info
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xBB080C1E))
                    .border(0.5.dp, CricketGoldBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(CricketStadiumNeon)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "3D PITCH CAM",
                        color = CricketGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
