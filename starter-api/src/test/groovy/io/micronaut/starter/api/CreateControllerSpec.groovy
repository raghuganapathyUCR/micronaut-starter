package io.micronaut.starter.api

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import io.micronaut.starter.options.TestFramework
import io.micronaut.starter.util.ZipUtil
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.annotation.Nullable
import javax.inject.Inject
import java.util.zip.ZipInputStream

@MicronautTest
class CreateControllerSpec extends Specification {

    @Inject
    CreateClient client

    void "test default create app command"() {
        when:
        def bytes = client.createApp("test", Collections.emptyList(), null, null, null)

        then:
        ZipUtil.isZip(bytes)

    }

    void "test default create app with feature"() {
        when:
        def bytes = client.createApp("test", ['graalvm'], null, null, null)

        then:
        ZipUtil.containsFile(bytes, "native-image.properties")
    }

    void "test create app with kotlin"() {
        when:
        def bytes = client.createApp("test", ['graalvm'], null, null, Language.kotlin)

        then:
        ZipUtil.containsFile(bytes, "Application.kt")
    }

    void "test create app with groovy"() {
        when:
        def bytes = client.createApp("test", ['graalvm'], null, null, Language.groovy)

        then:
        ZipUtil.containsFile(bytes, "Application.groovy")
    }

    void "test create app with maven"() {
        when:
        def bytes = client.createApp("test", ['graalvm'], BuildTool.maven, null, Language.groovy)

        then:
        ZipUtil.containsFile(bytes, "pom.xml")
    }

    void "test create app with spock"() {
        when:
        def bytes = client.createApp("test", ['graalvm'], BuildTool.gradle, TestFramework.spock, Language.groovy)

        then:
        ZipUtil.containsFileWithContents(bytes, "build.gradle", "spock-core")
    }


    @Client('/create')
    static interface CreateClient {
        @Get(uri = "/app/{name}{?features,build,test,lang}", consumes = "application/zip")
        byte[] createApp(
                String name,
                @Nullable List<String> features,
                @Nullable BuildTool build,
                @Nullable TestFramework test,
                @Nullable Language lang
        );
    }
}
