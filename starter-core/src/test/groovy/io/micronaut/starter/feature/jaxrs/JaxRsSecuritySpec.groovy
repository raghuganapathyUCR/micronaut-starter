package io.micronaut.starter.feature.jaxrs

import io.micronaut.starter.ApplicationContextSpec
import io.micronaut.starter.BuildBuilder
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.BuildTool
import io.micronaut.starter.options.Language
import spock.lang.Unroll

class JaxRsSecuritySpec extends ApplicationContextSpec implements CommandOutputFixture {

    void 'test readme.md with feature jax-rs-security contains links to micronaut docs'() {
        when:
        def output = generate(['security', JaxRs.NAME])
        def readme = output["README.md"]

        then:
        readme
        readme.contains("https://micronaut-projects.github.io/micronaut-jaxrs/latest/guide/index.html")
    }

    @Unroll
    void 'test jax-rs-security with Gradle for language=#language'() {
        when:
        String template = new BuildBuilder(beanContext, BuildTool.GRADLE)
                .features(['security', JaxRs.NAME, 'kapt'])
                .language(language)
                .render()

        then:
        template.contains('implementation("io.micronaut.jaxrs:micronaut-jaxrs-server-security")')
        template.contains('implementation("io.micronaut.jaxrs:micronaut-jaxrs-server")')
        template.contains("$scope(\"io.micronaut.jaxrs:micronaut-jaxrs-processor\")")

        where:
        language        | scope
        Language.JAVA   | "annotationProcessor"
        Language.KOTLIN | "kapt"
        Language.GROOVY | "compileOnly"
    }

    void 'test jax-rs-security is not with Gradle if none SecurityFeature selected'() {
        when:
        String template = new BuildBuilder(beanContext, BuildTool.GRADLE)
                .features([JaxRs.NAME])
                .language(Language.JAVA)
                .render()

        then:
        !template.contains('implementation("io.micronaut.jaxrs:micronaut-jaxrs-server-security")')
    }

    void 'test maven jax-rs-security feature'() {
        when:
        String template = new BuildBuilder(beanContext, BuildTool.MAVEN)
                .features(['security', JaxRs.NAME])
                .render()

        then:
        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server-security</artifactId>
      <scope>compile</scope>
    </dependency>
""")

        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server</artifactId>
      <scope>compile</scope>
    </dependency>
""")
        template.contains("""
            <path>
              <groupId>io.micronaut.jaxrs</groupId>
              <artifactId>micronaut-jaxrs-processor</artifactId>
              <version>\${micronaut.jaxrs.version}</version>
              <exclusions>
                <exclusion>
                  <groupId>io.micronaut</groupId>
                  <artifactId>micronaut-inject</artifactId>
                </exclusion>
              </exclusions>
            </path>
""")

        when:
        template = new BuildBuilder(beanContext, BuildTool.MAVEN)
                .language(Language.KOTLIN)
                .features(['security', JaxRs.NAME])
                .render()

        then:
        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server-security</artifactId>
      <scope>compile</scope>
    </dependency>
""")

        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server</artifactId>
      <scope>compile</scope>
    </dependency>
""")
        template.count('''\
               <annotationProcessorPath>
                 <groupId>io.micronaut.jaxrs</groupId>
                 <artifactId>micronaut-jaxrs-processor</artifactId>
                 <version>${micronaut.jaxrs.version}</version>
               </annotationProcessorPath>
''') == 2

        when:
        template = new BuildBuilder(beanContext, BuildTool.MAVEN)
                .language(Language.GROOVY)
                .features(['security', JaxRs.NAME])
                .render()

        then:
        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server-security</artifactId>
      <scope>compile</scope>
    </dependency>
""")

        template.contains("""
    <dependency>
      <groupId>io.micronaut.jaxrs</groupId>
      <artifactId>micronaut-jaxrs-server</artifactId>
      <scope>compile</scope>
    </dependency>
""")
    }
}
