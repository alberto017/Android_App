package com.example.elias.agenda_4;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {


    Button btnRegistrar, btn_fechaprestamo, btn_fechadevolucion;
    int año1, mes1, dia1, año2, mes2, dia2, dia_actual, mes_actual, año_actual;
    static final int DIALOG_ID = 0;
    static final int DIALOG_DEV = 1;
    ListView prestamosPendientes, prestamosDevueltos;
    TextView txt_fechaprestamo, txt_fechadevolucion;
    EditText fld_nombreobjeto, fld_prestadoa, fldtipo;
    ArrayList<String> al;
    ArrayList<String> a2;
    TabHost tabHost;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        prestamosPendientes = (ListView) findViewById(R.id.lst_verPendientes);
        prestamosDevueltos = (ListView) findViewById(R.id.lst_verRegresados);
        fld_nombreobjeto = (EditText) findViewById(R.id.fld_nombreobjeto);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        fld_prestadoa = (EditText) findViewById(R.id.fld_persona);
        txt_fechaprestamo = (TextView) findViewById(R.id.lblFechaPrIn);
        txt_fechadevolucion = (TextView) findViewById(R.id.lblFechaDevIn);
        fldtipo = (EditText) findViewById(R.id.field_tipo);



        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator("Registrar Préstamo");
        spec.setContent(R.id.tab1);
        tabHost.addTab(spec);

        tabHost.setup();
        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
        spec2.setIndicator("Préstamos Pendientes");
        spec2.setContent(R.id.tab2);
        tabHost.addTab(spec2);

        tabHost.setup();
        TabHost.TabSpec spec3 = tabHost.newTabSpec("tab3");
        spec3.setIndicator("Préstamos Devueltos");
        spec3.setContent(R.id.tab3);
        tabHost.addTab(spec3);



        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Iyari");
        actionBar.setSubtitle("Agenda de prestamos");


        db = openOrCreateDatabase("agenda_prestamos", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS prestamos(ID INTEGER PRIMARY KEY AUTOINCREMENT, prestado_a VARCHAR, nombre_objeto VARCHAR, fecha_prestamo date, fecha_devolucion date, tipo_objeto varchar );");
        db.execSQL("CREATE TABLE IF NOT EXISTS devueltos(ID_ INTEGER PRIMARY KEY AUTOINCREMENT, prestado_a2 VARCHAR, nombre_objeto2 VARCHAR, fecha_prestamo2 date, fecha_devolucion2 date, tipo_objeto2 varchar, fecha_recuperacion date );");


        Calendar c = Calendar.getInstance();
        año_actual = c.get(Calendar.YEAR);
        mes_actual = c.get(Calendar.MONTH)+1;
        dia_actual = c.get(Calendar.DAY_OF_MONTH);

        final String fecha_sistema = año_actual + "/" + mes_actual + "/" + dia_actual;

        txt_fechaprestamo.setText(año_actual + "/" + mes_actual + "/" + dia_actual);


        db.execSQL("drop view if exists vista_alerta ");
        db.execSQL("create view vista_alerta as select prestado_a, nombre_objeto, fecha_devolucion from prestamos where fecha_devolucion <('" + fecha_sistema + "');");



        Calendar fecha1 = Calendar.getInstance();
        año1 = fecha1.get(Calendar.YEAR);
        mes1 = fecha1.get(Calendar.MONTH);
        dia1 = fecha1.get(Calendar.DAY_OF_MONTH);

        al = new ArrayList<String>();
        a2 = new ArrayList<String>();

        ArrayAdapter<String> arrayPendientes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al);
        ArrayAdapter<String> arrayDevueltos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, a2);

        prestamosPendientes.setAdapter(arrayPendientes);
        prestamosDevueltos.setAdapter(arrayDevueltos);

        prestamosPendientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String p1 = parent.getItemAtPosition(position).toString();
                String[] apuntador1 = p1.split("'");

                db.execSQL("INSERT INTO devueltos (prestado_a2, nombre_objeto2, fecha_prestamo2, fecha_devolucion2, tipo_objeto2) select prestado_a, nombre_objeto, fecha_prestamo, fecha_devolucion, tipo_objeto from prestamos where prestado_a='" + apuntador1[1] + "';");
                db.execSQL("UPDATE devueltos SET fecha_recuperacion = '" + fecha_sistema + "' WHERE prestado_a2 = '"+ apuntador1[1] + "';");
                db.execSQL("DELETE from prestamos where prestado_a='" + apuntador1[1] + "';");


                al.remove(position);
                prestamosPendientes.invalidateViews();

                Cursor k = db.rawQuery("SELECT * FROM prestamos", null);
                if (k.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "No hay préstamos registrados", Toast.LENGTH_SHORT).show();

                }


                prestamosPendientes.invalidateViews();

            }
        });


        prestamosDevueltos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String p2 = parent.getItemAtPosition(position).toString();
                String[] apuntador2 = p2.split("'");

                db.execSQL("INSERT INTO prestamos (prestado_a, nombre_objeto, fecha_prestamo, fecha_devolucion, tipo_objeto) select prestado_a2, nombre_objeto2, fecha_prestamo2, fecha_devolucion2, tipo_objeto2 from devueltos where prestado_a2='" + apuntador2[1] + "';");
                db.execSQL("DELETE from devueltos where prestado_a2='" + apuntador2[1] + "';");


                a2.remove(position);
                Cursor k = db.rawQuery("SELECT * FROM devueltos", null);
                if (k.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "No hay préstamos devueltos", Toast.LENGTH_SHORT).show();

                }

                prestamosDevueltos.invalidateViews();


            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre_objeto = fld_nombreobjeto.getText().toString();
                String nombre_persona = fld_prestadoa.getText().toString();
                String fecha_devolucion = txt_fechadevolucion.getText().toString();
                String fecha_prestamo = txt_fechaprestamo.getText().toString();
                String tipo_obj = fldtipo.getText().toString();
                try {
                    if (nombre_objeto.equals("") || nombre_persona.equals("") || fecha_devolucion.equals("") || fecha_prestamo.equals("") || tipo_obj.equals("")) {
                        Toast.makeText(MainActivity.this, "Debe llenar todos los campos", Toast.LENGTH_LONG).show();

                    } else {

                        db.execSQL("INSERT INTO prestamos(prestado_a, nombre_objeto, fecha_prestamo, fecha_devolucion, tipo_objeto) VALUES ('" + fld_prestadoa.getText().toString().trim() + "','" + fld_nombreobjeto.getText().toString().trim() +
                                "','" + txt_fechaprestamo.getText().toString().trim() + "','" + txt_fechadevolucion.getText().toString().trim() + "','" + fldtipo.getText().toString().trim() + "');");
                        Toast.makeText(MainActivity.this, "Préstamo registrado", Toast.LENGTH_SHORT).show();
                        borrar();


                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Ocurrió un error. Intentelo de nuevo.", Toast.LENGTH_LONG).show();

                }


            }


        });


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Cursor alerta = db.rawQuery("select * from vista_alerta;", null);
                if (tabId == "tab2") {
                    if (alerta.getCount() == 0) {

                    } else {

                        StringBuffer fasd = new StringBuffer();
                        while (alerta.moveToNext()) {
                            fasd.append("Prestado a: " + alerta.getString(0) + "\nObjeto prestado: " + alerta.getString(1) + "\nDebió ser devuelto el dia: " + alerta.getString(2) + "\n");

                        }
                        showMessage("Préstamos que deben ser devueltos:", fasd.toString());

                    }
                    al.removeAll(al);
                    Cursor c = db.rawQuery("SELECT * FROM prestamos", null);
                    if (c.getCount() == 0) {
                        Toast.makeText(MainActivity.this, "No hay registros", Toast.LENGTH_SHORT).show();

                    }


                    while (c.moveToNext()) {

                        al.add("Prestado a: '" + c.getString(1) + "'\nObjeto prestado: " + c.getString(2) + "\nFecha del préstamo: " + c.getString(3) + "\nSe debe devolver el día: " + c.getString(4) + "\nTipo de objeto: " + c.getString(5));
                    }
                    prestamosPendientes.invalidateViews();

                }

                if (tabId == "tab3") {
                    a2.removeAll(a2);
                    Cursor a = db.rawQuery("SELECT * FROM devueltos", null);
                    if (a.getCount() == 0) {
                        Toast.makeText(MainActivity.this, "No hay registros", Toast.LENGTH_SHORT).show();

                    }


                    while (a.moveToNext()) {

                        a2.add("Prestado a: '" + a.getString(1) + "'\nObjeto prestado: " + a.getString(2) + "\nFecha del préstamo: " + a.getString(3) + "\nSe debe devolver el día: " + a.getString(4) + "\nTipo de objeto: " + a.getString(5) + "\nDevuelto el dia: " + fecha_sistema);
                    }
                    prestamosDevueltos.invalidateViews();

                }

            }
        });

        showDialogOnButtonClick();
        mostrarFechaDev();

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DEV) {
            return new DatePickerDialog(this, picker_fechadevolucion, año1, mes1, dia1);
        }

        if (id == DIALOG_ID) {
            return new DatePickerDialog(this, picker_fechaprestamo, año1, mes1, dia1);
        }


        return null;
    }


    public DatePickerDialog.OnDateSetListener picker_fechaprestamo = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            txt_fechaprestamo = (TextView) findViewById(R.id.lblFechaPrIn);
            año1 = year;
            mes1 = monthOfYear + 1;
            dia1 = dayOfMonth;


            txt_fechaprestamo.setText(año1 + "/" + mes1 + "/" + dia1);


        }
    };


    public DatePickerDialog.OnDateSetListener picker_fechadevolucion = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            txt_fechadevolucion = (TextView) findViewById(R.id.lblFechaDevIn);
            año2 = year;
            mes2 = monthOfYear + 1;
            dia2 = dayOfMonth;


            txt_fechadevolucion.setText(año2 + "/" + mes2 + "/" + dia2);


        }
    };

    public void showDialogOnButtonClick() {
        btn_fechaprestamo = (Button) findViewById(R.id.btnEditarPre);

        btn_fechaprestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });


    }

    public void mostrarFechaDev() {
        btn_fechadevolucion = (Button) findViewById(R.id.btnEditarDev);

        btn_fechadevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DEV);
            }
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void borrar() {
        fld_prestadoa.setText("");
        fld_nombreobjeto.setText("");
        txt_fechaprestamo.setText("");
        txt_fechadevolucion.setText("");
        fldtipo.setText("");
    }

}