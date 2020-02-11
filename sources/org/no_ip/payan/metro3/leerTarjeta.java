package org.no_ip.payan.metro3;

import android.content.Intent;
import android.nfc.TagLostException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.no_ip.payan.metro3.clases.Clave;
import org.no_ip.payan.metro3.clases.MCReader;
import org.no_ip.payan.metro3.clases.Tarjeta;
import org.no_ip.payan.metro3.clases.util;

public class leerTarjeta extends AppCompatActivity {
    MCReader algo;
    TextView lblEstado;
    ProgressBar pb;

    private class leer extends AsyncTask<Integer, Void, Integer> {
        private leer() {
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(Integer... params) {
            try {
                if (leerTarjeta.this.algo.isConnected()) {
                    leerTarjeta.this.algo.close();
                }
                Tarjeta.sectores[params[0].intValue()] = leerTarjeta.this.algo.readSector(params[0].intValue(), util.hexStringToByteArray(Clave.claves[Tarjeta.tipo][params[0].intValue()]), true);
            } catch (TagLostException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Integer returno) {
            if (returno.intValue() + 1 <= 15) {
                Integer returno2 = Integer.valueOf(returno.intValue() + 1);
                leerTarjeta.this.lblEstado.setText("LEYENDO SECTOR " + returno2);
                leerTarjeta.this.pb.setProgress((returno2.intValue() * 100) / 15);
                new leer().execute(new Integer[]{returno2});
                return;
            }
            leerTarjeta.this.startActivity(new Intent(leerTarjeta.this, datosTarjeta.class));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_leer_tarjeta);
        setTitle("LEYENDO TARJETA");
        this.lblEstado = (TextView) findViewById(R.id.lblEstado);
        this.pb = (ProgressBar) findViewById(R.id.progressBar3);
        try {
            this.algo = MCReader.get(util.getTag());
        } catch (Exception e) {
            Toast.makeText(this, "ERROR AL PROCESAR LA TARJETA", 1);
        }
        new leer().execute(new Integer[]{0});
    }
}
