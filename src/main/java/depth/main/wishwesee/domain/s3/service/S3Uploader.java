package depth.main.wishwesee.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class S3Uploader {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일을 S3 버킷에 업로드
    public String uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String saveFileName = createSaveFileName(originalFileName);

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, saveFileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        // 업로드된 파일의 전체 URL 반환
        return getFullPath(saveFileName);
    }

    // 저장할 파일 이름을 생성
    private String createSaveFileName(String originalFileName) {
        String ext = extractExt(originalFileName); // 파일 확장자 추출
        String uuid = UUID.randomUUID().toString(); // 고유한 UUID 생성
        return uuid + "." + ext;
    }

    // 파일 이름에서 확장자를 추출
    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }

    // S3에 업로드된 파일의 전체 URL 경로 생성
    public String getFullPath(String fileName) {
        return "https://" + bucket + ".s3.amazonaws.com/" + fileName;
    }

    // S3에서 파일 삭제
    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.", e);
        }
    }

    public String extractImageNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
