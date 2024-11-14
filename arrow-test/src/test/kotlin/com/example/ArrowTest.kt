package com.example

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.zipOrAccumulate
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory

sealed interface Error

data object NationalityError : Error
data object TinError : Error

class User private constructor(
    val nationaily: String,
    val tin: String?,
) {
    companion object {
        operator fun invoke(nationality: String, tin: String?): Either<NonEmptyList<Error>, User> = either {
            zipOrAccumulate(
                { ensure(nationality == "US") { NationalityError } },
                { ensureNotNull(tin) { TinError } },
            )
            {_, _ -> }
            User(nationality, tin)
        }
    }
}

class ArrowTest {
    val logger = LoggerFactory.getLogger(this::class.java)
    @Test
    fun testArrow() {

        val user = User("KR", null)
        logger.info { user.toString() }
    }
}
