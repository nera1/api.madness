package api.madn.es.config.ses

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sesv2.SesV2Client

@Configuration
@EnableConfigurationProperties(AwsProps::class)
class AwsSesConfig(
    private val props: AwsProps
) {
    @Bean
    fun sesV2Client(): SesV2Client {
        val creds = AwsBasicCredentials.create(props.accessKey, props.secretKey)
        return SesV2Client.builder()
            .region(Region.of(props.region))
            .credentialsProvider(StaticCredentialsProvider.create(creds))
            .build()
    }
}

@ConfigurationProperties(prefix = "aws")
data class AwsProps(
    val accessKey: String,
    val secretKey: String,
    val region: String
)
