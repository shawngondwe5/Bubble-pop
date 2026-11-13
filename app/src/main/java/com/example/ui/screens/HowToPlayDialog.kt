package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JellyButton
import com.example.ui.components.WoodFrameCard
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.GameGold
import com.example.ui.theme.TitanOneFontFamily

@Composable
fun HowToPlayDialog(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .testTag("how_to_play_dialog"),
        contentAlignment = Alignment.Center
    ) {
        WoodFrameCard(
            title = "HOW TO PLAY",
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x44000000))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RuleItem(
                    icon = Icons.Default.Navigation,
                    iconBg = Color(0xFF1E88E5),
                    title = "Aim & Shoot",
                    desc = "Drag your finger across the screen to aim the dotted line. The line bounces off the side walls! Release to fire the bubble into the grid."
                )

                RuleItem(
                    icon = Icons.Default.Check,
                    iconBg = Color(0xFF43A047),
                    title = "Match 3 or More",
                    desc = "Connect 3 or more bubbles of the exact same colour to pop them and clear the board."
                )

                RuleItem(
                    icon = Icons.Default.SwapHoriz,
                    iconBg = Color(0xFFFF9800),
                    title = "Swap Bubble Colours",
                    desc = "Tap the SWAP preview slot beside the cannon to switch between your current and next bubble."
                )

                RuleItem(
                    icon = Icons.Default.Star,
                    iconBg = GameGold,
                    title = "Avalanche Drops",
                    desc = "Bubbles that lose their connection to the ceiling fall down and award huge bonus points!"
                )

                RuleItem(
                    icon = Icons.Default.LocalFireDepartment,
                    iconBg = Color(0xFFFF3D00),
                    title = "Ceiling & Danger Line",
                    desc = "Taking 4 shots without making a match pushes the ceiling down! Don't let bubbles reach the red danger line."
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            JellyButton(
                text = "GOT IT!",
                onClick = onClose,
                icon = Icons.Default.Check,
                primaryColor = Color(0xFF43A047),
                darkColor = Color(0xFF1B5E20),
                lightColor = Color(0xFFA5D6A7),
                testTag = "how_to_play_close_button",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RuleItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = TitanOneFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GameGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontFamily = FredokaFontFamily,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color.White
            )
        }
    }
}
