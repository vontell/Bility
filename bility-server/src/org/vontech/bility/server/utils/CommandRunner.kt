package org.vontech.bility.server.utils

import java.io.*


class CommandRunner {

    companion object {

        private val runtime: Runtime = Runtime.getRuntime()

        // https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
        fun executeCommand(command: String, printAsWeGo: Boolean = false, wait: Boolean = true): String {

            val output = StringBuffer()

            val p: Process
            try {
                p = runtime.exec(command)

                if (!wait) {
                    return "... not waiting for command to finish"
                }

                val inputReader = BufferedReader(InputStreamReader(p.inputStream))
                val errorReader = BufferedReader(InputStreamReader(p.errorStream))

                var line = inputReader.readLine()
                while (line != null) {
                    if (printAsWeGo) {
                        println(line)
                    }
                    output.append(line + "\n")
                    line = inputReader.readLine()
                }

                line = errorReader.readLine()
                while (line != null) {
                    if (printAsWeGo) {
                        println(line)
                    }
                    output.append(line + "\n")
                    line = inputReader.readLine()
                }

                p.waitFor()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return output.toString()

        }

        fun gitClone(gitSource: String, saveLocation: String): String {
            return executeCommand("git clone $gitSource $saveLocation")
        }

        fun unzip(zipFileLocation: String, extractDestination: String): String {
            var result = executeCommand("unzip $zipFileLocation -d $extractDestination")
            result += executeCommand("rm -rf ${extractDestination}__MACOSX")
            return result
        }

        @Throws(IOException::class)
        fun copyFileUsingStream(source: File, dest: File) {
            var ins: InputStream? = null
            var os: OutputStream? = null
            try {
                ins = FileInputStream(source)
                os = FileOutputStream(dest)
                val buffer = ByteArray(1024)
                var length: Int
                length = ins.read(buffer)
                while (length > 0) {
                    os.write(buffer, 0, length)
                    length = ins.read(buffer)
                }
            } finally {
                ins!!.close()
                os!!.close()
            }
        }

    }

}