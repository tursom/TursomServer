package cn.tursom.database.wrapper

interface AbstractWrapper<T, Children : AbstractWrapper<T, Children>> :
  Wrapper<T>,
  Compare<Children, T>,
  Nested<Children, Children>,
  Join<Children>,
  Func<Children, T>