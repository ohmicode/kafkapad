package http

import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should successfully return topics"
    request {
        method GET()
        url '/kafka/topics'
        headers {
            header 'login': 'CorrectLogin'
            header 'password': 'CorrectPassword'
        }
    }
    response {
        body('[{"name":"three.messages.topic","partitions":[{"partition":0,"leader":{"id":0,"host":"127.0.0.1","port":7777,"rack":null},"replicas":[{"id":0,"host":"127.0.0.1","port":7777,"rack":null}],"inSyncReplicas":[{"id":0,"host":"127.0.0.1","port":7777,"rack":null}],"offlineReplicas":[]}]},{"name":"empty.topic","partitions":[{"partition":0,"leader":{"id":0,"host":"127.0.0.1","port":7777,"rack":null},"replicas":[{"id":0,"host":"127.0.0.1","port":7777,"rack":null}],"inSyncReplicas":[{"id":0,"host":"127.0.0.1","port":7777,"rack":null}],"offlineReplicas":[]}]}]')
        status 200
    }
}
