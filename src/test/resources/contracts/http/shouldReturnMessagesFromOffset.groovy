package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should successfully return messages"
    request {
        method GET()
        url("/kafka/messages/direct") {
            queryParameters {
                parameter("topic", "three.messages.topic")
                parameter("offset", "1")
                parameter("count", "1")
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
    }
    response {
        body('[{"timestamp":42,"timestampType":"creationTime","headers":[],"partition":0,"offset":1,"value":"message1"}]')
        status 200
    }
}
