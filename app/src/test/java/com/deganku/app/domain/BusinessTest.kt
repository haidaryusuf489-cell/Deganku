package com.deganku.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BusinessTest {
    @Test
    fun cashFormula() {
        assertEquals(700000, ShiftEngine.expectedCash(500000, 300000, 50000, 50000))
    }

    @Test
    fun variance() {
        assertEquals(-10000, ShiftEngine.variance(500000, 490000))
    }

    @Test
    fun permission() {
        assertFalse(PermissionEngine.allowed("CASHIER", PermissionEngine.RECIPE_EDIT))
    }
}
