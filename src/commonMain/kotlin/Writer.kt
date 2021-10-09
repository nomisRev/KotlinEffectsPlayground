import arrow.continuations.generic.AtomicRef
import arrow.continuations.generic.update
import arrow.typeclasses.Semigroup

interface WriterEffect<A> {
  /** Write [a] to writer */
  suspend fun tell(a: A): Unit

  /**
   * Read current written values of [A].
   * Returns null when the writer didn't receive any values of [A] through [tell].
   */
  suspend fun written(): A?
}

public suspend fun <A, B> writer(
  semigroup: Semigroup<A>,
  f: suspend WriterEffect<A>.() -> B
): Pair<A?, B> {
  val effect = WriterEf(semigroup)
  val b = f(effect)
  return Pair(effect.result(), b)
}

private class WriterEf<A>(
  semigroup: Semigroup<A>
) : WriterEffect<A>, Semigroup<A> by semigroup {
  private val value = AtomicRef<Any?>(EmptyValue)
  override suspend fun tell(a: A) =
    value.update { original ->
      if (original === EmptyValue) a
      else (original as A).combine(a)
    }

  override suspend fun written(): A? = result()

  fun result(): A? = EmptyValue.unbox(value.get())
}

suspend fun pingService1(): Unit = println("Ping service 1")
suspend fun pingService2(): Unit = println("Ping service 2")
suspend fun pingService3(): Unit = println("Ping service 3")
suspend fun pingService4(): Unit = println("Ping service 4")

public suspend fun writerExample(): Unit {
  val (writerRes, b) = writer(Semigroup.string()) {
    pingService1()
    tell("Service 1")
    pingService2()
    tell("Service 2")
    pingService3()
    tell("Service 3")
    pingService4()
    tell("Service 4")
  }

  println("Writer finished")
  println("Output writer: $writerRes")
  println("Output program: $b")
}
