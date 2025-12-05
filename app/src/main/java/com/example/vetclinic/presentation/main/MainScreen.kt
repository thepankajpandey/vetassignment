package com.example.vetclinic.presentation.main

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vetclinic.data.model.PetDto
import com.example.vetclinic.ui.theme.VetClinicTheme
import com.example.vetclinic.util.WorkHoursUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

val ChatColor = Color(0xFF1E88E5)
val CallColor = Color(0xFF4CAF50)

@Composable
fun MainScreen(
    state: UiState
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        CommunicationButtons(
            isChatEnabled = state.isChatEnabled,
            isCallEnabled = state.isCallEnabled,
            workHours = state.workHours
        )

        OfficeHoursCard(state.workHours)
        HorizontalDivider(modifier = Modifier.padding(start = 16.dp, top = 16.dp))

        when {
            state.isLoading -> LoadingView()
            state.error != null -> ErrorView(state.error)
            else -> PetsList(state.pets) {}
        }
    }
}

@Composable
fun OfficeHoursCard(workHours: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(2.dp, Color.Gray)
    ) {
        Text(
            text = "Office Hours: $workHours",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoadingView() =
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

@Composable
fun ErrorView(error: String) =
    Text(
        text = "Error: $error",
        color = Color.Red,
        modifier = Modifier.padding(16.dp)
    )

@Composable
fun PetsList(pets: List<PetDto>, onPetClick: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(pets) { pet ->
            PetRow(
                title = pet.title,
                imageUrl = pet.image_url,
                onClick = { onPetClick(pet.content_url) }
            )
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
fun CommunicationButtons(
    isChatEnabled: Boolean,
    isCallEnabled: Boolean,
    workHours: String
) {
    val isWithin = WorkHoursUtil.isWithinWorkHours(workHours)

    val dialogMessage = if (isWithin) {
        "Thank you for getting in touch with us. We’ll get back to you as soon as possible"
    } else {
        "Work hours has ended. Please contact us again on the next work day"
    }

    var showDialog by remember { mutableStateOf(false) }
    val onButtonClick = {
        showDialog = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val modifier = Modifier
            .weight(1f)
            .height(56.dp)

        if (isChatEnabled) {
            RectangularButton(
                modifier,
                ButtonDefaults.buttonColors(containerColor = ChatColor),
                "Chat",
                onClick = onButtonClick
            )
        }

        if (isChatEnabled && isCallEnabled) {
            Spacer(modifier = Modifier.width(16.dp))
        }

        if (isCallEnabled) {
            RectangularButton(
                modifier,
                ButtonDefaults.buttonColors(containerColor = CallColor),
                "Call",
                onClick = onButtonClick
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Information") },
                text = { Text(dialogMessage) }
            )
        }
    }
}

@Composable
fun RectangularButton(
    modifier: Modifier = Modifier,
    colors: ButtonColors,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = colors,
        shape = RoundedCornerShape(20),
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@Composable
fun PetRow(title: String, imageUrl: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(84.dp)
        .clickable { onClick() }
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {

        val bitmapState = produceState<android.graphics.Bitmap?>(initialValue = null, imageUrl) {
            value = loadBitmap(imageUrl)
        }

        if (bitmapState.value != null) {
            Image(
                bitmapState.value!!.asImageBitmap(),
                contentDescription = title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.DarkGray)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
    }
}

suspend fun loadBitmap(urlStr: String): android.graphics.Bitmap? {
    return withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val code = conn.responseCode
            if (code in 200..299) {
                val stream = conn.inputStream
                BitmapFactory.decodeStream(stream)
            } else null
        } catch (_: Throwable) {
            null
        } finally {
            conn?.disconnect()
        }
    }
}

@Preview(
    name = "Light Mode, Small Font",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    fontScale = 0.85f // Smaller font
)
@Preview(
    name = "Dark Mode, Large Font",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    fontScale = 1.3f // Larger font
)
@Preview(
    name = "Tablet Landscape",
    device = "spec:width=1280dp,height=800dp,orientation=landscape",
    showBackground = true
)
@Composable
fun MainScreenPreview() {
    val uiState = UiState(
        isLoading = false,
        isChatEnabled = true,
        isCallEnabled = true,
        workHours = "M-F 09:00 - 18:00",
        pets = listOf(
            PetDto(
                image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Cat_poster_1.jpg/1200px-Cat_poster_1.jpg",
                title = "Cat",
                content_url = "https://en.wikipedia.org/wiki/Cat",
                date_added = "2018-06-02T03:27:38.027Z"
            ),
            PetDto(
                image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Cat_poster_1.jpg/1200px-Cat_poster_1.jpg",
                title = "Dog",
                content_url = "https://en.wikipedia.org/wiki/Cat",
                date_added = "2018-06-02T03:27:38.027Z"
            ),
            PetDto(
                image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Cat_poster_1.jpg/1200px-Cat_poster_1.jpg",
                title = "Cow",
                content_url = "https://en.wikipedia.org/wiki/Cat",
                date_added = "2018-06-02T03:27:38.027Z"
            ),
            PetDto(
                image_url = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Cat_poster_1.jpg/1200px-Cat_poster_1.jpg",
                title = "Horse",
                content_url = "https://en.wikipedia.org/wiki/Cat",
                date_added = "2018-06-02T03:27:38.027Z"
            )
        )
    )

    VetClinicTheme {
        MainScreen(
            state = uiState
        )
    }
}

