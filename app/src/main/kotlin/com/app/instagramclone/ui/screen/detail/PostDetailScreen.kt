package com.app.instagramclone.ui.screen.detail

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
fun PostDetailScreen(postId: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Post $postId")
    }
}

@Preview(name = "Post Detail — Light", showBackground = true)
@Preview(name = "Post Detail — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostDetailScreenPreview() {
    InstagramCloneTheme {
        Surface {
            PostDetailScreen(postId = "abc123")
        }
    }
}
