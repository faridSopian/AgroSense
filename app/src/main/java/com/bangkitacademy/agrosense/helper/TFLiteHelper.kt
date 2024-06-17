package com.bangkitacademy.agrosense.helper

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(context: Context, modelPath: String) {
    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context, modelPath))
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun runInference(input: FloatArray): Array<String> {
        val inputArray = arrayOf(input)
        val outputArray = Array(1) { FloatArray(1) }
        interpreter.run(inputArray, outputArray)

        // Mengonversi outputArray ke dalam bentuk Array<String> nama tanaman
        val recommendedPlantName = outputArray.map { it.toString() }.toTypedArray()
        return recommendedPlantName
    }

    fun close() {
        interpreter.close()
    }
}


