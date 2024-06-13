package com.example.aplicationpaw.views.ui.dialogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.aplicationpaw.R
import com.google.android.gms.maps.model.LatLng

class RequestDialogFragment : DialogFragment() {

    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private var listener: RequestDialogListener? = null

    interface RequestDialogListener {
        fun onPriceEntered(price: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startLatLng = it.getParcelable(ARG_START_LATLNG)
            endLatLng = it.getParcelable(ARG_END_LATLNG)
        }
        listener = targetFragment as? RequestDialogListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_dialog, container, false)
        val priceEditText: EditText = view.findViewById(R.id.priceEditText)
        val submitButton: Button = view.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val price = priceEditText.text.toString().trim()
            if (price.isNotEmpty()) {
                listener?.onPriceEntered(price)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Ingrese un precio", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        private const val ARG_START_LATLNG = "start_latlng"
        private const val ARG_END_LATLNG = "end_latlng"

        fun newInstance(startLatLng: LatLng, endLatLng: LatLng): RequestDialogFragment {
            val fragment = RequestDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_START_LATLNG, startLatLng)
            args.putParcelable(ARG_END_LATLNG, endLatLng)
            fragment.arguments = args
            return fragment
        }
    }
}

