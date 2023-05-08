package com.denizd.textbasic.fragment

import android.os.Bundle
import com.denizd.textbasic.R
import com.google.android.material.transition.MaterialSharedAxis

class GuideFragment : BaseFragment(R.layout.fragment_guide) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }
}