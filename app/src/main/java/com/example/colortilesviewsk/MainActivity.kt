package com.example.colortilesviewsk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.colortilesviewsk.databinding.ActivityMainBinding
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val cells: MutableList<MutableList<Cell>> = mutableListOf()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillCells()
        updateCellField()
        binding.cellGameView.onCellClick = {
            onCellClick(it)
        }
    }

    private fun fillCells() {
        for (j in 0 until 4) {
            val list = mutableListOf<Cell>()
            for (i in 0 until 4) {
                list.add(Cell(Random.nextBoolean(), i, j))
            }
            cells.add(list)
        }
    }

    private fun checkVictory() {
        if (cells.all { it.all { cell -> cell.isActive } } || cells.all { it.all { cell -> !cell.isActive } }) {
            Toast.makeText(this, "Victory", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCellClick(cell: Cell) {
        for (j in 0 until 4) {
            for (i in 0 until 4) {
                if (j == cell.y || i == cell.x) {
                    cells[j][i] = cells[j][i].copy(isActive = cells[j][i].isActive.not())
                }
            }
        }
        updateCellField()
        checkVictory()
    }

    private fun updateCellField() {
        binding.cellGameView.items = cells
    }
}