
/**
 * This is a work-around for having nested nulls in generic code.
 * This allows for writing faster generic code instead of using `Option`.
 * This is only used as an optimisation technique in low-level code,
 * always prefer to use `Option` in actual business code when needed in generic code.
 */
@PublishedApi
internal object EmptyValue {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <A> unbox(value: Any?): A =
    if (value === this) null as A else value as A
}
