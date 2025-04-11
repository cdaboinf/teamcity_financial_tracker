import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.03"

project {

    vcsRoot(HttpsGithubComCdaboinfFinancialTrackerCoreGitRefsHeadsMain)

    buildType(Build)

    params {
        param("DotNetCoreSDK9.0.100_Path", "/root/.dotnet/sdk")
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "FinancialTrackerApi/FinancialTrackerApi/bin/publish.zip"

    params {
        param("teamcity.tool.NuGet.CommandLine.DEFAULT", "6.4.3")
    }

    vcs {
        root(HttpsGithubComCdaboinfFinancialTrackerCoreGitRefsHeadsMain)
    }

    steps {
        dotnetRestore {
            id = "dotnet"
            projects = "FinancialTrackerApi/FinancialTrackerApi.sln"
        }
        dotnetBuild {
            name = "Build Solution"
            id = "Build_Solution"
            projects = "FinancialTrackerApi/FinancialTrackerApi.sln"
            sdk = "9.0.100"
        }
        dotnetPublish {
            name = "Publish"
            id = "Publish"
            projects = "FinancialTrackerApi/FinancialTrackerApi"
        }
        script {
            name = "Zip Published Files"
            id = "Zip_Published_Files"
            scriptContent = """
                cd %teamcity.build.checkoutDir%/FinancialTrackerApi/FinancialTrackerApi/bin/Release/net8.0/publish
                /usr/bin/zip -r ../../../publish.zip .
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object HttpsGithubComCdaboinfFinancialTrackerCoreGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/cdaboinf/FinancialTrackerCore.git#refs/heads/main"
    url = "https://github.com/cdaboinf/FinancialTrackerCore.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "cedaboin@gmail.com"
        password = "credentialsJSON:963b8835-6112-4a56-a3f1-2fd627a591bb"
    }
})
