package dev.palindrom615

import com.linecorp.centraldogma.client.armeria.ArmeriaCentralDogmaBuilder
import com.linecorp.centraldogma.common.Query
import io.kotest.core.spec.style.FunSpec

class CDTest: FunSpec( {
    test("testCdFileWatcherThrowError") {
        val cd = ArmeriaCentralDogmaBuilder()
            .host("localhost", 443)
            .useTls()
            .build()
        val watcher = cd.fileWatcher(
            "project",
            "repo",
            Query.ofJsonPath( "/file.json", "$.aaa",))
        { entry ->
            throw Exception("test")
        }
        watcher.awaitInitialValue()
    }
})