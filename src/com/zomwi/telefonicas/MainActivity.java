package com.zomwi.telefonicas;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ImageButton botonOk;
	private TextView textoSaldo;
	private ImageButton botonPrestamo;
	private ImageButton botonInternetTotal;

	// Constants
	String SMS_ENVIADO = "SMS_ENVIADO";

	String SMS_ENTREGADO = "SMS_ENTREGADO";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/roboto_thin.ttf");
		textoSaldo = (TextView) findViewById(R.id.textoSaldo);
		textoSaldo.setTypeface(font);
		textoSaldo.setTextSize(50f);
		if (getIntent().getExtras() != null) {
			textoSaldo.setText(getIntent().getStringExtra("saldo"));
		}

		botonOk = (ImageButton) findViewById(R.id.imageButtonSaldo);
		botonOk.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				enviarSMS("174", "saldo");
			}
		});
		/*
		 * SMSReceiver = new BroadcastReceiver() {
		 * 
		 * @Override public void onReceive(Context context, Intent intent) {
		 * Bundle bundle = intent.getExtras(); Object[] pdus = (Object[])
		 * bundle.get("pdus"); SmsMessage mensaje =
		 * SmsMessage.createFromPdu((byte[]) pdus[0]); String remitente =
		 * mensaje.getOriginatingAddress(); String cuerpo =
		 * mensaje.getMessageBody().toString(); Toast.makeText(context,
		 * remitente + "\n\n" + cuerpo, Toast.LENGTH_LONG).show(); } };
		 * registerReceiver(SMSReceiver, new IntentFilter("SMS"));
		 */

		botonPrestamo = (ImageButton) findViewById(R.id.imageButtonPrestar);
		botonPrestamo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Intent intent = new Intent(Intent.ACTION_CALL);
				String encodedHash = Uri.encode("#");
				String ussd = "*" + "222" + encodedHash;
				startActivityForResult(
						new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)),
						1);

			}
		});

		botonInternetTotal = (ImageButton) findViewById(R.id.imageButtonInternet);
		botonInternetTotal.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				enviarSMS("5599", "si");

			}
		});

	}

	protected void enviarSMS(String numeroTelefonico, String mensaje) {
		PendingIntent pendingIntentEnviado = PendingIntent.getBroadcast(this,
				0, new Intent(SMS_ENVIADO), 0);
		PendingIntent pendingIntentEntregado = PendingIntent.getBroadcast(this,
				0, new Intent(SMS_ENTREGADO), 0);

		// Cuando el SMS es enviado
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					textoSaldo.setText("Enviado.");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					textoSaldo.setText("Fallo general.");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					textoSaldo.setText("Sin servicio.");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					textoSaldo.setText("PDU nulo.");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					textoSaldo.setText("Radio apagado.");
					break;

				}
			}

		}, new IntentFilter(SMS_ENVIADO));

		// Cuando esta por entregarse
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					textoSaldo.setText("Entregado.");
					MainActivity.this.finish();
					break;

				case Activity.RESULT_CANCELED:
					textoSaldo.setText("No entregado.");
					break;
				}
			}

		}, new IntentFilter(SMS_ENTREGADO));

		// Enviar el SMS
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(numeroTelefonico, null, mensaje,
				pendingIntentEnviado, pendingIntentEntregado);

	}

}
