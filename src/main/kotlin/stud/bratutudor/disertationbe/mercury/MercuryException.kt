package stud.bratutudor.disertationbe.mercury

sealed class MercuryException {
    class ApiTimeout(sal: String) : MercuryException()
    class Mbape : MercuryException()
}

val sal = MercuryException.ApiTimeout("ha")