package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should return error when sending message without Content-Type"
    request {
        method POST()
        url '/kafka/messages/send'
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
        body('Message')
    }
    response {
        status 415
    }
}
