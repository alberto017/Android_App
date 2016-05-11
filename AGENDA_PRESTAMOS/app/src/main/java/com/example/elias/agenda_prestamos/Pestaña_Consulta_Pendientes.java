package com.example.elias.agenda_prestamos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Elias on 10/05/16.
 */
public class Pestaña_Consulta_Pendientes extends Fragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fm_consulta_pendientes, container, false);
        return rootView;
    }
}
