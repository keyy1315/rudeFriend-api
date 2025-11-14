package com.loltft.rudefriend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
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

    private val urlPrefix = "https://$bucketName.s3.$region.amazonaws.com/"


    @Throws(IOException::class)
    fun uploadFile(gameType: String, file: MultipartFile, fileUuid: UUID): String {
        val key = "$gameType/$fileUuid"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return urlPrefix + key
    }

    @Throws(IOException::class)
    fun deleteFiles(fileUrls: List<String>): MutableList<String> {
        val deletedFileUrls = mutableListOf<String>()
        for (url in fileUrls) {
            val key = url.removePrefix(urlPrefix)

            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

            s3Client.deleteObject(deleteRequest)
            deletedFileUrls.add(urlPrefix + key)
        }
        return deletedFileUrls
    }
}
