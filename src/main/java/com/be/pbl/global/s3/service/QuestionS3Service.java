package com.be.pbl.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.entity.ImageDirection;
import com.be.pbl.domain.question.entity.Question;
import com.be.pbl.domain.question.exception.QuestionErrorCode;
import com.be.pbl.domain.question.repository.QuestionRepository;
import com.be.pbl.global.config.S3Config;
import com.be.pbl.global.exception.CustomException;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.dto.response.QuestionS3Response;
import com.be.pbl.global.s3.exception.S3ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionS3Service {

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;
    private final ExhibitionS3Service exhibitionS3Service; // 키 생성 메서드만 재사용
    private final QuestionRepository questionRepository;

    // 허용된 이미지 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif", "ico", "heic", "heif"
    );

    // 허용된 이미지 MIME 타입
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp",
        "image/svg+xml", "image/tiff", "image/x-icon", "image/heic", "image/heif"
    );

    // s3 업로드 DB 상태 업데이트
    @Transactional
    public QuestionS3Response uploadToS3(PathName pathName, MultipartFile file, Long questionId, Category category, ImageDirection direction) {

        String imgUrl = uploadImage(pathName, file); // s3 url

        log.info("질문 id = {}에 대한 이미지 업데이트",  questionId);
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

        question.setCategory(category);

        // 방향 별로 생성할 이미지 결정
        if(direction == ImageDirection.LEFT) {
            question.setLeftImageUrl(imgUrl);
        } else if(direction == ImageDirection.RIGHT) {
            question.setRightImageUrl(imgUrl);
        }

        return QuestionS3Response.builder()
            .id(questionId)
            .imageUrl(imgUrl)
            .category(category)
            .direction(direction)
            .build();
    }

    // 로컬에서 S3로 이미지 업로드
    public String uploadImage(PathName pathName, MultipartFile file) {
        validateFile(file);

        String keyName = exhibitionS3Service.createKeyName(pathName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType()); // 파일의 MIME 타입 (ex. application/pdf, image/png)

        try {
            amazonS3.putObject(
                new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
            return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
        } catch (Exception e) {
            log.error("S3 upload 중 오류 발생", e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        }
    }

    // 파일 유효성 검사 (파일 크기, 확장자, MIME 타입)
    private void validateFile(MultipartFile file) {
        // 파일 크기 검사 (5MB 이하)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
        }

        // 파일 확장자 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            log.warn("허용되지 않은 파일 확장자: {}", fileExtension);
            throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
        }

        // MIME 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("허용되지 않은 MIME 타입: {}", contentType);
            throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
        }

        log.info("파일 검증 성공 - 파일명: {}, 확장자: {}, MIME: {}, 크기: {} bytes",
            originalFilename, fileExtension, contentType, file.getSize());
    }

    // 파일 확장자 추출
    private String getFileExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자 없음
        }
        return filename.substring(lastIndexOf + 1);
    }

}
