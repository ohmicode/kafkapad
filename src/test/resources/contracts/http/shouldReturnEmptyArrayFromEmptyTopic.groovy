package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should successfully return an empty array"
    request {
        method GET()
        url("/kafka/messages/first") {
            queryParameters {
                parameter("topic", "empty.topic")
                parameter("count", "5")
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
    }
    response {
        body("[]")
        status 200
    }
}
