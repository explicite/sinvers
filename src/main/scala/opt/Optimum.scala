package opt

abstract class Optimum(val inf: Double)

case object MIN extends Optimum(Double.PositiveInfinity)

case object MAX extends Optimum(Double.NegativeInfinity)
