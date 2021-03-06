package it.chiarani.meteotrentinoapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import it.chiarani.meteotrentinoapp.R;
import it.chiarani.meteotrentinoapp.repositories.WeatherReportRepository;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

  public final static String PREF_FIRST_POS = "first_pos";
  public final static String PREF_SECOND_POS = "second_pos";
  public Preference pref_key_notifica_mattina;
  public Preference pref_key_allerta;
  public Preference pref_key_privacy;
  public Preference pref_key_feedback;
  public Preference pref_version;
  public Preference pref_preferiti;
  public Preference pref_clean_cache;
  public Preference pref_key_contatti_t;
  public Preference pref_key_sito;
  public Preference pref_key_faq;
  public Preference pref_key_allor_gps;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pref_key_notifica_mattina = findPreference("pref_key_notifica_mattina");
    pref_key_privacy          = findPreference("pref_key_privacy");
    pref_key_feedback         = findPreference("pref_key_feedback");
    pref_version              = findPreference("pref_key_versione");
    pref_preferiti            = findPreference("pref_key_pulisci_preferiti");
    pref_clean_cache          = findPreference("pref_key_pulisci_database");
    pref_key_allerta          = findPreference("pref_key_notifica_allerta");
    pref_key_contatti_t       = findPreference("pref_key_feedback_telegram");
    pref_key_sito             = findPreference("pref_key_sito_web");
    pref_key_faq              = findPreference("pref_key_faq");
    pref_key_allor_gps        = findPreference("pref_key_gps");

    try {
      pref_version.setSummary(this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0).versionName);
    } catch(PackageManager.NameNotFoundException ex) { Log.e("Preferences", ex.getMessage());}

    pref_key_privacy  .setOnPreferenceClickListener(this);
    pref_key_feedback .setOnPreferenceClickListener(this);
    pref_version      .setOnPreferenceClickListener(this);
    pref_preferiti    .setOnPreferenceClickListener(this);
    pref_clean_cache  .setOnPreferenceClickListener(this);
    pref_key_contatti_t.setOnPreferenceClickListener(this);
    pref_key_sito     .setOnPreferenceClickListener(this);
    pref_key_faq      .setOnPreferenceClickListener(this);
  }

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public boolean onPreferenceClick(Preference preference) {
    switch (preference.getKey()) {
      case "pref_key_faq":
        Intent faqintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Xiryl/MeteoTrentino-App/blob/master/README.md#faq"));
        startActivity(faqintent);
        break;
      case "pref_key_sito_web":
        Intent sitointent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chiarani.it"));
        startActivity(sitointent);
        break;
      case "pref_key_feedback_telegram":
        Intent telegramintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Xiryl"));
        startActivity(telegramintent);
        break;
      case "pref_key_pulisci_preferiti":

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();

        e.putString(PREF_FIRST_POS, getContext().getResources().getString(R.string.first_pref));
        e.putString(PREF_SECOND_POS, getContext().getResources().getString(R.string.second_pref));

        //  Apply changes
        e.apply();
        Toast.makeText(getContext(), "Ok.", Toast.LENGTH_SHORT).show();
        break;
      case "pref_key_privacy":
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chiarani.it/meteotrentinoapp_privacy_policy.pdf"));
        startActivity(browserIntent);
        break;
      case "pref_key_feedback":
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:fabio@chiarani.it"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[METEOTRENTINO-APP]");
        startActivity(emailIntent);
        break;
      case "pref_key_versione":
        String appV = "";
        try {
          appV = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException ex) {Log.e("Preferences", ex.getMessage());}
        break;
      case "pref_key_pulisci_database":
        WeatherReportRepository repo = new WeatherReportRepository(getActivity().getApplication());
        repo.deleteAll();
        Toast.makeText(getContext(), "Ok.", Toast.LENGTH_SHORT).show();
        break;
    }

    return true;
  }

}