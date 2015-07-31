package com.hanry;

import java.net.URL;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.ListPreference;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.hanry.Constant;
import com.hanry.R;
import com.hanry.Constant.CommandArray;

public class WifiCarSettings extends PreferenceActivity implements OnSharedPreferenceChangeListener {  
	
	private EditTextPreference mPrefRouterUrl;
	private EditTextPreference mPrefCameraUrl;
	private EditTextPreference mPrefRouterUrlTest;
	private EditTextPreference mPrefCameraUrlTest;
	
	private EditTextPreference mPrefLenOn;
	private EditTextPreference mPrefLenOff;

	private EditTextPreference mPrefAvanzar;
	private EditTextPreference mPrefRetroceder;
	private EditTextPreference mPrefIzquierda;
	private EditTextPreference mPrefDerecha;
	private EditTextPreference mPrefParar;

	private EditTextPreference txtPotenciaMotor;
	private EditTextPreference txtCorreccionMotorIzquiedo;
	private EditTextPreference txtCorreccionMotorDerecho;
	private EditTextPreference txtPotenciaGiro;

	private EditTextPreference txtCommandStart;
	private EditTextPreference txtCommandPotenciaMotor;
	private EditTextPreference txtCommandCorreccionMotorIzquiedo;
	private EditTextPreference txtCommandCorreccionMotorDerecho;
	private EditTextPreference txtCommandPotenciaGiro;
	private EditTextPreference txtCommandServoVertical;
	private EditTextPreference txtCommandServoHorizontal;

	private ListPreference listGearStep;

	@Override  
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		// SharePreferences
		addPreferencesFromResource(R.xml.wifi_car_settings);
		
		
		mPrefRouterUrl = (EditTextPreference)findPreference(Constant.PREF_KEY_ROUTER_URL);
		mPrefCameraUrl = (EditTextPreference)findPreference(Constant.PREF_KEY_CAMERA_URL);
		mPrefRouterUrlTest = (EditTextPreference)findPreference(Constant.PREF_KEY_ROUTER_URL_TEST);
		mPrefCameraUrlTest = (EditTextPreference)findPreference(Constant.PREF_KEY_CAMERA_URL_TEST);
		
		mPrefLenOn = (EditTextPreference)findPreference(Constant.PREF_KEY_LEN_ON);
		mPrefLenOff = (EditTextPreference)findPreference(Constant.PREF_KEY_LEN_OFF);

		mPrefAvanzar = (EditTextPreference)findPreference(Constant.PREF_KEY_AVANZAR);
		mPrefRetroceder = (EditTextPreference)findPreference(Constant.PREF_KEY_RETROCEDER);
		mPrefIzquierda = (EditTextPreference)findPreference(Constant.PREF_KEY_IZQUIERDA);
		mPrefDerecha = (EditTextPreference)findPreference(Constant.PREF_KEY_DERECHA);
		mPrefParar = (EditTextPreference)findPreference(Constant.PREF_KEY_PARAR);

		txtPotenciaMotor = (EditTextPreference)findPreference(Constant.PREF_KEY_POTENCIA);
		txtCorreccionMotorDerecho = (EditTextPreference)findPreference(Constant.PREF_KEY_POTENCIA_DERECHO);
		txtCorreccionMotorIzquiedo = (EditTextPreference)findPreference(Constant.PREF_KEY_POTENCIA_IZQUIERDO);
		txtPotenciaGiro = (EditTextPreference)findPreference(Constant.PREF_KEY_POTENCIA_GIRO);

