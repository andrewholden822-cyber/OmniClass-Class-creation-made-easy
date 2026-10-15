package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ClassEntity
import com.example.data.models.ModuleEntity
import org.json.JSONArray

@Composable
fun CourseLandingScreen(
    course: ClassEntity,
    modules: List<ModuleEntity>,
    onEnterClassroom: () -> Unit,
    onToggleEnrollment: () -> Unit,
    onExportWebsite: () -> Unit,
    onLaunchLiveWebsite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val takeaways = mutableListOf<String>()
    try {
        val arr = JSONArray(course.keyTakeawaysJson)
        for (i in 0 until arr.length()) takeaways.add(arr.getString(i))
    } catch (_: Exception) {}

    val startColor = try {
        Color(android.graphics.Color.parseColor(course.bannerGradientStart))
    } catch (_: Exception) {
        Color(0xFF312E81)
    }

    val endColor = try {
        Color(android.graphics.Color.parseColor(course.bannerGradientEnd))
    } catch (_: Exception) {
        Color(0xFF0F172A)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Landing Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(startColor, endColor)))
                    .padding(24.dp)
            ) {
                Column {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "${course.domain} • ${course.level}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFE0E7FF),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = course.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFC7D2FE)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Meta Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${course.rating} Rating",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${course.studentCount} Students Enrolled",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0)
                        )

                        Text(
                            text = course.durationText,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFA5B4FC)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CTAs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onEnterClassroom,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("start_learning_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (course.isEnrolled) "Go to Classroom" else "Enroll & Start Class", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onLaunchLiveWebsite,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("launch_live_website_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Open Live Site")
                        }

                        OutlinedButton(
                            onClick = onExportWebsite,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("export_website_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export")
                        }
                    }
                }
            }
        }

        // Instructor Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(course.instructorAvatarColor))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = course.instructorName.take(1),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = course.instructorName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = course.instructorTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Course Description
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "About This Course",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = course.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Key Learning Outcomes
        if (takeaways.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What You'll Learn",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        takeaways.forEach { takeaway ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = takeaway,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Syllabus Modules Preview
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Syllabus Curriculum",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${modules.size} Modules",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        items(modules, key = { it.id }) { mod ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onEnterClassroom() },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Module ${mod.orderIndex}: ${mod.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mod.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${mod.durationMinutes}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
