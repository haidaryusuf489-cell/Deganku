package com.deganku.app.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64

object PinSecurity {
    private const val ITERATIONS=210000
    fun create(pin:String):Pair<String,String>{
        require(pin.length>=4)
        val salt=ByteArray(16).also{SecureRandom().nextBytes(it)}
        val hash=derive(pin.toCharArray(),salt)
        return Base64.encodeToString(salt,Base64.NO_WRAP) to Base64.encodeToString(hash,Base64.NO_WRAP)
    }
    fun verify(pin:String,salt64:String,hash64:String):Boolean{
        val salt=Base64.decode(salt64,Base64.NO_WRAP)
        val candidate=derive(pin.toCharArray(),salt)
        return MessageDigest.isEqual(candidate,Base64.decode(hash64,Base64.NO_WRAP))
    }
    private fun derive(pin:CharArray,salt:ByteArray):ByteArray{
        val spec=PBEKeySpec(pin,salt,ITERATIONS,256)
        return try{SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded}
        finally{spec.clearPassword();pin.fill('\u0000')}
    }
}
