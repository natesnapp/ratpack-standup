package standup

import groovy.json.JsonSlurper
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import spock.lang.AutoCleanup
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson

class StandupApiSpec extends Specification {
    @AutoCleanup
    def aut = new GroovyRatpackMainApplicationUnderTest()

    JsonSlurper jsonSlurper = new JsonSlurper()

    void "should add and a return status"() {
        given:
        Status newStatus = new Status(
                name: 'Craig Atkinson',
                yesterday: 'Finished a task',
                today: 'Starting a new task'
        )

        when:
        def postResponse = aut.httpClient.requestSpec { spec ->
            spec.body { b ->
                b.text(toJson(newStatus))
            }
        }.post("api/status")

        then:
        assert postResponse.statusCode == 200

        when:
        def getAllResponse = aut.httpClient.get('api/status/all')

        then:
        assert getAllResponse.statusCode == 200

        when:
        def parsedJson = jsonSlurper.parseText(getAllResponse.body.text)

        List<Status> allStatusList = parsedJson as List

        then:
        assert allStatusList.size() == 1

        assert allStatusList[0].name == 'Craig Atkinson'
        assert allStatusList[0].yesterday == newStatus.yesterday
        assert allStatusList[0].today == newStatus.today
    }
}
