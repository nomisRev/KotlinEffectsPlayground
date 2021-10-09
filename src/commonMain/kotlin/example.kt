import arrow.core.computations.EitherEffect
import arrow.core.computations.either
import arrow.core.right
import arrow.typeclasses.Semigroup

typealias EitherEff<E> = EitherEffect<E, Any?>

// TODO try to compile with multiple receivers branch
// context(WriterEffect<String>, WriterEffect<Int>, EitherEffect<Error>)
suspend fun <Ctx> Ctx.program(): String
// Emulate multiple receivers
  where Ctx : WriterEffect<String>,
        Ctx : EitherEff<Error> {
  val msg = "Hello"
  tell("Hello")
  val res = msg.right().bind()
  tell(", World!")
  return res
}

suspend fun compositionExample() {
  val (writer, res) = writer(Semigroup.string()) {
    either<Error, String> {
      // This will not be needed with the Multiple Receivers, you can simply call `program()`.
      val multipleReceiver = IntersectionTypeHack(this as EitherEff<Error>, this@writer)
      multipleReceiver.program()
    }
  }

  println("Program finished")
  println("Output writer: $writer")
  println("Output program: $res")
}

// Emulate what the compiler will automatically do with multiple receivers
class IntersectionTypeHack(
  val either: EitherEff<Error>,
  val writer: WriterEffect<String>
) : EitherEff<Error> by either, WriterEffect<String> by writer
