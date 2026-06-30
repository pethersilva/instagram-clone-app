package com.app.instagramclone.ui.screen.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.instagramclone.ui.theme.InstagramCloneTheme

@Composable
fun ProfileScreen(userId: String, onPostClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile $userId")
    }
}

@Preview(name = "Profile — Light", showBackground = true)
@Preview(name = "Profile — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenPreview() {
    InstagramCloneTheme {
        Surface {
            ProfileScreen(userId = "user-123", onPostClick = {})
        }
    }
}
