package com.shihcheeng.sspaiyou.user

import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.ui.screen.favourite.favouriteTypeLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class FavouriteArticleTypeTest {
    @Test
    fun freeArticleUsesArticleLabel() {
        assertEquals(R.string.type_obj_1, ColumnArticle(free = true).favouriteTypeLabel())
    }

    @Test
    fun paidArticleUsesPaidLabel() {
        assertEquals(
            R.string.favourite_type_paid_article,
            ColumnArticle(free = false).favouriteTypeLabel()
        )
    }

    @Test
    fun memberArticleTakesPrecedenceOverPaidLabel() {
        assertEquals(
            R.string.favourite_type_member_article,
            ColumnArticle(free = false, belongToMember = true).favouriteTypeLabel()
        )
    }

    @Test
    fun freeMemberArticleKeepsMemberLabel() {
        assertEquals(
            R.string.favourite_type_member_article,
            ColumnArticle(free = true, belongToMember = true).favouriteTypeLabel()
        )
    }

    @Test
    fun podcastTakesPrecedenceOverAccessLabels() {
        assertEquals(
            R.string.favourite_type_podcast,
            ColumnArticle(podcastDuration = 120, belongToMember = true).favouriteTypeLabel()
        )
    }

    @Test
    fun unknownPostTypeDoesNotGuessAnEnumMeaning() {
        assertEquals(
            R.string.type_obj_1,
            ColumnArticle(free = true, postType = 999).favouriteTypeLabel()
        )
    }
}
