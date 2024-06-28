package tech.igorramazanov.scalanativeplayground

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*
import tech.igorramazanov.scalanativeplayground.magickwand as m

object MagickWandMain:
  def main(args: Array[String]): Unit = Zone(magickWandExample())

  private def magickWandExample()(using Zone): Unit =
    m.MagickWandGenesis()

    val magickWand: Ptr[m.MagickWand] = m.NewMagickWand()
    val pixelWand: Ptr[m.PixelWand] = m.NewPixelWand()

    m.PixelSetColor(pixelWand, c"blue").verify()

    m.MagickReadImage(magickWand, toCString("logo:")).verify()

    val width: USize = m.MagickGetImageWidth(magickWand)
    val height: USize = m.MagickGetImageHeight(magickWand)

    m.MagickSetImageBackgroundColor(magickWand, pixelWand).verify()

    m.MagickExtentImage(
        magickWand,
        1024.toUSize,
        768.toUSize,
        (-(1024 - width.toInt) / 2).toSize,
        (-(768 - height.toInt) / 2).toSize,
      )
      .verify()

    m.MagickWriteImage(magickWand, c"logo-extend.png").verify()

    val _ = m.DestroyMagickWand(magickWand)
    val _ = m.DestroyPixelWand(pixelWand)

    m.MagickWandTerminus()

  extension (b: m.MagickBooleanType)
    def verify() = assert(b == m.MagickBooleanType.MagickTrue)
