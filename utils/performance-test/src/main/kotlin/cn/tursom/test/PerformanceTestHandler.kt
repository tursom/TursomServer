package cn.tursom.test

interface PerformanceTestHandler : () -> Unit {
  fun logSchedule(percentage: Int) {
  }
}