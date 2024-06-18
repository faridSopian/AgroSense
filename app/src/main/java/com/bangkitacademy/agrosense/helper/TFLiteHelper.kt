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

    fun runInference(input: FloatArray): String {
        val inputArray = arrayOf(input)
        val outputArray = Array(1) { FloatArray(22) } // Sesuaikan dengan bentuk output model
        interpreter.run(inputArray, outputArray)

        // Mengonversi outputArray ke dalam nama tanaman
        val recommendedPlantIndex = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1
        return getPlantNameByIndex(recommendedPlantIndex)
    }

    private fun getPlantNameByIndex(index: Int): String {
        // Ganti dengan nama tanaman sebenarnya sesuai dengan index output dari model
        val plantNames = arrayOf("Padi", "Jagung", "Jute", "Kapas", "Kelapa", "Pepaya",
            "Jeruk", "Apel", "Melon", "Semangka", "Anggur", "Mangga",
            "Pisang", "Delima", "Kacang Lentil", "Lentil Hitam", "Kacang Hijau", "Matki",
            "Kacang Gude", "Kacang Merah", "Kacang Arab", "Kopi")
        return if (index in plantNames.indices) plantNames[index] else "Unknown Plant"
    }

    fun close() {
        interpreter.close()
    }
}
