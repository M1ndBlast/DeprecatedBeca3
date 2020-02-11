package org.no_ip.payan.metro3;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.no_ip.payan.metro3.clases.Clave;
import org.no_ip.payan.metro3.clases.MCReader;
import org.no_ip.payan.metro3.clases.Tarjeta;
import org.no_ip.payan.metro3.clases.util;

public class MainActivity extends AppCompatActivity {
    private Intent mOldIntent = null;
    private boolean mResume = true;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ((FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, nuevaTarjeta.class));
            }
        });
        util.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
        if (util.getNfcAdapter() == null) {
            this.mResume = false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        util.disableNfcForegroundDispatch(this);
    }

    private void checkNfc() {
        if (util.getNfcAdapter() == null || util.getNfcAdapter().isEnabled()) {
            if (this.mOldIntent != getIntent()) {
                int typeCheck = util.treatAsNewTag(getIntent(), this);
                if (typeCheck == -1 || typeCheck != -2) {
                }
                this.mOldIntent = getIntent();
            }
            util.enableNfcForegroundDispatch(this);
        }
    }

    public void onNewIntent(Intent intent) {
        int treatAsNewTag = util.treatAsNewTag(intent, this);
        MCReader algo = MCReader.get(util.getTag());
        boolean sePudo = false;
        int i = 0;
        while (true) {
            if (i >= Clave.cuantas) {
                break;
            }
            if (algo.isConnected()) {
                algo.close();
            }
            if (algo.authenticate2(0, util.hexStringToByteArray(Clave.claves[i][0]), true)) {
                Tarjeta.tipo = i;
                startActivity(new Intent(this, leerTarjeta.class));
                sePudo = true;
                algo.close();
                break;
            }
            i++;
        }
        if (!sePudo) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle((CharSequence) "SELECCIONE UN TIPO DE TARJETA");
            builder.setItems(new CharSequence[]{"METRO", "SUBURBANO"}, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(MainActivity.this, nuevaTarjeta.class);
                    Tarjeta.tipo = which;
                    i.putExtra("cual", which);
                    MainActivity.this.startActivity(i);
                }
            });
            builder.show();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mResume) {
            System.out.println("ME ACVTIVARON");
            if (Tarjeta.sectores[0][0] != null) {
                startActivity(new Intent(this, datosTarjeta.class));
            } else {
                checkNfc();
            }
        }
    }
}
