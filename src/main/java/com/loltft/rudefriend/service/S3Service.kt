package com.loltft.rudefriend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.util.*

@Service
class S3Service {
    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String? = null

    @Value("\${cloud.aws.region.static}")
    private val region: String? = null

    private val s3Client: S3Client? = null

    @Throws(IOException::class)
    fun uploadFile(file: MultipartFile): String? {
        val key: String? = UUID.randomUUID().toString()

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()

        s3Client!!.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()))

        val s3FileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key
        return key
    }
}