		txtCommandStart = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_START);
		txtCommandCorreccionMotorDerecho = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_POTENCIA_DERECHO);
		txtCommandCorreccionMotorIzquiedo = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_POTENCIA_IZQUIERDO);
		txtCommandPotenciaGiro = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_POTENCIA_GIRO);
		txtCommandPotenciaMotor = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_POTENCIA);
		txtCommandServoHorizontal = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_SERVO_HORIZONTAL);
		txtCommandServoVertical = (EditTextPreference)findPreference(Constant.PREF_KEY_COMMAND_SERVO_VERTICAL);

		listGearStep = (ListPreference)findPreference(Constant.PREF_KEY_GEAR_STEP);

		initValue();
	}  
	 
	 void initValue(){
		 
		 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		 String CameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL, Constant.DEFAULT_VALUE_CAMERA_URL);
		 mPrefCameraUrl.setSummary(CameraUrl);
		 
		 String RouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL, Constant.DEFAULT_VALUE_ROUTER_URL);
		 mPrefRouterUrl.setSummary(RouterUrl);
		 
		 
		 String testCameraUrl = settings.getString(Constant.PREF_KEY_CAMERA_URL_TEST, Constant.DEFAULT_VALUE_CAMERA_URL_TEST);
		 mPrefCameraUrlTest.setSummary(testCameraUrl);
		 
		 String testRouterUrl = settings.getString(Constant.PREF_KEY_ROUTER_URL_TEST, Constant.DEFAULT_VALUE_ROUTER_URL_TEST);
		 mPrefRouterUrlTest.setSummary(testRouterUrl);
		 
		 String lenon = settings.getString(Constant.PREF_KEY_LEN_ON, Constant.DEFAULT_VALUE_LEN_ON);
		 mPrefLenOn.setSummary(lenon);
		 
		 String lenoff = settings.getString(Constant.PREF_KEY_LEN_OFF, Constant.DEFAULT_VALUE_LEN_OFF);
		 mPrefLenOff.setSummary(lenoff);


		 String cmdAvanzar = settings.getString(Constant.PREF_KEY_AVANZAR, Constant.DEFAULT_VALUE_AVANZAR);
		 mPrefAvanzar.setSummary(cmdAvanzar);
		 String cmdRetroceder = settings.getString(Constant.PREF_KEY_RETROCEDER, Constant.DEFAULT_VALUE_RETROCEDER);
		 mPrefRetroceder.setSummary(cmdRetroceder);
		 String cmdIzquierda = settings.getString(Constant.PREF_KEY_IZQUIERDA, Constant.DEFAULT_VALUE_IZQUIERDA);
		 mPrefIzquierda.setSummary(cmdIzquierda);
		 String cmdDerecha = settings.getString(Constant.PREF_KEY_DERECHA, Constant.DEFAULT_VALUE_DERECHA);
		 mPrefDerecha.setSummary(cmdDerecha);
		 String cmdParar = settings.getString(Constant.PREF_KEY_PARAR, Constant.DEFAULT_VALUE_PARAR);
		 mPrefParar.setSummary(cmdParar);

		 String potenciaMotor = settings.getString(Constant.PREF_KEY_POTENCIA, Constant.DEFAULT_VALUE_POTENCIA);
		 txtPotenciaMotor.setSummary(potenciaMotor);
		 String correccionMotorDerecho = settings.getString(Constant.PREF_KEY_POTENCIA_DERECHO, Constant.DEFAULT_VALUE_POTENCIA_DERECHO);
		 txtCorreccionMotorDerecho.setSummary(correccionMotorDerecho);
		 String correccionMotorIzquierdo = settings.getString(Constant.PREF_KEY_POTENCIA_IZQUIERDO, Constant.DEFAULT_VALUE_POTENCIA_IZQUIERDO);
		 txtCorreccionMotorIzquiedo.setSummary(correccionMotorIzquierdo);
		 String potenciaGiro = settings.getString(Constant.PREF_KEY_POTENCIA_GIRO, Constant.DEFAULT_VALUE_POTENCIA_GIRO);
		 txtPotenciaGiro.setSummary(potenciaGiro);

		 String cmdStart = settings.getString(Constant.PREF_KEY_COMMAND_START, Constant.DEFAULT_VALUE_COMMAND_START);
		 txtCommandStart.setSummary(cmdStart);
		 String cmdCorreccionDerecho = settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_DERECHO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_DERECHO);
		 txtCommandCorreccionMotorDerecho.setSummary(cmdCorreccionDerecho);
		 String cmdCorreccionIzquiedo = settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_IZQUIERDO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_IZQUIERDO);
		 txtCommandCorreccionMotorIzquiedo.setSummary(cmdCorreccionIzquiedo);
		 String cmdPotenciaGiro = settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA_GIRO, Constant.DEFAULT_VALUE_COMMAND_POTENCIA_GIRO);
		 txtCommandPotenciaGiro.setSummary(cmdPotenciaGiro);
		 String cmdPotenciaMotor = settings.getString(Constant.PREF_KEY_COMMAND_POTENCIA, Constant.DEFAULT_VALUE_COMMAND_POTENCIA);
		 txtCommandPotenciaMotor.setSummary(cmdPotenciaMotor);
		 String cmdServoHorizontal = settings.getString(Constant.PREF_KEY_COMMAND_SERVO_HORIZONTAL, Constant.DEFAULT_VALUE_COMMAND_SERVO_HORIZONTAL);
		 txtCommandServoHorizontal.setSummary(cmdServoHorizontal);
		 String cmdServoVertical = settings.getString(Constant.PREF_KEY_COMMAND_SERVO_VERTICAL, Constant.DEFAULT_VALUE_COMMAND_SERVO_VERTICAL);
		 txtCommandServoVertical.setSummary(cmdServoVertical);

		 String gearStep =  settings.getString(Constant.PREF_KEY_GEAR_STEP, Constant.DEFAULT_VALUE_GEAR_STEP);
		 listGearStep.setSummary(gearStep);
	 }
	 
    @Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
 
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		Preference pref = findPreference(key);
	    if (pref instanceof EditTextPreference) {
	        EditTextPreference etp = (EditTextPreference) pref;
			/*
	        if (etp == mPrefLenOn || etp == mPrefLenOff) {
	        	String comm = etp.getText();
	        	CommandArray cmd = new CommandArray(comm);
				if (!cmd.isValid()) {
                    Toast.makeText(this, "Comando innvalido", Toast.LENGTH_SHORT).show();
                }
			}
			*/
	        etp.setSummary(etp.getText());
	    }else if (pref instanceof ListPreference){
			ListPreference listPreference = (ListPreference) pref;
			listPreference.setSummary(listPreference.getValue());
		}
	}
}
