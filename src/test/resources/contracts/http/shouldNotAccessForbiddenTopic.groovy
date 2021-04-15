package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should not access forbidden topic"
    request {
        method GET()
        url("/kafka/messages/last") {
            queryParameters {
                parameter("topic", "forbidden.topic")
                parameter("count", "3")
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
    }
    response {
        status 403
    }
}
