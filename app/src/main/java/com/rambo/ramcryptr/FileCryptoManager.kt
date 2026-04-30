package com.rambo.ramcryptr

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object FileCryptoManager {

private const val HEADER="RAMCRYPT_V1\n"

private const val KEY=
"12345678901234567890123456789012"

private fun makeKey():ByteArray{
return MessageDigest
.getInstance("SHA-256")
.digest(
KEY.toByteArray()
)
}

fun encryptFile(
input:File,
output:File
){

val iv=ByteArray(16)
SecureRandom().nextBytes(iv)

val cipher=
Cipher.getInstance(
"AES/CBC/PKCS5Padding"
)

cipher.init(
Cipher.ENCRYPT_MODE,
SecretKeySpec(
makeKey(),
"AES"
),
IvParameterSpec(iv)
)

FileOutputStream(output).use{fos->

fos.write(
HEADER.toByteArray()
)

fos.write(iv)

CipherOutputStream(
fos,
cipher
).use{cos->

FileInputStream(
input
).use{fis->

fis.copyTo(cos)

}

}

}

}

fun decryptFile(
input:File,
output:File
){

FileInputStream(
input
).use{fis->

val headerBytes=
ByteArray(HEADER.length)

fis.read(headerBytes)

val header=String(headerBytes)

if(header!=HEADER){
throw Exception("Invalid encrypted file")
}

val iv=
ByteArray(16)

fis.read(iv)

val cipher=
Cipher.getInstance(
"AES/CBC/PKCS5Padding"
)

cipher.init(
Cipher.DECRYPT_MODE,
SecretKeySpec(
makeKey(),
"AES"
),
IvParameterSpec(iv)
)

CipherInputStream(
fis,
cipher
).use{cis->

FileOutputStream(
output
).use{fos->

cis.copyTo(fos)

}

}

}

}

}
