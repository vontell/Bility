package org.vontech.bility.server.drivers.android

import io.kotlintest.specs.FeatureSpec
import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import org.vontech.bility.server.main
import org.vontech.bility.server.pipeline.*
import org.vontech.bility.server.projectSaveLocation


class AndroidPipelineTest: FeatureSpec({

    feature("the Android pipeline") {

        scenario("should work with git") {

            val gitRepo = "git@github.com:vontell/BilityTestApplication.git"
            val projectName = "BilityTest"
            val packageName = "org.vontech.bilitytestapplication"
            val appModule = "app"
            val entryFolder = ""

            //withTestApplication(Application::main) {
            //    val project = Project(ProjectSourceType.GIT, gitRepo,  projectName, packageName, appModule, entryFolder, projectSaveLocation)
            //    var projectConfig = PipelineConfig(project)
            //    projectConfig = generateMissingConfig(projectConfig)

            //    val runner = PipelineRunner(projectConfig)
            //    runner.startRunner()
            //}

        }

    }

})