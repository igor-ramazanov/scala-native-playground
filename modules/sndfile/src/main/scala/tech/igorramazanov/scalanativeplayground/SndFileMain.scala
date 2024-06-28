package tech.igorramazanov.scalanativeplayground

import java.nio.charset.StandardCharsets
import scala.scalanative.unsafe.CUnsignedInt
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsafe.Zone
import scala.scalanative.unsafe.fromCString
import scala.scalanative.unsafe.toCString
import tech.igorramazanov.scalanativeplayground.sndfile.SF_INFO
import tech.igorramazanov.scalanativeplayground.sndfile.SF_INFO.*
import tech.igorramazanov.scalanativeplayground.sndfile.SNDFILE

object SndFileMain:
  def main(args: Array[String]): Unit = Zone(sndfileExample(args.head))

  def sndfileExample(file_path: String)(using Zone): Unit =
    val sf_info: Ptr[SF_INFO] = sndfile.SF_INFO()
    val path: Ptr[Byte] = toCString(file_path, StandardCharsets.UTF_8)
    val file: Ptr[SNDFILE] = sndfile
      .sf_open(path, sndfile.SFM_READ.toInt, sf_info)

    println(s"Channels: ${(!sf_info).channels}")
    println(s"Format: ${(!sf_info).format}")
    println(s"Frames: ${(!sf_info).frames}")
    println(s"Sample rate: ${(!sf_info).samplerate}")
    println(s"Sections: ${(!sf_info).sections}")
    println(s"Seekable: ${(!sf_info).seekable}")

    read(file, sndfile.SF_STR_ALBUM, "Album")
    read(file, sndfile.SF_STR_ARTIST, "Artist")
    read(file, sndfile.SF_STR_COMMENT, "Comment")
    read(file, sndfile.SF_STR_COPYRIGHT, "Copyright")
    read(file, sndfile.SF_STR_DATE, "Date")
    read(file, sndfile.SF_STR_GENRE, "Genre")
    read(file, sndfile.SF_STR_LICENSE, "License")
    read(file, sndfile.SF_STR_SOFTWARE, "Software")
    read(file, sndfile.SF_STR_TITLE, "Title")
    read(file, sndfile.SF_STR_TRACKNUMBER, "Tracknumber")

    val result = sndfile.sf_close(file)

    if result != 0 then println("Failed to close the file: ${file_path}")

  def read(file: Ptr[SNDFILE], str_type: CUnsignedInt, str_name: String): Unit =
    val c_string = sndfile.sf_get_string(file, str_type.toInt)
    val str_value = fromCString(c_string, StandardCharsets.UTF_8)
    println(s"$str_name: $str_value")
