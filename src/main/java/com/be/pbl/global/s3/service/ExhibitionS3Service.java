package com.be.pbl.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.exception.ExhibitionErrorCode;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import com.be.pbl.global.config.S3Config;
import com.be.pbl.global.exception.CustomException;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.dto.response.S3Response;
import com.be.pbl.global.s3.exception.S3ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final S3Config s3Config;
    private final ExhibitionRepository exhibitionRepository;

    // 단일 URL에서 이미지를 다운로드하여 S3에 업로드
    private String uploadSingleImageFromUrl(PathName pathName, String imageUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            // URL 연결
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000); // 10초 타임아웃
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // User-Agent 설정

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.error("이미지 다운로드 실패. HTTP 응답 코드: {}, URL: {}", responseCode, imageUrl);
                throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
            }

            // Content-Type 확인
            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.error("이미지 파일이 아닙니다. Content-Type: {}, URL: {}", contentType, imageUrl);
                throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
            }

            // Content-Length 확인 (파일 크기 제한 5MB)
            int contentLength = connection.getContentLength();
            if (contentLength > 5 * 1024 * 1024) {
                log.error("파일 크기가 너무 큽니다. Size: {} bytes, URL: {}", contentLength, imageUrl);
                throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
            }

            // 이미지 다운로드
            inputStream = connection.getInputStream();

            // S3에 업로드
            String keyName = createKeyName(pathName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            if (contentLength > 0) {
                metadata.setContentLength(contentLength);
            }

            amazonS3.putObject(new PutObjectRequest(s3Config.getBucket(), keyName, inputStream, metadata));
            String s3Url = amazonS3.getUrl(s3Config.getBucket(), keyName).toString();

            log.info("이미지 마이그레이션 성공. 원본: {}, S3: {}", imageUrl, s3Url);
            return s3Url;

        } catch (IOException e) {
            log.error("이미지 다운로드 실패. URL: {}", imageUrl, e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("S3 업로드 실패. URL: {}", imageUrl, e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        } finally {
            // 리소스 정리
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                log.error("리소스 정리 중 오류 발생", e);
            }
        }
    }

    // isS3Upload가 false인 모든 전시회의 이미지를 S3로 업로드
    @Transactional
    public S3Response uploadExhibitionImages(PathName pathName) {
        log.info("isS3Upload가 false인 전시회들의 이미지 S3 업로드 시작");

        // isS3Upload가 false인 모든 전시회 조회
        List<Exhibition> exhibitions = exhibitionRepository.findAll().stream()
            .filter(exhibition -> !exhibition.isS3Upload())
            .toList();

        if (exhibitions.isEmpty()) {
            log.info("업로드할 전시회가 없습니다. 모든 전시회가 이미 S3에 업로드되었습니다.");
            return S3Response.builder()
                .successCount(0)
                .failCount(0)
                .build();
        }

        log.info("총 {} 개의 전시회 업로드 시작", exhibitions.size());

        // 전체 성공/실패 카운트
        AtomicInteger totalSuccessCount = new AtomicInteger(0);
        AtomicInteger totalFailCount = new AtomicInteger(0);

        // 각 전시회에 대해 업로드 수행
        for (Exhibition exhibition : exhibitions) {
            List<String> imageUrls = exhibition.getImgUrl();

            if (imageUrls == null || imageUrls.isEmpty()) {
                log.warn("전시회 ID {}: 업로드할 이미지가 없습니다.", exhibition.getId());
                continue;
            }

            log.info("전시회 ID {}: {} 개의 이미지 업로드 시작", exhibition.getId(), imageUrls.size());

            // 모든 이미지 URL을 S3에 업로드
            List<String> s3Urls = imageUrls.stream()
                .map(url -> {
                    // 이미 S3 URL인 경우 재업로드 스킵
                    if (url.contains("amazonaws.com") || url.contains("s3")) {
                        log.info("이미 S3 URL입니다. 스킵: {}", url);
                        totalSuccessCount.incrementAndGet();
                        return url;
                    }

                    try {
                        String s3Url = uploadSingleImageFromUrl(pathName, url);
                        totalSuccessCount.incrementAndGet();
                        return s3Url;
                    } catch (Exception e) {
                        log.error("이미지 업로드 실패. URL 유지: {}", url, e);
                        totalFailCount.incrementAndGet();
                        return url; // 실패 시 원본 URL 유지
                    }
                })
                .collect(Collectors.toList());

            log.info("전시회 ID {}: 이미지 업로드 완료", exhibition.getId());

            // 업로드 성공 후 Exhibition 엔티티 업데이트
            exhibition.updateImageUrls(s3Urls); // S3 URL로 업데이트
            exhibition.updateIsS3Upload(true); // 업로드 플래그 업데이트
            exhibitionRepository.save(exhibition);
        }

        log.info("전체 이미지 업로드 완료. 성공: {}, 실패: {}",
            totalSuccessCount.get(), totalFailCount.get());

        return S3Response.builder()
            .successCount(totalSuccessCount.get())
            .failCount(totalFailCount.get())
            .build();
    }

    // Exhibition의 이미지 URL 리스트를 S3로 마이그레이션 (AdminExhibitionService에서 사용)
    public List<String> migrateImageUrls(PathName pathName, List<String> imageUrls) {
        return imageUrls.stream()
            .map(url -> {
                try {
                    return uploadSingleImageFromUrl(pathName, url);
                } catch (Exception e) {
                    log.error("이미지 업로드 실패. URL 유지: {}", url, e);
                    return url; // 실패 시 원본 URL 유지
                }
            })
            .collect(Collectors.toList());
    }

    public String createKeyName(PathName pathName) {

        return switch (pathName) {
            case EXHIBITION -> s3Config.getExhibition();
            case QUESTION   -> s3Config.getQuestion();

        }
            + '/'
            + UUID.randomUUID();
    }

    public String getFileUrl(String keyName) {
        existFile(keyName);

        try {
            return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
        } catch (Exception e) {
            log.error("S3 upload 중 오류 발생", e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        }
    }

    public void deleteFile(String keyName) {
        existFile(keyName);

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), keyName));
        } catch (Exception e) {
            log.error("S3 삭제 중 오류 발생", e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        }
    }

    // 이미지 파일 관련 기능은 거의 개인 맞춤형이라 리스트로 쓸일이 채용공고 말고는 없을 듯(고려사항)
    public List<String> getAllFiles(PathName pathName) {
        String prefix = switch (pathName) {
            case EXHIBITION -> s3Config.getExhibition();
            case QUESTION   -> s3Config.getQuestion();

        };

        try {
            return amazonS3
                .listObjectsV2(
                    new ListObjectsV2Request().withBucketName(s3Config.getBucket()).withPrefix(prefix))
                .getObjectSummaries()
                .stream()
                .map(obj -> amazonS3.getUrl(s3Config.getBucket(), obj.getKey()).toString())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("S3 파일 목록 조회 중 오류 발생", e);
            throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
        }
    }

    public void deleteFile(PathName pathName, String fileName) {
        String prefix = switch (pathName) {
            case EXHIBITION -> s3Config.getExhibition();
            case QUESTION   -> s3Config.getQuestion();

        };
        String keyName = prefix + "/" + fileName;
        deleteFile(keyName);
    }

    // 파일 존재 여부 확인
    private void existFile(String keyName) {
        if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
            throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
        }
    }

    // 파일 유효성 검사 (파일 크기가 맞는지)
    private void validateFile(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
        }
    }
}
