package io

import java.io.{File, PrintWriter}

import reo.{CustomArgs, HSArgs}

case class DON(file: File) {
  def name = file.getName

  def workingDirectory = file.getParent

  var hsArgs: HSArgs =
    HSArgs(
      1271649,
      -0.00254,
      -0.05621,
      0.1455,
      -0.0324,
      0,
      0,
      0,
      0,
      0,
      0
    )

  var customArgs  = CustomArgs(0.25, 0.1)

  def updateHS(hsArgs: HSArgs): Unit = {
    this.hsArgs = hsArgs

    val content = s".FICHIER\nFOUT = file.out\nFMAY = work.may\nDelete\n.FIN FICHIER\n.UNITES\nmm-mpa-mm.kg.s\n.FIN UNITES\n.INCREMENT\nCalage\n.FIN INCREMENT\n.RHEOLOGIE\n$hsArgs Coeff Poisson = 3.000000e-001\nModule Young = 2.000000e+008\nTemp Init = 1000.00000\nGravity\nInertie\nOutil 0: Coulomb,\nmu = 0.120000\n.FIN RHEOLOGIE\n.THERMIQUE\nMVolumique = 7.800000e-006\nCmassique = 7.000000e+008\nConductmat = 2.300000e+004\nOutil 0\nalphat = 2.000000e+003\ntempout = 1000.000000\neffusoutil = 1.176362e+004\nFace libre\nalphat = 1.000000e+001\ntempext = 1000.000000\nepsilon = 8.800000e-001\n.FIN THERMIQUE\n.PILOTAGE\nFile = pilotage.dat,\nhauteur actuelle = 12.001,\nhauteur finale = 7.522\n.FIN PILOTAGE\n.EXECUTION\nSans Visualisation\n.FIN EXECUTION"
    val writer = new PrintWriter(file)
    try {
      writer.write(content)
    }catch {
      case e: Exception =>
        updateHS(hsArgs)
    }
    writer.flush()
    writer.close()
  }

  def updateCustom(customArgs: CustomArgs): Unit = {
    this.customArgs = customArgs

    val content = s".FICHIER\nFOUT = file.out\nFMAY = work.may\nDelete\n.FIN FICHIER\n.UNITES\nmm-mpa-mm.kg.s\n.FIN UNITES\n.RHEOLOGIE\n$customArgs Coeff Poisson = 3.000000e-001\nModule Young = 2.000000e+008\nTemp Init = 1000.00000\nOutil 0: Coulomb,\nmu = 0.120000\n.FIN RHEOLOGIE\n.THERMIQUE\nMVolumique = 7.800000e-006\nCmassique = 7.000000e+008\nConductmat = 2.300000e+004\nOutil 0\nalphat = 2.000000e+003\ntempout = 1000.000000\neffusoutil = 1.176362e+004\nFace libre\nalphat = 1.000000e+001\ntempext = 1000.000000\nepsilon = 8.800000e-001\n.FIN THERMIQUE\n.MAUTO\nautosize\n.FIN MAUTO\n.EXECUTION\ndhsto = 1.000000\nSans Visualisation\n.FIN EXECUTION"
    val writer = new PrintWriter(file)
    try {
      writer.write(content)
    }catch {
      case e: Exception =>
        updateCustom(customArgs)
    }
    writer.flush()
    writer.close()
  }

  def refresh() = {
    updateHS(this.hsArgs)
    //updateCustom(this.customArgs)
  }

  def files(mesh: String = "work.may", tooling: String = "file.out"): String = {
    s".FICHIER\nFMAY=$mesh\nFOUT=$tooling\nDelete\n.FIN FICHIER\n"
  }

  def unites(unites: String = "mm-kpa-mm.kg.s"): String = {
    s".UNITES\n$unites\n.FIN UNITES\n"
  }

  def increment(adjustment: String = "Calage"): String = {
    s".INCREMENT\n$adjustment\n.FIN INCREMENT\n"
  }

  def rheology(hsArgs: HSArgs,
               poisson: String = "3.000000e-001",
               young: String = "2.000000e+008",
               temInit: String = "1000.00000",
               frictionLaw: String,
               other: Seq[String] = Seq("Gravity", "Inerite")): String = {
    s".RHEOLOGIE\n$hsArgs\nCoeff Poisson=$poisson\nModule Young=$young\nTemp Init=$temInit\n${other.mkString("\n")}$frictionLaw"
  }


  def thermal(partConditions: PartConditionsArgs,
              partDieConditions: PartDieConditionsArgs,
              partExternalConditions: PartExternalConditionsArgs): String = {
    s".THERMIQUE\n$partConditions$partDieConditions$partExternalConditions.FIN THERMIQUE\n"
  }

  def pilotage(file: String = "pilotage.data",
               min: String = "12.00001",
               max: String = "7.522"): String = {
    s".PILOTAGE\nFile=$file,\nhauteur actuelle=$min,\nhauteur finale=$max\n.FIN PILOTAGE\n"
  }

  def execution(args: Seq[String] = Seq("Dt constant", "Sans Visualisation")): String = {
    s".EXECUTION\n${args.mkString("\n")}.FIN EXECUTION\n"
  }

  def remeshing(args: Seq[String] = Seq("Fine Mesh")): String = {
    s".MAUTO\n${args.mkString("\n")}.FIN MAUTO"
  }
}

case class ColumbFrictionLawArgs(mu: String = "0.120000") {
  override def toString: String = {
    s"Outil 0 : Coulomb,\nmu=$mu\n"
  }
}

case class PartConditionsArgs(densityRho: String = "7.800000e-006",
                              heatCapacity: String = "7.000000e+008",
                              conductivity: String = "2.300000e+004") {
  override def toString: String = {
    s"MVolumique=$densityRho\nCmassique=$heatCapacity\nConductmat=$conductivity\n"
  }
}

case class PartDieConditionsArgs(transfer: String = "2.000000e+003",
                                 temperature: String = "20.000000",
                                 effusivity: String = "1.176362e+004") {
  override def toString: String = {
    s"Outil 0\nalphat=$transfer\ntempout=$temperature\neffusoutil=$effusivity\n"
  }
}

case class PartExternalConditionsArgs(airCoefficient: String = "1.000000e+001",
                                      airTemp: String = "20.000000",
                                      emissivite: String = "8.800000e-001") {
  override def toString: String = {
    s"Face libre\nalphat=$airCoefficient\ntempext=$airTemp\nepsilon=$emissivite\n"
  }
}
