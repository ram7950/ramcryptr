package com.rambo.ramcryptr.encoding

import java.util.Calendar

object TBKSys8Manager {

    private val keys = listOf(
        "KEY_WEEK_1",
        "KEY_WEEK_2",
        "KEY_WEEK_3",
        "KEY_WEEK_4",
        "KEY_WEEK_5",
        "KEY_WEEK_6",
        "KEY_WEEK_7",
        "KEY_WEEK_8"
    )

    fun getCurrentKey(): String {

        val week=
            Calendar.getInstance()
                .get(
                    Calendar.WEEK_OF_YEAR
                ) % 8

        return keys[week]
    }

}
