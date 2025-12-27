package prac.demonote.global.s3;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties(prefix = "aws.s3")
@Validated
public record S3Properties(

    @NotBlank(message = "S3 AccessKey는 필수입니다") String accesskey,

    @NotBlank(message = "S3 SecretKey는 필수입니다") String secretKey,

    @NotBlank(message = "S3 SecretKey는 필수입니다") String region,

    @NotBlank(message = "S3 BucketName은 필수입니다") String bucket

) {

}
