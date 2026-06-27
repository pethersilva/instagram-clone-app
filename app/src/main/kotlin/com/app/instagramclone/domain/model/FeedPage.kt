package com.app.instagramclone.domain.model

data class FeedPage(
    val data: List<Post>,
    val nextCursor: String?,
    val hasMore: Boolean
)
