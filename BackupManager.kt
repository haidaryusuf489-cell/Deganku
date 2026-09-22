package com.deganku.app.core.backup

import android.content.Context
import com.deganku.app.core.security.KeystoreCrypto
import java.io.File
import java.security.MessageDigest

class BackupManager(context:Context){
    private val dir=File(context.filesDir,"backups").apply{mkdirs()}
    private val crypto=KeystoreCrypto()
    fun backup(db:File,schema:Int):File{
        require(db.exists())
        val bytes=db.readBytes()
        val meta="DGB1|$schema|${System.currentTimeMillis()}|${sha(bytes)}\n".toByteArray()
        val box=crypto.encrypt(meta+bytes)
        val out=File(dir,"deganku_${System.currentTimeMillis()}.dgb")
        out.writeBytes(byteArrayOf(0x44,0x47,0x42,0x31,box.iv.size.toByte())+box.iv+box.data)
        return out
    }
    private fun sha(b:ByteArray)=MessageDigest.getInstance("SHA-256").digest(b).joinToString(""){"%02x".format(it)}
}
