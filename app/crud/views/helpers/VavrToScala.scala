package crud.views.helpers

import java.util.function.Supplier

/**
  * Provides implicit conversions for Vavr.io data types to Scala types.
  */
object VavrToScala {

  /**
    * Import this class to get an `asScala` method on vavr.io `Option` objects, which converts those to Scala `Option`s.
    *
    * @param o The Vavr.io Option object
    * @tparam T Content type of the Option
    */
  implicit class VavrOptionToScalaOption[T >: Null](val o: io.vavr.control.Option[T]) {

    def asScala: Option[T] = Option(o.getOrElse(new Supplier[T] {
      override def get(): T = null
    }))

  }

}
