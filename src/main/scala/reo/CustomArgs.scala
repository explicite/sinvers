package reo

case class CustomArgs(n: Double = 0.25,
    m: Double = 0.1) {
  val beta = 2E2
  val eb0 = 1E-3
  val K = 1.7E2

  def formatter(d: Double): String = new java.text.DecimalFormat("0.##############E0").format(d)

  override def toString: String = {
    s"Thermoecroui : USERPUISSANCE,\nbeta=${formatter(beta)},\neb0=${formatter(eb0)},\nn=${formatter(n)}\nK=${formatter(K)}\nm=${formatter(m)}\n"
  }
}

object CustomArgs {
  def apply(args: Seq[Double]): CustomArgs = {
    assert(args.size == 2, "not satisfied numbers of arguments")
    CustomArgs(args.head, args(1))
  }
}
