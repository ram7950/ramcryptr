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

private const val PREFIX="RAMCRYPT_V2|"

private const val KEY=
"12345678901234567890123456789012"

private fun makeKey():ByteArray{
return MessageDigest
.getInstance("SHA-256")
.digest(KEY.toByteArray())
}

fun encryptFile(
input:File,
output:File,
ext:String,
mime:String
){

val header =
"${PREFIX}ext=${ext}|mime=${mime}\n"

val iv=ByteArray(16)
SecureRandom().nextBytes(iv)

val cipher=
Cipher.getInstance("AES/CBC/PKCS5Padding")

cipher.init(
Cipher.ENCRYPT_MODE,
SecretKeySpec(makeKey(),"AES"),
IvParameterSpec(iv)
)

FileOutputStream(output).use{fos->

fos.write(header.toByteArray())
fos.write(iv)

CipherOutputStream(fos,cipher).use{cos->

FileInputStream(input).use{fis->
fis.copyTo(cos)
}

}

}

}

fun decryptFile(
input:File,
output:File
): Pair<String,String>{

FileInputStream(input).use{fis->

val headerBuilder=StringBuilder()

while(true){
val ch=fis.read()
if(ch==-1 || ch.toChar()=='\n') break
headerBuilder.append(ch.toChar())
}

val header=headerBuilder.toString()

if(!header.startsWith(PREFIX)){
throw Exception("Invalid encrypted file")
}

val parts=header.split("|")

var ext="tmp"
var mime="*/*"

for(p in parts){
if(p.startsWith("ext=")) ext=p.removePrefix("ext=")
if(p.startsWith("mime=")) mime=p.removePrefix("mime=")
}

val iv=ByteArray(16)
fis.read(iv)

val cipher=
Cipher.getInstance("AES/CBC/PKCS5Padding")

cipher.init(
Cipher.DECRYPT_MODE,
SecretKeySpec(makeKey(),"AES"),
IvParameterSpec(iv)
)

CipherInputStream(fis,cipher).use{cis->

FileOutputStream(output).use{fos->
cis.copyTo(fos)
}

}

return Pair(ext,mime)

}

}

}
