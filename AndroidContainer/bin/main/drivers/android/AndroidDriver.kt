package org.vontech.androidserver.drivers.android

class AndroidDriver {

    /**
     * A collection of constants and methods that are useful for non-instances
     * of the AndroidDriver object
     */
    companion object {

        /** Environment variable for the ANDROID_HOME **/
        @JvmField val ANDROID_HOME = "ANDROID_HOME"

    }

    /**
     * Returns whether the Android SDK is avaliable on this system
     * @return true if the Android SDK is available
     */
    fun isSdkAvailable(): Boolean {
        return false
    }

    /**
     * Returns true if
     */
    fun isAndroidHomeSet(): Boolean {
        val path = System.getenv("ANDROID_HOME")
        return path != null
    }

}