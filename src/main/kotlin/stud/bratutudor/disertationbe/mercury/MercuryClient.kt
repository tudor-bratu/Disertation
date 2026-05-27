package stud.bratutudor.disertationbe.mercury

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MercuryClient(
    @Value("\${app.mercury.apikey}") private val apiKey: String,
    @Value("\${}") private val apiKey: String
)