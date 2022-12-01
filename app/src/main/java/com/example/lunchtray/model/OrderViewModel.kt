/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    // Map of menu items
    val menuItems = DataSource.menuItems

    // Default values for item prices
    private var previousEntreePrice = 0.0
    private var previousSidePrice = 0.0
    private var previousAccompanimentPrice = 0.0

    // Default tax rate
    private val taxRate = 0.08

    // Entree for the order
    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree

    // Side for the order
    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side

    // Accompaniment for the order.
    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment

    // Subtotal for the order
    // Note that the next 3 LiveData variables are formatted to display currency
    private val _subtotal = MutableLiveData(0.0)
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Total cost of the order
    private val _total = MutableLiveData(0.0)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // Tax for the order
    private val _tax = MutableLiveData(0.0)
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set the entree for the order.
     */
    fun setEntree(entree: String)
    {
        // TODO: if _entree.value is not null, set the previous entree price to the current
        //  entree price.
        //If the _entree is not null (i.e. the user already selected an entree, but changed their
        // choice), set the previousEntreePrice to the current _entree's price.
        if(_entree.value != null)
        {
            previousEntreePrice = _entree.value!!.price
            // Error without double-bang on _entree.value(!!)
        }

        // TODO: if _subtotal.value is not null subtract the previous entree price from the current
        //  subtotal value. This ensures that we only charge for the currently selected entree.
        // If the _subtotal is not null, subtract the previousEntreePrice from the subtotal.
        if(_subtotal.value != null){
            _subtotal.value = _subtotal.value!! - previousEntreePrice
        }

        // TODO: set the current entree value to the menu item corresponding to the passed in string
        // Update the value of _entree to the entree passed into the function
        // (access the MenuItem using menuItems).
        _entree.value = menuItems.get(entree)
        // TODO: update the subtotal to reflect the price of the selected entree.
        // Call updateSubtotal(), passing in the newly selected entree's price.
        updateSubtotal(_entree.value!!.price)
    }

    /**
     * Set the side for the order.
     */
    fun setSide(side: String) {
        // TODO: if _side.value is not null, set the previous side price to the current side price.
        // If the _side is not null (i.e. the user already selected an side, but changed their
        // choice), set the previousSidePrice to the current _side's price.
        if(_side.value != null)
        {
            previousSidePrice = _side.value!!.price
        }
        // TODO: if _subtotal.value is not null subtract the previous side price from the current
        //  subtotal value. This ensures that we only charge for the currently selected side.
        // If the _subtotal is not null, subtract the previousSidePrice from the subtotal.
        if(_subtotal.value != null){
            _subtotal.value = _subtotal.value!! - previousSidePrice
        }

        // TODO: set the current side value to the menu item corresponding to the passed in string
        // Update the value of _side to the side passed into the function
        // (access the MenuItem using menuItems).
        _side.value = menuItems.get(side)
        // TODO: update the subtotal to reflect the price of the selected side.
        // Call updateSubtotal(), passing in the newly selected side's price.
        updateSubtotal(_side.value!!.price)
    }

    /**
     * Set the accompaniment for the order.
     */
    fun setAccompaniment(accompaniment: String) {
        // TODO: if _accompaniment.value is not null, set the previous accompaniment price to the
        //  current accompaniment price.
        // If the _side is not null (i.e. the user already selected an side, but changed their
        // choice), set the previousSidePrice to the current _side's price.
        if(_accompaniment.value != null)
        {
            previousAccompanimentPrice = _accompaniment.value!!.price
        }

        // TODO: if _accompaniment.value is not null subtract the previous accompaniment price from
        //  the current subtotal value. This ensures that we only charge for the currently selected
        //  accompaniment.
        // If the _subtotal is not null, subtract the previousAccompanimentPrice from the subtotal.
        if(_subtotal.value != null){
            _subtotal.value = _subtotal.value!! - previousAccompanimentPrice
        }

        // TODO: set the current accompaniment value to the menu item corresponding to the passed in
        //  string
        // Update the value of _side to the side passed into the function
        // (access the MenuItem using menuItems).
        _accompaniment.value = menuItems.get(accompaniment)
        // TODO: update the subtotal to reflect the price of the selected accompaniment.
        // Call updateSubtotal(), passing in the newly selected accompaniment's price.
        updateSubtotal(_accompaniment.value!!.price)
    }

    /**
     * Update subtotal value.
     */
    private fun updateSubtotal(itemPrice: Double) {
        // TODO: if _subtotal.value is not null, update it to reflect the price of the recently
        //  added item.
        // If _subtotal is not null, add the itemPrice to the _subtotal.
        // Seemed like a great place for an elvis operator, but since subtotal.value is liveData, it's a pain
        if(_subtotal.value != null)
        {
            _subtotal.value = _subtotal.value!! + itemPrice
            // again, needed double-bang to _subtotal.value to perform non-null asserted call
        }

        //  TODO: Otherwise, set _subtotal.value to equal the price of the item.
        // Otherwise, if _subtotal is null, set the _subtotal to the itemPrice.
        else{
            _subtotal.value = itemPrice
        }
        // TODO: calculate the tax and resulting total
        // After _subtotal has been set (or updated), call calculateTaxAndTotal() so that these
        // values are updated to reflect the new subtotal.
        calculateTaxAndTotal()
    }

    /**
     * Calculate tax and update total.
     */
    fun calculateTaxAndTotal() {
        // TODO: set _tax.value based on the subtotal and the tax rate.
        // Set the _tax equal to the tax rate times the subtotal.
        _tax.value = taxRate * _subtotal.value!!
        // TODO: set the total based on the subtotal and _tax.value.
        // Set the _total equal to the subtotal plus the tax.
        _total.value = _subtotal.value!! + _tax.value!!
    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        // TODO: Reset all values associated with an order
        // resetOrder() will be called when the user submits or cancels an order. You want to make
        // sure your app doesn't have any data left over when the user starts a new order.
        // Implement resetOrder() by setting all the variables that you modified in OrderViewModel
        // back to their original value (either 0.0 or null).
        // Should be done for all variables and values in this viewModel.  Tax rate seems like it
        // should stay the same, unless a UI controller tells us to change it based on a different
        // locale

        previousEntreePrice = 0.0
        previousSidePrice = 0.0
        previousAccompanimentPrice = 0.0
        _entree.value = null
        // entree, side, and accompaniment error with doubles.  These are of type menuItem so we
        // need to use null as the reset "value" for each
        _side.value = null
        _accompaniment.value = null
        // Still need to reset subtotal, total, and tax with numerical "values"
        _subtotal.value = 0.0
        _total.value = 0.0
        _tax.value = 0.0
        // After this work is completed, we move to the 4 layout files and implement data binding
        // Must complete TODOs in order to set text and click listeners  Don't forget the checkout.xml
    }
}



