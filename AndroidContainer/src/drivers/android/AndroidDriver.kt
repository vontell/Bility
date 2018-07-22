package org.vontech.androidserver.drivers.android

import org.vontech.androidserver.utils.CommandRunner

class AndroidDriver {

    /**
     * A collection of constants and methods that are useful for non-instances
     * of the AndroidDriver object
     */
    companion object {

        /** Environment variable for the ANDROID_HOME **/
        const val ANDROID_HOME = "ANDROID_HOME"

        /** Folder info for the Android Tools **/
        const val PLATFORM_PATH = "/platforms"
        const val BUILD_TOOLS = "/build-tools"

    }

    fun getAvailablePlatforms(): List<String> {
        val result = CommandRunner.executeCommand("ls " + getAndroidHome() + PLATFORM_PATH)
        return result.split("\n").dropLast(1)
    }

    fun getAvailableBuildTools(): List<String> {
        val result = CommandRunner.executeCommand("ls " + getAndroidHome() + BUILD_TOOLS)
        return result.split("\n").dropLast(1)
    }

    /**
     * Returns true if ANDROID_HOME is set on this host
     */
    fun isAndroidHomeSet(): Boolean {
        println(System.getenv())
        println(ANDROID_HOME)
        println(System.getenv(ANDROID_HOME))
        val path = System.getenv(ANDROID_HOME)
        return path != null
    }

    fun getAndroidHome(): String? {
        return System.getenv(ANDROID_HOME)
    }

}