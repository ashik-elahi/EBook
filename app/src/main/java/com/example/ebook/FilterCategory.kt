package com.example.ebook

import android.widget.Filter


class FilterCategory: Filter {

    // arraylist in which we want to search
    private var filterList: ArrayList<ModelCategory>

    // adapter in which filter need to be implemented
    private var adapterCategory: AdapterCategory

    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory): super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        // value should not be null and empty
        if (constraint!= null && constraint.isNotEmpty()){
            constraint = constraint.toString().uppercase()
            val filterModels: ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size){
                if (filterList[i].category.uppercase().contains(constraint)){
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterModels.size
            results.values = filterModels
        }
        else {
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        // notify changes
        adapterCategory.notifyDataSetChanged()
    }
}