package opt

case class NelderMead(f: (Seq[Double]) => Double,
                      p0: Seq[Double],
                      p1: Seq[Double],
                      p2: Seq[Double]) {

  /**
   *
   * @param α
   * @param β
   * @param γ coefficient of expansion greater than 2
   * @param δ
   * @param ε
   */
  def min(α: Double = 1.0,
          β: Double = 0.5,
          γ: Double = 2.0,
          δ: Double = 0.5,
          ε: Double = 0.01) = {

  }

  def reflection = ???
  def expansion = ???
  def narrowing = ???
  def reduction = ???

}
