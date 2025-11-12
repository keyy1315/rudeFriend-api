package com.loltft.rudefriend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String,
    @Value("\${cloud.aws.region.static}")
    private val region: String
) {
    private val URL_PREFIX = "https://$bucketName.s3.$region.amazonaws.com/"

    @Throws(IOException::class)
    fun uploadFiles(gameType: String, files: List<MultipartFile>): MutableList<String> {
        val fileUrls = mutableListOf<String>()

        for (file in files) {
            val key = gameType + "/" + UUID.randomUUID().toString()

            val putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.contentType)
                .build()

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

            fileUrls.add(URL_PREFIX + key)
        }
        return fileUrls
    }

    @Throws(IOException::class)
    fun deleteFiles(fileUrls: List<String>): MutableList<String> {
        val deletedFileUrls = mutableListOf<String>()
        for (url in fileUrls) {
            val key = url.removePrefix(URL_PREFIX)

            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

            s3Client.deleteObject(deleteRequest)
            deletedFileUrls.add(URL_PREFIX + key)
        }
        return deletedFileUrls
    }
}
