package org.livingdoc.example

class CoffeeMachine {
    fun getCoffee(): Boolean {
        return (deposit > 0.0f && leftCoffees > 0)
    }

    fun depositMoney(amount: Float): Boolean {
        if (!triggered) {
            deposit += amount
            return true
        } else {
            return false
        }
    }

    fun setLeftCoffees(amount: Int) {
        leftCoffees = amount
    }

    var triggered = false
    private var deposit: Float = 0.0f
    private var leftCoffees: Int = 0
}
