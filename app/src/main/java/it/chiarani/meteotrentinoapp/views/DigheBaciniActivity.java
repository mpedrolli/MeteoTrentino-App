package it.chiarani.meteotrentinoapp.views;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.chiarani.meteotrentinoapp.R;
import it.chiarani.meteotrentinoapp.adapters.BaciniAdapter;
import it.chiarani.meteotrentinoapp.adapters.BulletProbDaysAdapter;
import it.chiarani.meteotrentinoapp.adapters.SensorAdapter;
import it.chiarani.meteotrentinoapp.api.API_bacini;
import it.chiarani.meteotrentinoapp.api.API_bacini_response;
import it.chiarani.meteotrentinoapp.databinding.ActivityDigheBaciniBinding;
import it.chiarani.meteotrentinoapp.helper.CustomDialog;
import it.chiarani.meteotrentinoapp.helper.JSONUtilities;
import it.chiarani.meteotrentinoapp.helper.WeatherStation;
import it.chiarani.meteotrentinoapp.models.SensoreBacini;
import it.chiarani.meteotrentinoapp.models.StazioniBacini;

public class DigheBaciniActivity extends SampleActivity implements SensorAdapter.ClickListener, API_bacini_response {

    ActivityDigheBaciniBinding binding;
    JSONObject jsonObject;
    List<String> bacini;
    List<String> stazioni;
    List<SensoreBacini> sensori_for_adapter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_dighe_bacini;
    }

    @Override
    protected void setActivityBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutID());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomDialog cdd = new CustomDialog(DigheBaciniActivity.this, "Attenzione!\nQuesta scheda è ancora in fase di sviluppo, non tutti i bacini e stazioni sono attualmente presenti.");
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();

        binding.fragmentRadarDayBtnMenu.setOnClickListener(v -> onBackPressed());

        try {
            jsonObject = new JSONObject(JSONUtilities.loadJSONFromAsset(this, "bacini.json"));
            bacini = new ArrayList<>();
            bacini.add("brenta");
            bacini.add("agno");
            bacini.add("piave");
            bacini.add("livenza");
            bacini.add("monticano");
            bacini.add("scolante laguna");
            bacini.add("adige");
            bacini.add("po/mincio");
            bacini.add("brenta/cismon");

            // ---- ---- ----
            // SPINNER BACINI
            // ---- ---- ----
            ArrayAdapter<String> adapter_bacini = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bacini);
            adapter_bacini.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.activityDigheBaciniSpinnerBacini.setAdapter(adapter_bacini);
            binding.activityDigheBaciniSpinnerBacini.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position_bacini, long id) {
                    try {
                        JSONArray brenta_arr = jsonObject.getJSONObject(bacini.get(position_bacini)).getJSONArray("stazioni");

                        stazioni = new ArrayList<>();
                        for(int i = 0; i < brenta_arr.length(); i++){
                            stazioni.add(brenta_arr.getJSONObject(i).optString("nome_stazione"));
                        }

                        // ---- ---- ------
                        // SPINNER STAZIONI
                        // ---- ---- ------
                        ArrayAdapter<String> adapter_stazioni = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, stazioni);
                        adapter_stazioni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.activityDigheBaciniSpinnerStazione.setAdapter(adapter_stazioni);
                        binding.activityDigheBaciniSpinnerStazione.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position_stazioni, long id) {
                                try {
                                    List<SensoreBacini> sensoriBacini = new ArrayList<>();

                                    // pluviometro
                                    SensoreBacini pluviometro = new SensoreBacini();
                                    pluviometro.setNome_sensore("pluviometro");
                                    pluviometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("pluviometro").
                                                    optString("api_pluviometro")
                                    );
                                    pluviometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("pluviometro").
                                                    optString("id_sensore")
                                    );

                                    // idrometro
                                    SensoreBacini idrometro = new SensoreBacini();
                                    idrometro.setNome_sensore("idrometro");
                                    idrometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("idrometro").
                                                    optString("api_idrometro")
                                    );
                                    idrometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("idrometro").
                                                    optString("id_sensore")
                                    );

                                    // temp aria
                                    SensoreBacini temperatura_aria = new SensoreBacini();
                                    temperatura_aria.setNome_sensore("temperatura_aria");
                                    temperatura_aria.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("temperatura_aria").
                                                    optString("api_temperatura_aria")
                                    );
                                    temperatura_aria.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("temperatura_aria").
                                                    optString("id_sensore")
                                    );

                                    // igrometro
                                    SensoreBacini igrometro = new SensoreBacini();
                                    igrometro.setNome_sensore("igrometro");
                                    igrometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("igrometro").
                                                    optString("api_igrometro")
                                    );
                                    igrometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("igrometro").
                                                    optString("id_sensore")
                                    );

                                    // nivometro
                                    SensoreBacini nivometro = new SensoreBacini();
                                    nivometro.setNome_sensore("nivometro");
                                    nivometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("nivometro").
                                                    optString("api_nivometro")
                                    );
                                    nivometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("nivometro").
                                                    optString("id_sensore")
                                    );

                                    // barometro
                                    SensoreBacini barometro = new SensoreBacini();
                                    barometro.setNome_sensore("barometro");
                                    barometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("barometro").
                                                    optString("api_barometro")
                                    );
                                    barometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("barometro").
                                                    optString("id_sensore")
                                    );

                                    // radiometro
                                    SensoreBacini radiometro = new SensoreBacini();
                                    radiometro.setNome_sensore("radiometro");
                                    radiometro.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("radiometro").
                                                    optString("api_radiometro")
                                    );
                                    radiometro.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("radiometro").
                                                    optString("id_sensore")
                                    );

                                    // vento
                                    SensoreBacini vento = new SensoreBacini();
                                    vento.setNome_sensore("vento");
                                    vento.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("vento").
                                                    optString("api_vento")
                                    );
                                    vento.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("vento").
                                                    optString("id_sensore")
                                    );

                                    // direzione_vento
                                    SensoreBacini direzione_vento = new SensoreBacini();
                                    direzione_vento.setNome_sensore("direzione_vento");
                                    direzione_vento.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("direzione_vento").
                                                    optString("api_direzione_vento")
                                    );
                                    direzione_vento.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("direzione_vento").
                                                    optString("id_radiometro")
                                    );

                                    // portata
                                    SensoreBacini portata = new SensoreBacini();
                                    portata.setNome_sensore("portata");
                                    portata.setApi_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("portata").
                                                    optString("api_portata")
                                    );
                                    portata.setId_sensore(
                                            jsonObject.getJSONObject(bacini.get(position_bacini)).
                                                    getJSONArray("stazioni").
                                                    getJSONObject(position_stazioni).
                                                    getJSONObject("sensori").
                                                    getJSONObject("portata").
                                                    optString("id_radiometro")
                                    );

                                    sensoriBacini.add(pluviometro);
                                    sensoriBacini.add(idrometro);
                                    sensoriBacini.add(temperatura_aria);
                                    sensoriBacini.add(igrometro);
                                    sensoriBacini.add(nivometro);
                                    sensoriBacini.add(barometro);
                                    sensoriBacini.add(radiometro);
                                    sensoriBacini.add(vento);
                                    sensoriBacini.add(direzione_vento);
                                    sensoriBacini.add(portata);

                                    binding.activityDigheBaciniRvSensore.setHasFixedSize(true);

                                    LinearLayoutManager linearLayoutManagerslot1 = new LinearLayoutManager(getApplicationContext());
                                    linearLayoutManagerslot1.setOrientation(LinearLayoutManager.HORIZONTAL);
                                    binding.activityDigheBaciniRvSensore.setLayoutManager(linearLayoutManagerslot1);

                                    sensori_for_adapter = new ArrayList<>();
                                    for(SensoreBacini sensore : sensoriBacini)
                                    {
                                        if(!sensore.getApi_sensore().isEmpty())
                                        {
                                            sensori_for_adapter.add(sensore);
                                        }
                                    }

                                    StazioniBacini stazioniBacini = new StazioniBacini();
                                    stazioniBacini.setSensori(sensori_for_adapter);
                                    SensorAdapter sensorAdapter = new SensorAdapter(getApplicationContext(), stazioniBacini, DigheBaciniActivity.this::onClick, 0);
                                    binding.activityDigheBaciniRvSensore.setAdapter(sensorAdapter);

                                    doBounceAnimation(binding.activityDigheBaciniRvSensore);
                                }
                                catch (Exception ex) {
                                    String x = ex.getMessage();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    catch (Exception ex) {
                       String x = ex.getMessage();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    int posx = -1;
    @Override
    public void onClick(int day, int position) {
        posx = position;
        API_bacini api_bacini = new API_bacini(this::processFinish, sensori_for_adapter.get(position).getApi_sensore());
        api_bacini.execute();
    }

    @Override
    public void processFinish(String data) {
        String[] k = data.split("\n");
        List<String> list_data = new ArrayList<>();
        String unitamisura = "";
        String[] x = Arrays.copyOfRange(k, 6, k.length-1);
        for(String s : x) {
            try {
                String day   = s.split("/")[0] + "/" + s.split("/")[0] + " " + s.split(" ")[1].substring(0, 5);

                switch (sensori_for_adapter.get(posx).getNome_sensore()) {
                    case "pluviometro": unitamisura = " mm/h"; break;
                    case "idrometro": unitamisura = " m"; break; //
                    case "temperatura_aria": unitamisura = " °C"; break;
                    case "igrometro": unitamisura = " %"; break;
                    case "nivometro": unitamisura = " cm"; break;
                    case "barometro": unitamisura = " hPa"; break;
                    case "radiometro": unitamisura = " W/mq"; break;
                    case "vento": unitamisura = " m/s"; break;
                    case "direzione_vento": unitamisura = " °"; break;
                    case "portata": unitamisura = " mc/s"; break;
                }

                String value = s.split(";")[1] + unitamisura;
                list_data.add(day + ";" + value);
            }
            catch (StringIndexOutOfBoundsException ex) {
            }
            catch (Exception e) {

            }
        }

        binding.activityDigheBaciniRvDati.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerslot1 = new LinearLayoutManager(getApplicationContext());
        linearLayoutManagerslot1.setOrientation(LinearLayoutManager.VERTICAL);
        binding.activityDigheBaciniRvDati.setLayoutManager(linearLayoutManagerslot1);
        BaciniAdapter baciniAdapter = new BaciniAdapter(list_data);
        binding.activityDigheBaciniRvDati.setAdapter(baciniAdapter);

    }

    private void doBounceAnimation(View targetView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationX", 0, 30, 0);
        animator.setInterpolator(new EasingInterpolator(Ease.ELASTIC_IN_OUT));
        animator.setStartDelay(500);
        animator.setDuration(1500);
        animator.start();
    }
}
