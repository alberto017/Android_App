package com.example.elias.agenda_prestamos;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        /**INDICAR TITULO Y SUBTITULO**/
        actionBar.setTitle("Agenda Prestamos");
        actionBar.setSubtitle("Administra tus prestamos de forma adecuada");

        /**MODO TABS EN ACTIONBAR**/
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        /**CREAR TABS**/
        ActionBar.Tab tab = actionBar.newTab()
                .setText("Registrar")
                .setTabListener(new TabsListener(
                        this, "Registrar Préstamo", Pestaña_Registro.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Pendientes")
                .setTabListener(new TabsListener(
                        this, "Consultar Préstamos Pendientes", Pestaña_Consulta_Pendientes.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText("Devueltos")
                .setTabListener(new TabsListener(
                        this, "Consultar Préstamos Devueltos", Pestaña_Consulta_Devueltos.class));
        actionBar.addTab(tab);
    }

    public class TabsListener  implements ActionBar.TabListener {

        private Fragment fragment;
        private final String tag;

        public TabsListener(Activity activity, String tag, Class cls) {
            this.tag = tag;
            fragment = Fragment.instantiate(activity, cls.getName());
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(android.R.id.content, fragment, tag);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
    }
}
