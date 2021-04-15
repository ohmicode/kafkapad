package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should not send message with wrong password"
    request {
        method POST()
        url('/kafka/messages/send') {
            queryParameters {
                parameter('topic', 'any.topic')
            }
        }
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'WrongPassword'
            header 'Content-Type': 'text/plain'
        }
        body('Message')
    }
    response {
        status 401
    }
}
