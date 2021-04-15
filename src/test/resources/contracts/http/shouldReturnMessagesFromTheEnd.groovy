package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should successfully return messages"
    request {
        method GET()
        url("/kafka/messages/last") {
            queryParameters {
                parameter("topic", "three.messages.topic")
                parameter("count", "3")
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
    }
    response {
        body('[{"timestamp":42,"timestampType":"creationTime","headers":[],"partition":0,"offset":0,"value":"message0"},{"timestamp":42,"timestampType":"creationTime","headers":[],"partition":0,"offset":1,"value":"message1"},{"timestamp":42,"timestampType":"creationTime","headers":[],"partition":0,"offset":2,"value":"message2"}]')
        status 200
    }
}
