package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should not access forbidden topic"
    request {
        method POST()
        url('/kafka/messages/send') {
            queryParameters {
                parameter("topic", "forbidden.topic")
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
            header 'Content-Type': 'text/plain'
        }
        body('Message')
    }
    response {
        status 403
    }
}
