package hu.csepviktor

import javafx.util.StringConverter
import java.io.*

class TimeStringConverter : StringConverter<Number>() {
    override fun toString(`object`: Number): String {
        val seconds = `object`.toLong() / 1_000_000_000
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    override fun fromString(string: String): Number {
        return string.split(':').let { it[0].toInt() * 60 + it[1].toInt() }
    }
}


// NOT USED, serialization is through JSON and encrypting
inline fun <reified T : Serializable> fromByteArray(byteArray: ByteArray): T? {
    ByteArrayInputStream(byteArray).use { byteIs ->
        ObjectInputStream(byteIs).use { ois ->
            val inputObject = ois.readObject()
            return if (inputObject is T)
                inputObject
            else
                null
        }
    }
}

fun Serializable.toByteArray(): ByteArray {
    ByteArrayOutputStream().use { byteOs ->
        ObjectOutputStream(byteOs).use { oos ->
            oos.writeObject(this)
            oos.flush()
        }
        return byteOs.toByteArray()
    }
}