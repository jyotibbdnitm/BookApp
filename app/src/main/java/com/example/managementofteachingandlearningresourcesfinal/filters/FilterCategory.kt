package com.example.managementofteachingandlearningresourcesfinal.filters

import android.widget.Filter
import com.example.managementofteachingandlearningresourcesfinal.adapters.AdapterCategory
import com.example.managementofteachingandlearningresourcesfinal.models.ModelCategory

class FilterCategory: Filter{
    private  var filterlist: ArrayList<ModelCategory>

    private var adapterCategory: AdapterCategory

    constructor(filterlist: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterlist = filterlist
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
       var constraint =constraint
        val results = FilterResults()

       if (constraint !=null && constraint.isNotEmpty()){
           constraint = constraint.toString().uppercase()
           val filteredModel:ArrayList<ModelCategory> = ArrayList()
           for (i in 0 until filterlist.size){
               if(filterlist[i].category.uppercase().contains(constraint)){
                   filteredModel.add(filterlist[i])
               }
           }
           results.count = filteredModel.size
           results.values = filteredModel
       }
        else{
            results.count = filterlist.size
           results.values = filterlist
       }
        return results

    }

    override fun publishResults(constraint:CharSequence?, results:  FilterResults) {
        adapterCategory.categoryArrayList =  results.values as ArrayList<ModelCategory>

        adapterCategory.notifyDataSetChanged()
    }
}