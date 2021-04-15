package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should successfully send message when Content-Type is text/plain"
    request {
        method POST()
        url('/kafka/messages/send') {
            queryParameters {
                parameter('topic', 'any.topic')
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
        body('Message sent')
        status 200
    }
}
