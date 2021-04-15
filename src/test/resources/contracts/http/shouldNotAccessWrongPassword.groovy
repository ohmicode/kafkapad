package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should not access wrong password"
    request {
        method GET()
        url '/kafka/topics'
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'WrongPassword'
        }
    }
    response {
        status 401
    }
}
