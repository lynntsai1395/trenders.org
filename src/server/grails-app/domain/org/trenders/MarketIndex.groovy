package org.trenders

import java.text.NumberFormat

class MarketIndex {
    def title
    def percentChange
    def currentValue
    def formatter = NumberFormat.getInstance(new Locale("en_US")) // yuck
    
    def getCurrentValue() {
        formatter.format(currentValue.toDouble())
    }
    
    def isUp() {
        ! percentChange.startsWith("-")
    }
    
    def getPercentChangeText() {
        percentChange
    }
}
