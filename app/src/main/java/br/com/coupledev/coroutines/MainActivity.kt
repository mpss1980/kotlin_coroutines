package br.com.coupledev.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.com.coupledev.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                val answer1 = async { networkCall() }
                val answer2 = async { networkCall2() }
                Log.d(TAG, "Answer1 is ${answer1.await()}")
                Log.d(TAG, "Answer2 is ${answer2.await()}")
            }
            Log.d(TAG, "Request took $time ms.")
        }

        GlobalScope.launch(Dispatchers.IO) {
            val result = doNetworkCall()
            withContext(Dispatchers.Main) {
                binding.tvDummy.text = result
            }
        }

        runBlocking {
            launch(Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Finished IO Coroutine 1")
            }
            launch(Dispatchers.IO) {
                delay(5000L)
                Log.d(TAG, "Finished IO Coroutine 2")
            }
            Log.d(TAG, "Start runBlocking")
            delay(5000L)
            Log.d(TAG, "End runBlocking")
        }

        val job = GlobalScope.launch(Dispatchers.Default) {
            repeat(5) {
                Log.d(TAG, "Coroutine is still working...")
                delay(1000L)
            }
        }

        runBlocking {
            job.join()
            Log.d(TAG, "Main thread is continuing...")
        }

        val job2 = GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "Starting a long calculation...")
            for (i in 30..40) {
                if (isActive) {
                    Log.d(TAG, "Result for i = $i: ${fib(i)}")
                }
            }
            Log.d(TAG, "Ending a long calculation...")
        }

        runBlocking {
            delay(2000L)
            job2.cancel()
            Log.d(TAG, "Canceld a job!")
        }

        GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG, "Starting a long calculation2...")
            withTimeout(3000L) {
                for (i in 30..40) {
                    if (isActive) {
                        Log.d(TAG, "Result2 for i = $i: ${fib(i)}")
                    }
                }
            }
            Log.d(TAG, "Ending a long calculation2...")
        }
    }

    private suspend fun doNetworkCall(): String {
        delay(3000L)
        return "This is the answer"
    }

    private fun fib(n: Int): Long {
        return when (n) {
            0 -> 0
            1 -> 1
            else -> fib(n - 1) + fib(n - 2)
        }
    }

    private suspend fun networkCall(): String {
        delay(3000L)
        return "Answer 1"
    }

    private suspend fun networkCall2(): String {
        delay(3000L)
        return "Answer 2"
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}