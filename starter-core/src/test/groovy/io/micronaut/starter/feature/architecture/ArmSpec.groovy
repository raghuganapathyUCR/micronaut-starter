package io.micronaut.starter.feature.architecture

import io.micronaut.starter.ApplicationContextSpec
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.feature.Category
import io.micronaut.starter.feature.OneOfFeature
import io.micronaut.starter.feature.aws.Cdk
import io.micronaut.starter.feature.function.awslambda.AwsLambda
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.JdkVersion
import io.micronaut.starter.options.Language
import io.micronaut.starter.options.MicronautJdkVersionConfiguration
import io.micronaut.starter.options.Options
import io.micronaut.starter.options.TestFramework
import spock.lang.Subject
import spock.util.environment.Jvm

class ArmSpec extends ApplicationContextSpec implements CommandOutputFixture {

    @Subject
    Arm arm = beanContext.getBean(Arm)

    void 'arm feature is in the cloud category'() {
        expect:
        arm.category == Category.CLOUD
    }

    void 'arm feature is an instance of AwsLambdaEventFeature'() {
        expect:
        arm instanceof CpuArchitecture
        arm instanceof OneOfFeature
    }

    void "arm supports every application type"(ApplicationType applicationType) {
        expect:
        arm.supports(applicationType)

        where:
        applicationType << ApplicationType.values()
    }

    void 'arm plus cdk feature sets lambda function architecture'() {
        when:

        Map<String, String> output = generate(ApplicationType.FUNCTION, new Options(Language.JAVA, TestFramework.JUNIT, BuildTool.GRADLE, MicronautJdkVersionConfiguration.DEFAULT_OPTION),
                [Cdk.NAME, AwsLambda.FEATURE_NAME_AWS_LAMBDA, Arm.NAME])

        then:
        output."$Cdk.INFRA_MODULE/src/main/java/example/micronaut/AppStack.java".contains($/import software.amazon.awscdk.services.lambda.Architecture/$)
        output."$Cdk.INFRA_MODULE/src/main/java/example/micronaut/AppStack.java".contains($/.architecture(Architecture.ARM_64)/$)
    }
}
